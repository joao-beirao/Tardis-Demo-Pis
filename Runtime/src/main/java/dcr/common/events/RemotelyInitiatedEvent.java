package dcr.common.events;

import dcr.common.events.userset.expressions.UserSetExpression;

public interface RemotelyInitiatedEvent
        extends Event {
    UserSetExpression getSenderExpr();
}
