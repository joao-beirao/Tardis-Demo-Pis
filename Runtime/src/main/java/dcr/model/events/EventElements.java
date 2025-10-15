package dcr.model.events;

import dcr.common.data.computation.BooleanExpression;
import dcr.common.data.computation.ComputationExpression;
import dcr.common.events.userset.expressions.UserSetExpression;

public final class EventElements {

    public static ComputationEventElement newComputationEvent(String choreoElementUID,
            String endpointElementUID,
            String localId, String eventType, ComputationExpression computation,
            EventElement.MarkingElement initialMarking, UserSetExpression receivers,
            BooleanExpression instantiationConstraint, BooleanExpression ifcConstraint) {
        return new ComputationEvent(choreoElementUID, endpointElementUID, localId, eventType,
                computation,
                initialMarking,
                receivers, instantiationConstraint, ifcConstraint);
    }

    public static ComputationEventElement newLocalComputationEvent(
            String choreoElementUID, String endpointElementUID, String localId, String eventType,
            ComputationExpression computation,
            EventElement.MarkingElement initialMarking, BooleanExpression instantiationConstraint,
            BooleanExpression ifcConstraint) {
        return new ComputationEvent(choreoElementUID, endpointElementUID, localId, eventType, computation,
                initialMarking,
                null, instantiationConstraint, ifcConstraint);
    }

    public static InputEventElement newInputEvent(String choreoElementUID,String endpointElementUID,
            String localId, String eventType, UserSetExpression receivers,
            EventElement.MarkingElement initialMarking, BooleanExpression instantiationConstraint,
            BooleanExpression ifcConstraint) {
        return new InputEvent(choreoElementUID, endpointElementUID, localId, eventType, initialMarking, receivers,
                instantiationConstraint, ifcConstraint);
    }

    public static InputEventElement newLocalInputEvent(String choreoElementUID,String endpointElementUID,
            String localId, String eventType, EventElement.MarkingElement initialMarking,
            BooleanExpression instantiationConstraint,
            BooleanExpression ifcConstraint) {
        return new InputEvent(choreoElementUID, endpointElementUID, localId, eventType, initialMarking, null,
                instantiationConstraint,
                ifcConstraint);
    }

    public static ReceiveEventElement newReceiveEvent(String choreoElementUID, String endpointElementUID,
            String localId, String eventType, UserSetExpression senders,
            EventElement.MarkingElement initialMarking, BooleanExpression instantiationConstraint,
            BooleanExpression ifcConstraint) {
        return new ReceiveEvent(choreoElementUID, endpointElementUID, localId, eventType, senders, initialMarking,
                instantiationConstraint, ifcConstraint);
    }
}