package dcr.model.events;

import dcr.common.data.computation.BooleanExpression;
import dcr.common.events.userset.expressions.UserSetExpression;

public sealed interface ReceiveEventElement
        extends EventElement, dcr.common.events.ReceiveEvent
        permits ReceiveEvent {

    UserSetExpression getSenderExpr();
}

final class ReceiveEvent
        extends GenericEventElement
        implements ReceiveEventElement {

    ReceiveEvent(String choreoElementUID, String endpointElementUID, String localId,
            String eventType, UserSetExpression senders, MarkingElement marking,
            BooleanExpression instantiationConstraint, BooleanExpression ifcConstraint) {
        super(choreoElementUID, endpointElementUID, localId, eventType, marking, senders,
                instantiationConstraint, ifcConstraint);
    }

    // FIXME [.get()]
    @Override
    public UserSetExpression getSenderExpr() {
        // TODO [revisit] get()
        return remoteParticipants().get();
    }

    @Override
    public String toString() {
        return String.format("<%s, %s> %s(%s: %s) [%s] (%s) [%s] (when: %s)", choreoElementUID(),
                endpointElementUID(), this.marking().toStringPrefix(), remoteID(), label(),
                valueType(), value(),
                this.remoteParticipants().map(r -> String.format("%s -> @self", r)).get(),
                instantiationConstraint());
    }

    @Override
    public String unparse() {
        return String.format("ReceiveEventElement<%s>[ (%s: %s) [%s] [%s] ]", choreoElementUID(),
                remoteID(), label(), valueType(),
                this.remoteParticipants().map(r -> String.format("%s -> @self", r)).get());
    }
}
