package dcr.runtime.elements.events;

import dcr.common.events.RemotelyInitiatedEvent;
import dcr.common.events.userset.expressions.UserSetExpression;
import dcr.common.events.userset.values.UserSetVal;

/**
 * An event which is initiated remotely (<i>i.e.<i/>, not by the owner of the projection).
 */
public interface RemotelyInitiatedEventInstance
        extends EventInstance, RemotelyInitiatedEvent {
    @Override
    UserSetExpression getSenderExpr();

    // TODO [revisit/remove] temporary fix for frontend purposes
    UserSetVal initiatorUsers();
}
