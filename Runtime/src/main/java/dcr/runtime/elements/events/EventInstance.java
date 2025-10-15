package dcr.runtime.elements.events;

import dcr.common.data.computation.BooleanExpression;
import dcr.common.events.Event;
import dcr.common.events.userset.values.UserVal;
import dcr.model.events.EventElement;
import dcr.runtime.elements.RuntimeElement;

import java.util.List;

// Marking is mutable, has a globalId/UUID, participant exprs are a fragment of the
// same exprs in
// the model - upon instantiation, Receiver/Sender exprs are replaced with actual User
// TODO [seal?]
public interface EventInstance
        extends Event, RuntimeElement {
    String localUID();

    @Override
    EventElement baseElement();

    @Override
    default BooleanExpression instantiationConstraint() {
        return baseElement().instantiationConstraint();
    }

    @Override
    default BooleanExpression ifcConstraint() {
        return baseElement().ifcConstraint();
    }

    long creationTimestamp();
}

