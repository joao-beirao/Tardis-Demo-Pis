package dcr.common.events;

import dcr.common.events.userset.expressions.UserSetExpression;

import java.util.Optional;

/**
 * An event which is initiated locally (<i>i.e.<i/>, by the owner of the projection).
 * <p>
 * A locally initiated event is said to be a <i>local event</i> if it has no passive participants,
 * and a <i>send event</i> otherwise.
 */
public interface LocallyInitiatedEvent
        extends Event {

    /**
     * Returns an optional describing the set of receivers of this event.
     *
     * @return an optional describing the receivers of this event, and an empty optional if there
     *         aren't any.
     */
    default Optional<? extends UserSetExpression> receivers() {
        return this.remoteParticipants();
    }
}
