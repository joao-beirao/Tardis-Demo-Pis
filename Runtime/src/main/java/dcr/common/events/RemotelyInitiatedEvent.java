package dcr.common.events;

import dcr.common.events.userset.expressions.UserSetExpression;

import java.util.Optional;

public interface RemotelyInitiatedEvent
        extends Event {
    UserSetExpression getSenderExpr();

    /**
     * Returns an optional describing the set of receivers of this event.
     *
     * @return an optional describing the receivers of this event, and an empty
     * optional if there
     * aren't any.
     */
    default Optional<? extends UserSetExpression> senders() {
        return this.remoteParticipants();
    }
}
