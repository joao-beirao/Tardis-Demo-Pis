package dcr.runtime.elements.relations;

import dcr.common.data.computation.ComputationExpression;
import dcr.model.relations.RelationElement;
import dcr.runtime.elements.RuntimeElement;
import dcr.runtime.elements.events.EventInstance;

public  interface RelationInstance
        extends RuntimeElement, dcr.common.relations.Relation {
    EventInstance getSource();

    @Override
    RelationElement baseElement();

    default ComputationExpression instantiationConstraint() {
        return baseElement().instantiationConstraint();
    }
}
