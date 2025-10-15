package dcr.common;

import dcr.common.events.ComputationEvent;
import dcr.common.events.Event;
import dcr.common.events.InputEvent;
import dcr.common.events.ReceiveEvent;
import dcr.common.relations.ControlFlowRelation;
import dcr.common.relations.Relation;
import dcr.common.relations.SpawnRelation;

public interface DCRGraph {
    Iterable<? extends Event> events();

    Iterable<? extends Relation> relations();

    Iterable<? extends ComputationEvent> computationEvents();

    Iterable<? extends InputEvent> inputEvents();

    Iterable<? extends ReceiveEvent> receiveEvents();

    Iterable<? extends ControlFlowRelation> controlFlowRelations();

    Iterable<? extends SpawnRelation> spawnRelations();
}
