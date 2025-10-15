package dcr.runtime.elements.events;

import dcr.common.events.LocallyInitiatedEvent;
import dcr.common.events.userset.expressions.UserSetExpression;
import dcr.common.events.userset.values.UserSetVal;

import java.util.Optional;

public interface LocallyInitiatedEventInstance
        extends EventInstance,
                LocallyInitiatedEvent {
    @Override
    Optional<? extends UserSetExpression> receivers();

    // TODO [revisit/remove] temporary fix for frontend purposes
    UserSetVal receiverUsers();
}
