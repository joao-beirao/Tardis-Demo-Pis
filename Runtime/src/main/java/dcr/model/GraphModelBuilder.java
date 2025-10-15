package dcr.model;

import dcr.common.data.computation.BoolLiteral;
import dcr.common.data.computation.BooleanExpression;
import dcr.common.data.computation.ComputationExpression;
import dcr.common.events.userset.expressions.UserSetExpression;
import dcr.model.events.*;
import dcr.model.relations.ControlFlowRelationElement;
import dcr.model.relations.RelationElements;
import dcr.model.relations.SpawnRelationElement;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

// TODO hide class behind factory builder
public class GraphModelBuilder {

    private final String elementId;
    private final List<Consumer<RecursiveGraphElement>> eventConsumers;
    private final List<Consumer<RecursiveGraphElement>> controlFlowRelationsConsumers;
    private final List<Consumer<RecursiveGraphElement>> spawnGraphConsumers;

    public GraphModelBuilder() {
        // DUMMY elementId - not a spawn
        this("_");
    }

    public GraphModelBuilder(String elementId) {

        this.elementId = elementId;
        eventConsumers = new LinkedList<>();
        controlFlowRelationsConsumers = new LinkedList<>();
        spawnGraphConsumers = new LinkedList<>();
    }


    protected String getElementId() {
        return elementId;
    }

    public GraphModelBuilder addLocalComputationEvent(String choreoElementUID,
            String endpointElementUID,
            String localId, String eventType, ComputationExpression computation,
            ImmutableMarkingElement initialMarking, BooleanExpression instantiationConstraint, BooleanExpression ifcConstraint ) {
        registerComputationEventElement(
                EventElements.newLocalComputationEvent(choreoElementUID, endpointElementUID, localId, eventType,
                        computation,
                        initialMarking, instantiationConstraint, ifcConstraint));
        return this;
    }

    public GraphModelBuilder addComputationEvent(String choreoElementUID, String endpointElementUID, String localId,
            String eventType, ComputationExpression computation, UserSetExpression receivers,
            ImmutableMarkingElement initialMarking, BooleanExpression instantiationConstraint, BooleanExpression ifcConstraint ) {
        registerComputationEventElement(
                EventElements.newComputationEvent(choreoElementUID, endpointElementUID, localId, eventType,
                        computation,
                        initialMarking, receivers, instantiationConstraint, ifcConstraint));
        return this;
    }

    public GraphModelBuilder addLocalInputEvent(String choreoElementUID, String endpointElementUID, String localId,
            String eventType, ImmutableMarkingElement initialMarking, BooleanExpression instantiationConstraint, BooleanExpression ifcConstraint ) {
        registerInputEventElement(
                EventElements.newLocalInputEvent(choreoElementUID, endpointElementUID, localId, eventType,
                        initialMarking,
                        instantiationConstraint, ifcConstraint));
        return this;
    }

    public GraphModelBuilder addInputEvent(String choreoElementUID, String endpointElementUID, String localId,
            String eventType, UserSetExpression receivers, ImmutableMarkingElement initialMarking,
            BooleanExpression instantiationConstraint, BooleanExpression ifcConstraint ) {
        registerInputEventElement(
                EventElements.newInputEvent(choreoElementUID, endpointElementUID, localId, eventType, receivers,
                        initialMarking,
                        instantiationConstraint, ifcConstraint));
        return this;
    }

    public GraphModelBuilder addReceiveEvent(String choreoElementUID, String endpointElementUID, String localId,
            String eventType, UserSetExpression senders, ImmutableMarkingElement initialMarking,
            BooleanExpression instantiationConstraint, BooleanExpression ifcConstraint ) {
        registerReceiveEventElement(
                EventElements.newReceiveEvent(choreoElementUID, endpointElementUID, localId, eventType, senders,
                        initialMarking,
                        instantiationConstraint, ifcConstraint));
        return this;
    }


    public GraphModelBuilder addControlFlowRelation(ControlFlowRelationElement element) {
        controlFlowRelationsConsumers.add(graph -> graph.addControlFlowRelation(element));
        return this;
    }

    public GraphModelBuilder addSpawnRelation(SpawnRelationElement element) {
        spawnGraphConsumers.add(graph -> graph.addSpawnRelation(element));
        return this;
    }

    // public GraphModelBuilder addControlFlowRelation(String uid, String srcId, String targetId,
    //         ControlFlowRelation.Type relationType, BooleanExpression instantiationConstraint) {
    //     ControlFlowRelationElement element =
    //             RelationElements.newControlFlowRelation(uid, srcId, targetId, relationType,
    //                     instantiationConstraint);
    //     controlFlowRelationsConsumers.add(graph -> graph.addControlFlowRelation(element));
    //     return this;
    // }

    // private void registerControlFlowRelation(ControlFlowRelationElement element) {
    //     controlFlowRelationsConsumers.add(graph -> graph.addControlFlowRelation(element));
    // }

    private void registerComputationEventElement(
            ComputationEventElement element) {
        System.err.println("Registering computation event element: " + elementId);
        eventConsumers.add(graph -> graph.addComputationEvent(element));
    }

    private final void registerInputEventElement(InputEventElement element) {
        System.err.println("Registering input event element: " + elementId);
        eventConsumers.add(graph -> graph.addInputEvent(element));
    }

    private final void registerReceiveEventElement(

            ReceiveEventElement element) {
        System.err.println("Registering receive event element: " + elementId);
        eventConsumers.add(graph -> graph.addReceiveEvent(element));
    }



    // @Override
    public GraphModelBuilder beginSpawn(String relationElementId, String subgraphElementId,
            String sourceEventId, String triggerId, ComputationExpression instantiationConstraint) {
        return new SpawnGraphModelBuilder(relationElementId, subgraphElementId, sourceEventId,
                triggerId, instantiationConstraint, this);
    }

    // @Override
    public GraphModelBuilder endSpawn() {
        // TODO [revisit] should probably throw a MalformedGraphException
        return this;
    }

    // @Override
    public RecursiveGraphElement build() {
        return populate(new RecursiveGraphElement(elementId));
    }

    private void addSpawnGraphBuilder(SpawnGraphModelBuilder builder) {
        spawnGraphConsumers.add(builder);
    }

    // recursive "downward" call - each builder passes on its type of graph
    protected RecursiveGraphElement populate(RecursiveGraphElement graph) {
        eventConsumers.forEach(c -> c.accept(graph));
        controlFlowRelationsConsumers.forEach(c -> c.accept(graph));
        spawnGraphConsumers.forEach(c -> c.accept(graph));
        return graph;
    }

    /**
     * The builder extends the {@link GraphModelBuilder} by keeping track of the parent scope, as
     * well as of the event that triggers the spawn.
     */
    private static final class SpawnGraphModelBuilder
            extends GraphModelBuilder
            implements Consumer<RecursiveGraphElement> {

        private final String sourceEventId;
        private final String subgraphElementId;
        private final String triggerId;
        private final ComputationExpression instantiationConstraint;
        private final GraphModelBuilder outerScope;

        private SpawnGraphModelBuilder(String relationElementId, String subgraphElementId,
                String sourceEventId, String triggerId, ComputationExpression instantiationConstraint, GraphModelBuilder outerScope) {
            super(relationElementId);
            this.sourceEventId = sourceEventId;
            this.triggerId = triggerId;
            this.instantiationConstraint = instantiationConstraint;
            this.outerScope = outerScope;
            this.subgraphElementId = subgraphElementId;
        }

        @Override
        public GraphModelBuilder endSpawn() {
            outerScope.addSpawnGraphBuilder(this);
            return outerScope;
        }

        //  just route the call to the top level
        @Override
        public RecursiveGraphElement build() {
            return outerScope.build();
        }

        @Override
        public void accept(RecursiveGraphElement model) {
            RecursiveGraphElement subgraph = populate(new RecursiveGraphElement(subgraphElementId));
            SpawnRelationElement spawnElement =
                    RelationElements.newSpawnRelation(getElementId(), sourceEventId,
                            triggerId, BoolLiteral.TRUE,
                            subgraph, instantiationConstraint);
            model.addSpawnRelation(spawnElement);
        }
    }
}
