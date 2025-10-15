package dcr.common.events;

import dcr.common.data.computation.BooleanExpression;
import dcr.common.data.types.EventType;
import dcr.common.data.types.Type;
import dcr.common.data.values.Value;
import dcr.common.events.userset.expressions.UserSetExpression;

import java.util.Optional;

// TODO [javadoc]
// TODO [revisit] consider adding a getInitiator()

/**
 * Common interface for any object representing a DCR event.
 */
public interface Event {

    /**
     * Common interface for any object representing a DCR marking.
     * <p>
     * DCR {@link Event events} are <i>stateful</i> entities. A Marking captures the mutable state
     * of an event, reflecting its current throughout execution.
     */
    interface Marking {
        /**
         * Indicates whether the event has already been <i>executed</i> (at any given point in
         * time).
         *
         * @return <code>true</code> if the associated event has already been executed;
         *         <code>false</code> otherwise.
         */
        boolean hasExecuted();

        /**
         * Indicates whether the event is currently <i>pending</i>.
         *
         * @return <code>true</code> if the associated event is currently pending;
         *         <code>false</code> otherwise.
         */
        boolean isPending();

        /**
         * Indicates whether the event is currently <i>included</i>. An event is said to be
         * <i>excluded</i> otherwise.
         *
         * @return <code>true</code> if the event is currently included;
         *         <code>false</code> otherwise.
         */
        boolean isIncluded();

        /**
         * Returns the current {@link Value value} of the event.
         *
         * @return the current {@link Value value} of the event.
         */
        Value value();

        /**
         * Returns the {@link Type type} of {@link Value values} stored by the event.
         * <p>
         * The type of event stored by each event is fixed at design time.
         *
         * @return the type of values stored by the event.
         */
        default Type valueType() {
            return value().type();
        }

        // TODO [rethink] useful, but maybe not the right place
        default String toStringPrefix() {
            if (isPending()) {
                if (!isIncluded()) {return "!%";}
                else {return "!";}
            }
            if (!isIncluded()) {return "%";}
            return "";
        }
    }

    /**
     * Returns the local id assigned to an event.
     *
     * @return the local id assigned to an event at design time.
     */
    String remoteID();


    /**
     * Returns the label assigned to this event.
     *
     * @return the label assigned to this event.
     */
    String label();


    /**
     * Returns a marking object reflecting the current state of this event.
     *
     * @return a marking object reflecting the current state of this event.
     */
    Marking marking();


    BooleanExpression instantiationConstraint();

    BooleanExpression ifcConstraint();

    /**
     * Returns a user-set expression describing the swarm members that participate in this event as
     * <i>receivers<i></> in an interaction.
     *
     * @return an Optional describing the receivers, if this event is an interaction; an empty
     *         Optional otherwise;
     */
    Optional<? extends UserSetExpression> remoteParticipants();

    default boolean hasExecuted() {return marking().hasExecuted();}

    default boolean isPending() {return marking().isPending();}

    default boolean isIncluded() {return marking().isIncluded();}

    default Value value() {return marking().value();}

    default EventType eventType() {
        return new EventType(label());
    }

    default Type valueType() {return marking().valueType();}
}