package dcr.model;

import dcr.common.DCRGraph;
import dcr.model.events.ComputationEventElement;
import dcr.model.events.EventElement;
import dcr.model.relations.ControlFlowRelationElement;
import dcr.model.relations.RelationElement;
import dcr.model.relations.SpawnRelationElement;

public sealed interface GraphElement
        extends ModelElement, DCRGraph
        permits RecursiveGraphElement {

    @Override
    Iterable<? extends EventElement> events();

    @Override
    Iterable<? extends RelationElement> relations();

    @Override
    Iterable<? extends ComputationEventElement> computationEvents();

    @Override
    Iterable<? extends ControlFlowRelationElement> controlFlowRelations();

    @Override
    Iterable<? extends SpawnRelationElement> spawnRelations();

    String unparse();
}
