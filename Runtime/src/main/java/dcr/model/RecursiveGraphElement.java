package dcr.model;


import dcr.common.Environment;
import dcr.common.events.InputEvent;
import dcr.common.events.ReceiveEvent;
import dcr.common.relations.Relation;
import dcr.model.events.ComputationEventElement;
import dcr.model.events.EventElement;
import dcr.model.events.InputEventElement;
import dcr.model.events.ReceiveEventElement;
import dcr.model.relations.ControlFlowRelationElement;
import dcr.model.relations.RelationElement;
import dcr.model.relations.SpawnRelationElement;

import java.util.*;
import java.util.function.Function;

public final class RecursiveGraphElement
        extends GenericElement
        implements GraphElement {

    private final Map<String, EventElement> eventsByLocalId;

    // TODO [revisit] possibly discard computationEvents
    private final Map<String, ComputationEventElement> computationEvents;
    private final Set<ControlFlowRelationElement> controlFlowRelations;

    // TODO should eventually be a list of relations per event
    private final List<SpawnRelationElement> spawnRelations;

    RecursiveGraphElement(String elementId) {
        super(elementId);
        this.eventsByLocalId = new HashMap<>();
        this.computationEvents = new HashMap<>();
        // TODO [revise] use of HashSet with ControlFlowRelationElements
        this.controlFlowRelations = new HashSet<>();
        this.spawnRelations = new LinkedList<>();
    }

    @Override
    public Iterable<? extends EventElement> events() {
        return eventsByLocalId.values();
    }

    @Override
    public Iterable<? extends RelationElement> relations() {
        List<RelationElement> relations = new LinkedList<>();
        relations.addAll(controlFlowRelations);
        relations.addAll(spawnRelations);
        return Collections.unmodifiableList(relations);
    }

    @Override
    public Iterable<ComputationEventElement> computationEvents() {
        return computationEvents.values();
    }

    @Override
    public Iterable<ControlFlowRelationElement> controlFlowRelations() {
        return controlFlowRelations;
    }

    @Override
    public Iterable<? extends InputEvent> inputEvents() {
        // TODO [implement]
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public Iterable<? extends ReceiveEvent> receiveEvents() {
        return null;
    }

    @Override
    public Iterable<SpawnRelationElement> spawnRelations() {
        return spawnRelations;
    }

    // TODO [revisit] code defensively ? Likely difficult and redundant since the compiler will have
    //  gone through this
    // String label, ASTNode expression,
    // Type type, EventMarking marking
    void addComputationEvent(ComputationEventElement event) {
        // TODO uncomment
        eventsByLocalId.putIfAbsent(event.endpointElementUID(), event);
        computationEvents.putIfAbsent(event.remoteID(), event);
    }

    void addInputEvent(InputEventElement event) {
        eventsByLocalId.putIfAbsent(event.endpointElementUID(), event);
    }

    void addReceiveEvent(ReceiveEventElement event) {
        eventsByLocalId.putIfAbsent(event.endpointElementUID(), event);
    }


    void addControlFlowRelation(ControlFlowRelationElement relationElement) {
        // TODO
        controlFlowRelations.add(relationElement);
    }

    void addSpawnRelation(SpawnRelationElement relationElement) {
        spawnRelations.add(relationElement);
    }

    private static Function<ModelElement, String> stringifier = ModelElement::unparse;

    private static String beginSpawn(String indent, String sourceId) {
        return String.format("\n%s%s -->> {", indent, sourceId);
    }

    private static String endSpawn(String indent) {
        return String.format("\n%s}", indent);
    }


    private static String tester(RecursiveGraphElement graph, String indent,
            Function<ModelElement, String> stringifier) {
        StringBuilder builder = new StringBuilder();
        builder.append(System.lineSeparator())
                .append(indent)
                .append("(<")
                .append(graph.endpointElementUID())
                .append(">)");
        graph.eventsByLocalId.values()
                .forEach(event -> builder.append(System.lineSeparator())
                        .append(indent)
                        .append(stringifier.apply(event)));
        if (!graph.spawnRelations.isEmpty()) {
            builder.append(System.lineSeparator()).append(indent).append(";");
        }
        graph.spawnRelations.forEach(spawn -> {
            builder.append(beginSpawn(indent, spawn.sourceId()));
            builder.append(
                    tester((RecursiveGraphElement) spawn.subGraph(), indent + "  ", stringifier));
            builder.append(endSpawn(indent));
        });
        return builder.toString();
    }

    @Override
    public String toString() {
        return "{" +
                tester(this, "  ", Objects::toString) +
                System.lineSeparator() +
                "}";
    }

    public String toString(String indent) {
        return tester(this, indent, Objects::toString);
    }

    @Override
    public String unparse() {return tester(this, "", ModelElement::unparse);}

    /**
     * Gathers both static and dynamic info required to instantiate a DCRGraph ... (subgraph?)
     */
    private static final class Spawn {

        // static information
        final RecursiveGraphElement graph;
        // dynamic info
        final String trigger;
        final Environment<String> names;

        Spawn(RecursiveGraphElement graph, String trigger, Environment<String> names) {
            this.graph = graph;
            this.trigger = trigger;
            this.names = names;
        }
    }
}
