package dcr.runtime.elements.events;

import dcr.common.events.InputEvent;
import dcr.common.events.userset.expressions.UserSetExpression;

import java.util.Optional;

public interface InputEventInstance
    extends LocallyInitiatedEventInstance, InputEvent {
    @Override
    Optional<? extends UserSetExpression> receivers();
}