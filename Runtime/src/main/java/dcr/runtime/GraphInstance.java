package dcr.runtime;

//TODO Graph interface

import dcr.common.DCRGraph;
import dcr.runtime.elements.events.ComputationEventInstance;
import dcr.runtime.elements.events.EventInstance;
import dcr.runtime.elements.events.InputEventInstance;
import dcr.runtime.elements.events.ReceiveEventInstance;
import dcr.runtime.elements.relations.ControlFlowRelationInstance;
import dcr.runtime.elements.relations.SpawnRelationInstance;

/**
 * A mutable object...
 */
public interface GraphInstance
        extends DCRGraph {

    @Override
    Iterable<EventInstance> events();

    @Override
    Iterable<ComputationEventInstance> computationEvents();

    @Override
    Iterable<InputEventInstance> inputEvents();

    @Override
    Iterable<ReceiveEventInstance> receiveEvents();

    @Override
    Iterable<ControlFlowRelationInstance> controlFlowRelations();

    @Override
    Iterable<SpawnRelationInstance> spawnRelations();
}
