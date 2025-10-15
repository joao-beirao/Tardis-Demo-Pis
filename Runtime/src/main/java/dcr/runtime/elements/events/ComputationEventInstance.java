package dcr.runtime.elements.events;

import dcr.common.events.ComputationEvent;
import dcr.common.events.userset.expressions.UserSetExpression;

import java.util.Optional;

public interface ComputationEventInstance
        extends LocallyInitiatedEventInstance, ComputationEvent {
    @Override
    Optional<? extends UserSetExpression> receivers();

}