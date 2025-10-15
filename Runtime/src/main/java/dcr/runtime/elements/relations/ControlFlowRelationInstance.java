package dcr.runtime.elements.relations;

import dcr.runtime.elements.events.EventInstance;

public  interface ControlFlowRelationInstance extends dcr.common.relations.ControlFlowRelation {
    EventInstance getTarget();
}
