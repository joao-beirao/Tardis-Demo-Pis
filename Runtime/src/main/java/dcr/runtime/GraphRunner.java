package dcr.runtime;

import dcr.common.Environment;
import dcr.common.Record;
import dcr.common.data.computation.ComputationExpression;
import dcr.common.data.values.*;
import dcr.common.events.Event;
import dcr.common.events.userset.values.SetUnionVal;
import dcr.common.events.userset.values.UserVal;
import dcr.model.GraphElement;
import dcr.model.events.EventElement;
import dcr.model.relations.ControlFlowRelationElement;
import dcr.model.relations.SpawnRelationElement;
import dcr.runtime.communication.CommunicationLayer;
import dcr.runtime.elements.events.EventInstance;
import dcr.runtime.elements.events.LocallyInitiatedEventInstance;
import dcr.runtime.elements.events.RemotelyInitiatedEventInstance;
import dcr.runtime.monitoring.EventUpdate;
import dcr.runtime.monitoring.GraphObserver;
import dcr.runtime.monitoring.StateUpdate;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocols.application.requests.InformationFlowException;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class GraphRunner {

    private static final Logger logger = LogManager.getLogger(GraphRunner.class);

    private static final String SELF = "_@self";

    // global mapping indexed by uuid
    private final Map<String, EventInfo<? extends GenericEventInstance>> eventsByUuid;

    // ==
    // convenience mappings indexed by element_uid + uuid_extension (not unique across
    // maps)
    private final Map<String, EventInfo<ComputationInstance>> computationEvents;
    private final Map<String, EventInfo<InputInstance>> inputEvents;
    private final Map<String, EventInfo<ReceiveInstance>> receiveEvents;

    private final Map<EventInstance, List<ControlFlowRelationInfo>> controlFlowRelations;
    // ==
    // convenience mappings
    // -> (outgoing direction) relations for which the execution of the 'source'
    // event may affect the state of the 'target' event - indexed by 'source' event
    private final Map<EventInstance, List<ControlFlowRelationInfo>> includes;
    private final Map<EventInstance, List<ControlFlowRelationInfo>> excludes;
    private final Map<EventInstance, List<ControlFlowRelationInfo>> responses;
    // -> (incoming direction) - relations for which the 'source' event can actively
    // restrict the execution of the 'target' event - indexed by 'target' event
    private final Map<EventInstance, List<ControlFlowRelationInfo>> conditions;
    private final Map<EventInstance, List<ControlFlowRelationInfo>> milestones;
    // event ids mapped to spawn models
    private final Map<EventInstance, List<SpawnRelationInfo>> spawnRelations;

    // whoami
    public final UserVal self;
    private final CommunicationLayer communicationLayer;
    private final Collection<GraphObserver> graphObservers;

    private static <K, V> void addToListMapping(K key, V value, Map<K, List<V>> mapping) {
        List<V> values = mapping.getOrDefault(key, new LinkedList<>());
        if (values.isEmpty()) {
            mapping.put(key, values);
        }
        values.add(value);
    }

    private static String newEventUuidOf(String localId, String idExtension) {
        return "_" + localId + (idExtension.isBlank() ? "" : "_" + idExtension);
    }


    private static <V> void bindIfAbsent(Environment<V> env, String identifier, V value) {
        if (env.bindIfAbsent(identifier, value).isPresent()) {
            throw new IllegalStateException(
                    "Internal error: environment binding should not already exist (" +
                            identifier + ")");
        }
    }

    private static <V> V lookupPresent(Environment<V> env, String identifier) {
        return env.lookup(identifier).map(Environment.Binding::value).orElseThrow(
                () -> new IllegalStateException(
                        "Internal error: environment binding should not be missing (" +
                                identifier + ")"));
    }

    private static <V> void rebindPresent(String identifier, V newValue,
                                          Environment<V> env) {
        env.rebindIfPresent(identifier, newValue).orElseThrow(
                () -> new IllegalStateException(
                        "Internal error: environment binding should not be missing (" +
                                identifier + ")")).setValue(newValue);
    }

    // =====================================================================
    // Initialization / Init
    // =====================================================================

    public GraphRunner(UserVal self, CommunicationLayer communicationLayer) {
        this.self = self;
        this.communicationLayer = communicationLayer;
        this.graphObservers = new LinkedList<>();
        this.eventsByUuid = new HashMap<>();
        this.computationEvents = new HashMap<>();
        this.inputEvents = new HashMap<>();
        this.receiveEvents = new HashMap<>();
        this.controlFlowRelations = new HashMap<>();
        this.includes = new HashMap<>();
        this.excludes = new HashMap<>();
        this.responses = new HashMap<>();
        this.conditions = new HashMap<>();
        this.milestones = new HashMap<>();
        this.spawnRelations = new HashMap<>();
    }

    public void init(GraphElement graphElement) {
        // TODO [revisit] not really using the model after init
        List<StateUpdate> updates = new LinkedList<>();
        instantiateGraphElement(Objects.requireNonNull(graphElement),
                SpawnContext.init(this.self), "", updates);
        graphObservers.forEach(observer -> observer.onUpdate(updates));
    }

    public void registerGraphObserver(GraphObserver observer) {
        this.graphObservers.add(observer);
    }


    // =====================================================================
    // == Entry-points for event execution
    // =====================================================================


    /**
     * Executes the {@link dcr.common.events.ComputationEvent Computation Event}
     * identified by
     * {@code eventId}
     *
     * @param eventId the event's unique identifier in this graph
     */
    public void executeComputationEvent(String eventId) throws InformationFlowException {
        var info = requireNonNullEnabledEvent(computationEvents.get(eventId));
        assertIfcOK(info.event(), info.valueEnv());
        var event = info.event();
        Value recomputedValue = event.computationExpression().eval(info.valueEnv());
        List<StateUpdate> updates = new LinkedList<>();
        locallyUpdateOnEventExecution(info, recomputedValue, updates);
        onLocallyInitiatedEvent(event, info.evalContext, updates);
        graphObservers.forEach(observer -> observer.onUpdate(updates));
    }

    /**
     * Executes the {@link dcr.common.events.InputEvent Input Event} identified by
     * {@code eventId },
     * with {@code inputValue} as the expected input.
     *
     * @param eventId    the event's unique identifier in this graph
     * @param inputValue the event's updated value
     */
    public void executeInputEvent(String eventId, Value inputValue)
            throws InformationFlowException {
        var info = requireNonNullEnabledEvent(inputEvents.get(eventId));
        assertAdmissibleValueType(info.event, inputValue);
        assertIfcOK(info.event(), info.valueEnv());
        List<StateUpdate> updates = new LinkedList<>();
        locallyUpdateOnEventExecution(info, inputValue, updates);
        onLocallyInitiatedEvent(info.event(), info.evalContext, updates);
        graphObservers.forEach(observer -> observer.onUpdate(updates));
    }

    /**
     * Executes the {@link dcr.common.events.InputEvent Input Event} identified by
     * {@code eventId }
     * (no input value expected).
     *
     * @param eventId the event's unique identifier in this graph
     */
    public void executeInputEvent(String eventId) throws InformationFlowException {
        var info = requireNonNullEnabledEvent(inputEvents.get(eventId));
        assertAdmissibleValueType(info.event(), VoidVal.instance());
        assertIfcOK(info.event(), info.valueEnv());
        List<StateUpdate> updates = new LinkedList<>();
        locallyUpdateOnEventExecution(info, VoidVal.instance(), updates);
        onLocallyInitiatedEvent(info.event(), info.evalContext, updates);
        graphObservers.forEach(observer -> observer.onUpdate(updates));
    }

    /**
     * Executes the {@link dcr.common.events.ReceiveEvent Receive Event} identified by
     * {@code eventId }.
     *
     * @param eventId          the event's unique identifier in this graph
     * @param receivedValue    the event's updated value
     * @param sender           the participant that triggered this event's dual
     * @param idExtensionToken the token to be used to generate the names of any events
     *                         potentially created due to
     *                         the triggering of this event
     */
    public void onReceiveEvent(String eventId, Value receivedValue, UserVal sender,
                               String idExtensionToken) {
        var info = receiveEvents.get(eventId);
        requireNonNullEnabledEvent(info);
        assertAdmissibleValueType(info.event, receivedValue);
        var event = info.event();
        List<StateUpdate> updates = new LinkedList<>();
        locallyUpdateOnEventExecution(info, receivedValue, updates);
        onRemotelyInitiatedEvent(event, sender, idExtensionToken, updates);
    }

    // indicates whether the event instance is enabled
    private boolean isEnabled(EventInstance event) {
        if (!event.isIncluded()) {return false;}
        for (var rel : conditions.getOrDefault(event, Collections.emptyList())) {
            if (rel.source().isIncluded() && !rel.source().hasExecuted()) {return false;}
        }
        for (var rel : milestones.getOrDefault(event, Collections.emptyList())) {
            if (rel.source().isIncluded() && rel.source().isPending()) {return false;}
        }
        return true;
    }


    // A comment on DCR semantics for event execution:
    // Computation expressions and guard expressions support leverage primitive values
    // and references to event-data values. Effects MUST apply in the following order
    //
    // 1. update the event's value - for a computation event this requires previous
    // evaluation of
    // the data expression;
    // 2. set the event as 'executed' and 'not-pending';
    // 3. Propagate side effects (relations) - guards are evaluated against the
    // previous marking,
    // except for the event being executed, for which the updated marking is considered
    // instead;
    // - 3.1 Evaluate responses first: if there's a self-response relation, the event
    // becomes
    // pending again;
    // - 3.2 Evaluate includes/excludes next: add all included events first, and remove
    // all
    // excluded events after - this means exclusion wins over inclusion, regardless of
    // the order in
    // which the relations are defined;
    // - 3.3 go through spawns last
    private void onPropagateControlFlowConstraints(EventInfo<?> eventInfo,
                                                   List<StateUpdate> updates) {
        responses.getOrDefault(eventInfo.event, Collections.emptyList())
                .forEach(response -> {
                    if (!response.target().isPending()) {
                        if (response.guard().eval(eventInfo.valueEnv())
                                .equals(BoolVal.TRUE)) {
                            updates.add(new EventUpdate(response.target(),
                                    StateUpdate.UpdateType.UPDATED));
                            response.target().onResponse();
                        }
                    }
                });
        includes.getOrDefault(eventInfo.event, Collections.emptyList())
                .forEach(include -> {
                    if (!include.target().isIncluded()) {
                        if (include.guard().eval(eventInfo.valueEnv())
                                .equals(BoolVal.TRUE)) {
                            updates.add(new EventUpdate(include.target(),
                                    StateUpdate.UpdateType.UPDATED));
                            include.target().onInclude();
                        }
                    }
                });
        excludes.getOrDefault(eventInfo.event, Collections.emptyList())
                .forEach(exclude -> {
                    if (exclude.target().isIncluded()) {
                        if (exclude.guard().eval(eventInfo.valueEnv())
                                .equals(BoolVal.TRUE)) {
                            updates.add(new EventUpdate(exclude.target(),
                                    StateUpdate.UpdateType.UPDATED));
                            exclude.target().onExclude();
                        }
                    }
                });
    }

    // for local events only
    private static String localIdExtensionOf(String choreoElementUID,
                                             String idTokenExtension) {
        return String.format("%s_%s", choreoElementUID, idTokenExtension);
    }

    // extend localUID to
    private static String globalIdExtensionOf(String idExtensionToken, UserVal sender,
                                              UserVal receiver) {
        return String.format("%s_%s", idExtensionToken,
                Integer.toHexString(sender.hashCode() + receiver.hashCode()));
    }


    // unique tokens for spawn-based event-id generation (for locally-initiated events)
    private static String generateIdExtensionToken() {
        return UUID.randomUUID().toString();
    }

    // TODO [revisit] updateEnv exception should reflect an implementation error: bug,
    //  not a feature
    // TODO [revisit] removal of conditions upon first execute
    private void locallyUpdateOnEventExecution(EventInfo<?> eventInfo, Value newValue,
                                               List<StateUpdate> updates) {
        GenericEventInstance event = eventInfo.event;
        var hasExecuted = event.hasExecuted();
        event.onExecuted(newValue);
        if (!hasExecuted) {conditions.remove(event);}
        var eventVal = new EventVal(newValue, event.eventType());
        rebindPresent(event.remoteID(), eventVal, eventInfo.valueEnv());
        updates.add(new EventUpdate(event, StateUpdate.UpdateType.UPDATED));
        onPropagateControlFlowConstraints(eventInfo, updates);
    }

    // TODO [extract consts]
    private <T extends GenericEventInstance> EventInfo<T> requireNonNullEnabledEvent(
            EventInfo<T> info) {
        if (info == null) {throw new RuntimeException("Event not found");}
        if (!isEnabled(info.event)) {throw new RuntimeException("Event is not enabled");}
        return info;
    }

    // TODO [proper exception]
    // TODO [revisit] moved to Marking?
    private void assertAdmissibleValueType(GenericEventInstance event,
                                           Value replaceValue) {
        if (!event.value().type().equals(replaceValue.type())) {
            logger.info("Unexpected value type {} for event of value type {}",
                    replaceValue.type(), event.value().type());
            throw new RuntimeException("Event value rejected: illegal value type");
        }
    }

    // called by Receive events
    private void onRemotelyInitiatedEvent(RemotelyInitiatedEventInstance event,
                                          UserVal sender, String idExtensionToken,
                                          List<StateUpdate> updates) {
        spawnRelations.getOrDefault(event, Collections.emptyList()).forEach(
                info -> onSpawn(event, info, sender, self, idExtensionToken, updates));
    }

    // called in the context of applying the effects of a triggered Input/Computation
    // event
    private void onLocallyInitiatedEvent(LocallyInitiatedEventInstance event,
                                         UserVal receiver, String idExtensionToken,
                                         List<StateUpdate> updates) {
        spawnRelations.getOrDefault(event, Collections.emptyList()).forEach(
                info -> onSpawn(event, info, self, receiver, idExtensionToken, updates));
    }

    // called by either Input or Computation events
    private void onLocallyInitiatedEvent(LocallyInitiatedEventInstance event,
                                         EvalContext evalCtxt,
                                         List<StateUpdate> updates) {
        // we
        var idExtensionToken = generateIdExtensionToken();
        event.receivers().ifPresentOrElse(rcvExpr -> {
            var rcvVal = rcvExpr.eval(evalCtxt.valueEnv(), evalCtxt.userEnv());
            var receivers =
                    communicationLayer.uponSendRequest(self, event.remoteID(), rcvVal,
                            event.marking(), idExtensionToken);
            receivers.forEach(
                    receiver -> onLocallyInitiatedEvent(event, receiver, idExtensionToken,
                            updates));
        }, () -> onSpawn((GenericEventInstance) event, idExtensionToken, updates));
    }

    // upon Local-based spawn
    private void onSpawn(GenericEventInstance event, String idExtensionToken,
                         List<StateUpdate> updates) {
        var spawns = spawnRelations.getOrDefault(event, new LinkedList<>());
        if (spawns.isEmpty()) {return;}
        spawns.forEach(info -> {
            var subgraph = info.spawn().subGraph();
            var newSpawnContext =
                    info.spawnContext().beginScope(info.spawn().triggerId(), event);
            var idExtension =
                    localIdExtensionOf(subgraph.endpointElementUID(), idExtensionToken);
            instantiateGraphElement(subgraph, newSpawnContext, idExtension, updates);
        });
    }

    // upon Tx/Rx-based spawn
    private void onSpawn(EventInstance event, SpawnRelationInfo info, UserVal sender,
                         UserVal receiver, String idExtensionToken,
                         List<StateUpdate> updates) {
        // var remoteUser = locallyInitiated ? receiver : sender;
        var subgraph = info.spawn().subGraph();
        var newSpawnContext = info.spawnContext()
                .beginScope(info.spawn().triggerId(), event, sender, receiver);
        var localIdExtension = globalIdExtensionOf(idExtensionToken, sender, receiver);
        instantiateGraphElement(subgraph, newSpawnContext, localIdExtension, updates);
    }


//    TODO [revisit] temporary fix for frontend purposes

    /**
     * Returns the currently enabled events
     *
     * @return the currently enabled events
     */
    public List<EventInstance> enabledEvents() {
        return eventsByUuid.values().stream().map(EventInfo::event)
                .filter(this::isEnabled).filter(e -> !(e instanceof ReceiveInstance))
                .collect(Collectors.toList());
    }

    /**
     * Returns the currently enabled events
     *
     * @return the currently enabled events
     */
    public List<EventInstance> events() {
        return eventsByUuid.values().stream().map(EventInfo::event)
//                .filter(e -> !(e instanceof ReceiveInstance))
                .collect(Collectors.toList());
    }

    /* =============================
     * Model-elements instantiation
     * ============================= */

    // instantiate a (sub)graph element - uidExtension expected to be empty for top-level
    private void instantiateGraphElement(GraphElement graph, SpawnContext spawnContext,
                                         String uidExtension, List<StateUpdate> updates) {
        List<Consumer<GraphRunner>> graphUpdates = new LinkedList<>();
        graph.events().forEach(element -> graphUpdates.add(
                runner -> runner.instantiateEventElement(element, uidExtension,
                        spawnContext, updates)));
        graph.controlFlowRelations().forEach(element -> graphUpdates.add(
                runner -> runner.instantiateControlFlowRelationElement(element,
                        spawnContext)));
        graph.spawnRelations().forEach(element -> graphUpdates.add(
                runner -> runner.instantiateSpawnRelation(element, spawnContext)));
        updateState(graphUpdates);
    }

    // TODO [revisit] also add to specific computation/input/receive mappings?
    // updates the graph's state by instantiating an event element
    private void instantiateEventElement(EventElement baseElement, String idExtension,
                                         SpawnContext spawnContext,
                                         List<StateUpdate> updates) {
        if (!canInstantiate(baseElement.instantiationConstraint(),
                spawnContext.evalEnv)) {
            logger.info("Dropping event: endpointElementUID {}",
                    baseElement.endpointElementUID());
            return;
        }
        String localUID = newEventUuidOf(baseElement.endpointElementUID(), idExtension);
        String remoteID = newEventUuidOf(baseElement.choreoElementUID(), idExtension);
        logger.info("Creating event: localUID {}, remoteID {}", localUID, remoteID);

        var instance = Events.newInstance(localUID, remoteID, baseElement);
        spawnContext.onNewEventInstance(instance);
        var evalContext = new EvalContext(spawnContext.evalEnv, spawnContext.userEnv);
        var eventInfo = switch (instance) {
            case ComputationInstance e -> {
                var info = new EventInfo<>(e, evalContext);
                computationEvents.put(instance.remoteID(), info);
                // TODO [remove] this is a temporary patch for demo purposes
                e.receivers = e.baseElement().remoteParticipants()
                        .map(rcvExpr -> rcvExpr.eval(evalContext.valueEnv(),
                                evalContext.userEnv()))
                        .orElse(new SetUnionVal(List.of()));
                yield info;
            }
            case InputInstance e -> {
                var info = new EventInfo<>(e, evalContext);
                inputEvents.put(instance.remoteID(), info);
                // TODO [remove] this is a temporary patch for demo purposes
                e.receivers = e.baseElement().remoteParticipants()
                        .map(rcvExpr -> rcvExpr.eval(evalContext.valueEnv(),
                                evalContext.userEnv()))
                        .orElse(new SetUnionVal(List.of()));
                yield info;
            }
            case ReceiveInstance e -> {
                var info = new EventInfo<>(e, evalContext);
                receiveEvents.put(instance.remoteID(), info);
                yield info;
            }
        };
        eventsByUuid.put(instance.localUID(), eventInfo);
        updates.add(new EventUpdate(instance, StateUpdate.UpdateType.CREATED));
    }

    // instantiate a spawn relation element
    private void instantiateSpawnRelation(SpawnRelationElement baseElement,
                                          SpawnContext spawnContext) {
        if (!canInstantiate(baseElement.instantiationConstraint(),
                spawnContext.evalEnv)) {
            logger.info("Dropping relation instance {}",
                    baseElement.endpointElementUID());
            return;
        }
        var relInstance = Relations.newSpawnRelationInstance(baseElement,
                lookupPresent(spawnContext.renamingEnv, baseElement.sourceId()));
        List<SpawnRelationInfo> spawnRelations =
                this.spawnRelations.getOrDefault(relInstance.getSource(),
                        new LinkedList<>());
        spawnRelations.add(new SpawnRelationInfo(relInstance, spawnContext));
        this.spawnRelations.putIfAbsent(relInstance.getSource(), spawnRelations);
    }

    // instantiate a control-flow relation element
    private InstantiatedControlFlowRelation newControlFlowRelationInstanceOf(
            ControlFlowRelationElement baseElement, SpawnContext spawnContext) {
        GenericEventInstance source =
                spawnContext.renamingEnv.lookup(baseElement.sourceId()).orElseThrow()
                        .value();
        GenericEventInstance target =
                spawnContext.renamingEnv.lookup(baseElement.targetId()).orElseThrow()
                        .value();
        return Relations.newControlFlowRelation(baseElement, source, target);
    }

    private void instantiateControlFlowRelationElement(
            ControlFlowRelationElement baseElement, SpawnContext spawnContext) {
        if (!canInstantiate(baseElement.instantiationConstraint(),
                spawnContext.evalEnv)) {
            logger.info("Dropping relation instance: {}",
                    baseElement.endpointElementUID());
            return;
        }
        var relInstance = newControlFlowRelationInstanceOf(baseElement, spawnContext);
        var relInfo = new ControlFlowRelationInfo(relInstance, spawnContext.evalEnv);
        addToListMapping(relInstance.getSource(), relInfo, controlFlowRelations);
        switch (relInstance.relationType()) {
            case INCLUDE -> addToListMapping(relInstance.getSource(), relInfo, includes);
            case EXCLUDE -> addToListMapping(relInstance.getSource(), relInfo, excludes);
            case RESPONSE ->
                    addToListMapping(relInstance.getSource(), relInfo, responses);
            case CONDITION ->
                    addToListMapping(relInstance.getTarget(), relInfo, conditions);
            case MILESTONE ->
                    addToListMapping(relInstance.getTarget(), relInfo, milestones);
        }

    }

    private boolean canInstantiate(ComputationExpression constraint,
                                   Environment<Value> evalEnv) {
        return constraint.eval(evalEnv).equals(BoolVal.of(true));
    }


    // Applies a sequence of updates to the graph's state as a single update step; each
    // update
    // step should leave the graph in a consistent state (or else...)
    private void updateState(Iterable<Consumer<GraphRunner>> updates) {
        updates.forEach(update -> update.accept(this));
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(eventsByUuid.values().stream().map(ctxt -> ctxt.event.toString())
                .collect(Collectors.joining("\n", "\n", "")));
        if (!controlFlowRelations.isEmpty()) {
            builder.append(System.lineSeparator()).append(";");
            controlFlowRelations.values().forEach(listing -> listing.forEach(
                    relInfo -> builder.append(System.lineSeparator())
                            .append(relInfo.relation().toString())));
        }
        if (!spawnRelations.isEmpty()) {
            builder.append(System.lineSeparator()).append(";");
            spawnRelations.values().forEach(list -> list.stream()
                    .map(info -> "\n" + info.spawn.baseElement().sourceId() + " -->> " +
                            info.spawn.subGraph()).forEach(builder::append));
        }
        return builder.toString();
    }

    // TODO [deprecate]
    // public String unparse(String indent) {
    //     Objects.requireNonNull(indent);
    //     StringBuilder builder = new StringBuilder();
    //     builder.append("   == Runtime Graph State ==\n");
    //     Consumer<Map<EventInstance, List<ControlFlowRelationInfo>>>
    //     ctrlFlowRelUnparser =
    //             infoVals -> {
    //                 infoVals.values()
    //                         .stream()
    //                         .map(values -> values.stream()
    //                                 .map(info -> info.relation.unparse(indent))
    //                                 .collect(Collectors.joining("\n")))
    //                         .forEach(builder::append);
    //             };
    //     Consumer<Map<EventInstance, List<SpawnRelationInfo>>> spawnRelUnparser =
    //     infoVals -> {
    //         infoVals.values()
    //                 .forEach(list -> list.stream()
    //                         .map(info -> info.spawn.unparse(indent) + "\n")
    //                         .forEach(builder::append));
    //     };
    //     eventsByUuid.values()
    //             .stream()
    //             .map(ctxt -> ctxt.event.unparse(indent) + "\n")
    //             .forEach(builder::append);
    //     ctrlFlowRelUnparser.accept(conditions);
    //     ctrlFlowRelUnparser.accept(responses);
    //     spawnRelUnparser.accept(spawnRelations);
    //     return builder.toString();
    // }

    /**
     * @param evalEnv cumulative register keeping track of alpha-renaming,
     *                sender/receiver of triggering
     *                event (when applicable), and eval env
     */
    record SpawnContext(Environment<Value> evalEnv,
                        Environment<GenericEventInstance> renamingEnv,
                        Environment<Pair<UserVal, UserVal>> userEnv) {
        // cumulative register keeping track of actual sender/receiver of each interaction
        // triggering a spawn (either Tx or Rx) - enables resolving of Receiver(e1)
        // and Sender(e1) type of expressions - empty for top-level events

        static SpawnContext init(UserVal self) {
            var selfVal = RecordVal.of(Record.ofEntries(
                    Record.Field.of("params", self.getParamsAsRecordVal())));
            Environment<Value> evalEnv = Environment.empty();
            // _@self must always be available for evaluation of constraints
            evalEnv.bindIfAbsent(SELF, selfVal);
            return new SpawnContext(evalEnv, Environment.empty(), Environment.empty());
        }

        void onNewEventInstance(GenericEventInstance instance) {
            evalEnv.bindIfAbsent(instance.remoteID(), instance.value());
            renamingEnv.bindIfAbsent(instance.baseElement().endpointElementUID(),
                    instance);
        }

        private static PropBasedVal encodeTriggerVal(Value val, UserVal initiator,
                                                     UserVal receiver) {
            // TODO extract (static final) const strings
            var initiatorVal = initiator.getParamsAsRecordVal();
            var receiverVal = receiver.getParamsAsRecordVal();
            return new RecordVal(Record.ofEntries(Record.Field.of("value", val),
                    Record.Field.of("initiator", initiatorVal),
                    Record.Field.of("receiver", receiverVal)));
        }

        // upon Tx/Rx trigger event
        SpawnContext beginScope(String triggerId, EventInstance triggerEvent,
                                UserVal sender, UserVal receiver) {
            var triggerVal = encodeTriggerVal(triggerEvent.value(), sender, receiver);
            var newEvalEnv = evalEnv.beginScope(triggerId, triggerVal);
            var newRenamingEnv = renamingEnv.beginScope();
            var newUserEnv = userEnv.beginScope(triggerEvent.baseElement().remoteID(),
                    Pair.of(sender, receiver));
            // TODO defensive copy triggerVal - immutable snapshot
            return new SpawnContext(newEvalEnv, newRenamingEnv, newUserEnv);
        }

        // upon Local trigger event
        SpawnContext beginScope(String triggerId, EventInstance triggerEvent) {
            var newEvalEnv = evalEnv.beginScope(triggerId, triggerEvent.value());
            return new SpawnContext(newEvalEnv, renamingEnv.beginScope(), userEnv);
        }
    }

    private record SpawnRelationInfo(InstantiatedSpawnRelation spawn,
                                     SpawnContext spawnContext) {}

    private record ControlFlowRelationInfo(InstantiatedControlFlowRelation relation,
                                           Environment<Value> evalEnv) {
        GenericEventInstance source() {
            return relation.getSource();
        }

        ComputationExpression guard() {return relation.guard();}

        GenericEventInstance target() {
            return relation.getTarget();
        }
    }

    // context for evaluation of computation- and user-expressions
    private record EvalContext(Environment<Value> valueEnv,
                               Environment<Pair<UserVal, UserVal>> userEnv) {}


    // encloses event
    private record EventInfo<E extends GenericEventInstance>(E event,
                                                             EvalContext evalContext) {
        Environment<Value> valueEnv() {return evalContext().valueEnv;}
    }

    // ifc-leak detection handler
    private static void onIfcLeakDetection(Event event) throws InformationFlowException {
        throw new InformationFlowException(
                "Event execution prevented due to information-flow control policy: " +
                        event.remoteID());
    }

    // Reminder: caller is responsible for providing the correct environment for the
    // purpose
    // of ifc evaluation (namely, whether the evalEnv should already reflect the event
    // being
    // created - maybe not a good idea... just like referencing 'this' from the
    // constructor)
    private static void assertIfcOK(Event event, Environment<Value> env)
            throws InformationFlowException {
        if (!event.ifcConstraint().eval(env).value()) {
            onIfcLeakDetection(event);
        }
    }
}
