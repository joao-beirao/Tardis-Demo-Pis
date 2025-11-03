package dcr.runtime;

import dcr.common.data.computation.ComputationExpression;
import dcr.common.data.values.Value;
import dcr.common.events.userset.expressions.UserSetExpression;
import dcr.common.events.userset.values.SetUnionVal;
import dcr.common.events.userset.values.UserSetVal;
import dcr.model.events.ComputationEventElement;
import dcr.model.events.EventElement;
import dcr.model.events.InputEventElement;
import dcr.model.events.ReceiveEventElement;
import dcr.runtime.elements.events.ComputationEventInstance;
import dcr.runtime.elements.events.EventInstance;
import dcr.runtime.elements.events.InputEventInstance;
import dcr.runtime.elements.events.ReceiveEventInstance;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

final class Events {
    // TODO [deprecate]
    // public static ComputationInstance newLocalComputationInstance(String localUID,
    // String remoteID,
    //         ComputationEventElement baseElement) {
    //     return new ComputationInstance(localUID, remoteID, baseElement);
    // }

    public static ComputationInstance newComputationInstance(String localUID,
                                                             String remoteID,
                                                             ComputationEventElement baseElement) {
        return new ComputationInstance(localUID, remoteID, baseElement);
    }

    // TODO [deprecate]
    // public static InputInstance newLocalInputInstance(String localUID, String remoteID,
    //         InputEventElement baseElement) {
    //     return InputInstance.of(localUID, remoteID, baseElement);
    // }

    public static InputInstance newInputInstance(String localUID, String remoteID,
                                                 InputEventElement baseElement) {
        return InputInstance.of(localUID, remoteID, baseElement);
    }

    public static ReceiveInstance newReceiveInstance(String localUID, String remoteID,
                                                     ReceiveEventElement baseElement) {
        return ReceiveInstance.of(localUID, remoteID, baseElement);
    }

    public static GenericEventInstance newInstance(String localUID, String remoteID,
                                                   EventElement baseElement) {
        return switch (baseElement) {
            case ComputationEventElement e ->
                    newComputationInstance(localUID, remoteID, e);
            case InputEventElement e -> newInputInstance(localUID, remoteID, e);
            case ReceiveEventElement e -> newReceiveInstance(localUID, remoteID, e);
        };
    }

    public static GenericEventInstance instantiate(String localUID, String remoteID,
                                                   EventElement baseElement) {
        return switch (baseElement) {
            case ComputationEventElement e ->
                    newComputationInstance(localUID, remoteID, e);
            case InputEventElement e -> newInputInstance(localUID, remoteID, e);
            case ReceiveEventElement e -> newReceiveInstance(localUID, remoteID, e);
        };
    }
}


sealed abstract class GenericEventInstance
        implements EventInstance
        permits ComputationInstance, InputInstance, ReceiveInstance {

    private static final class MutableMarking
            implements Marking, Serializable {

        @Serial
        private static final long serialVersionUID = 1894241710394083158L;
        private boolean hasExecuted, isPending, isIncluded;
        private Value value;

        static MutableMarking of(EventElement.MarkingElement marking) {
            return new MutableMarking(marking.hasExecuted(), marking.isPending(),
                    marking.isIncluded(), marking.value());
        }

        static MutableMarking of(Marking other) {
            return new MutableMarking(other.hasExecuted(), other.isPending(),
                    other.isIncluded(),
                    other.value());
        }

        private MutableMarking(boolean hasExecuted, boolean isPending, boolean isIncluded,
                               Value value) {
            this.hasExecuted = hasExecuted;
            this.isPending = isPending;
            this.isIncluded = isIncluded;
            this.value = value;
        }

        @Override
        public boolean hasExecuted() {return hasExecuted;}

        @Override
        public boolean isPending() {return isPending;}

        @Override
        public boolean isIncluded() {return isIncluded;}

        @Override
        public Value value() {return value;}

        @Override
        public String toString() {
            return String.format("(%b, %b, %b, %s)", hasExecuted, isPending, isIncluded,
                    value);
        }
    }

    // used locally/internally: uniquely identifies the event within the endpoint
    private final String localUID;
    // used for incoming requests to trigger events: identifies the event in a
    // context-dependent manner (computation/input/receive)
    private final String remoteID;
    private final EventElement baseElement;
    private final MutableMarking marking;
    private final long creationTimestamp;

    // TODO [revisit] not accounting for subtyping - it should - revisit Type
    // TODO [revisit] proper IllegalValueType exception
    static void trySetValue(MutableMarking marking, Value value) {
        if (!value.type().getClass().equals(marking.valueType().getClass())) {
            throw new RuntimeException("Bad Input val");
        }
        marking.value = value;
    }

    GenericEventInstance(String localUID, String remoteID, EventElement baseElement) {
        this.localUID = localUID;
        this.remoteID = remoteID;
        this.baseElement = baseElement;
        this.marking = MutableMarking.of(baseElement.marking());
        this.creationTimestamp = System.currentTimeMillis();
    }

    @Override
    public String localUID() {
        return localUID;
    }

    @Override
    public String remoteID() {
        return remoteID;
    }

    @Override
    public String label() {
        return baseElement.label();
    }

    @Override
    public Marking marking() {
        return marking;
    }

    @Override
    public EventElement baseElement() {
        return baseElement;
    }

    @Override
    public Optional<? extends UserSetExpression> remoteParticipants() {
        return baseElement.remoteParticipants();
    }

    @Override
    public long creationTimestamp() {
        return creationTimestamp;
    }

    void onExecuted(Value value) {
        trySetValue(marking, value);
        marking.hasExecuted = true;
        marking.isPending = false;
    }

    void onResponse() {marking.isPending = true;}

    void onInclude() {marking.isIncluded = true;}

    void onExclude() {marking.isIncluded = false;}

    abstract String unparse(String indentation);
}

/**
 * @param
 */
final class ComputationInstance
        extends GenericEventInstance
        implements ComputationEventInstance {
    // TODO [remove] this is a temporary patch for demo purposes
    UserSetVal receivers;

    ComputationInstance(String localUID, String remoteID,
                        ComputationEventElement baseElement, UserSetVal receivers) {
        super(localUID, remoteID, baseElement);
        this.receivers = receivers;
    }

    ComputationInstance(String localUID, String remoteID,
                        ComputationEventElement baseElement) {
        this(localUID, remoteID, baseElement, new SetUnionVal(List.of()));
    }

    @Override
    public ComputationExpression computationExpression() {
        return ((ComputationEventElement) baseElement()).computationExpression();
    }

    @Override
    public Optional<? extends UserSetExpression> receivers() {return baseElement().remoteParticipants();}

    // TODO [remove] this is a temporary broken fix
    @Override
    public UserSetVal receiverUsers() {
        return receivers;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s(%s : %s) [%s] (%s) [%s]", localUID(),
                receivers().map(ignored -> "Tx").orElse("Local"),
                marking().toStringPrefix(),
                remoteID(), label(), computationExpression(), value(),
                receivers().map(r -> String.format("@self ->" + " %s", r))
                        .orElse("Local"));
    }

    public String unparse(String indentation) {
        return String.format("%s%s - (%s: %s) [%s] %s", indentation, localUID(),
                remoteID(), label(),
                computationExpression().toString(), marking());
    }
}

/**
 * @param
 */
final class InputInstance
        extends GenericEventInstance
        implements InputEventInstance {

    // TODO [remove] this is a temporary patch for demo purposes
    UserSetVal receivers;

    private InputInstance(String localUID, String remoteID, InputEventElement baseElement,
                          UserSetVal receivers) {
        super(localUID, remoteID, baseElement);
        this.receivers = receivers;
    }

    static InputInstance of(String localUID, String remoteID,
                            InputEventElement baseElement) {
        return new InputInstance(localUID, remoteID, baseElement,
                new SetUnionVal(List.of()));
    }

    static InputInstance of(String localUID, String remoteID,
                            InputEventElement baseElement, UserSetVal receivers) {
        return new InputInstance(localUID, remoteID, baseElement, receivers);
    }

    @Override
    public Optional<? extends UserSetExpression> receivers() {return baseElement().remoteParticipants();}

    // TODO [remove] this is a temporary broken fix
    @Override
    public UserSetVal receiverUsers() {
        return receivers;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s(%s : %s) [?:%s] (%s) [%s]",
                receivers().map(ignored -> "Tx").orElse("Local"), localUID(),
                marking().toStringPrefix(), remoteID(), label(), valueType(), value(),
                receivers().map(r -> String.format("@self -> %s", r)).orElse("Local"));
    }

    @Override
    public String unparse(String indentation) {
        return String.format("%sInput: %s  ( %s )", indentation, localUID(), value());
    }
}

/**
 * @param
 */
final class ReceiveInstance
        extends GenericEventInstance
        implements ReceiveEventInstance {


    // TODO [remove] this is a temporary patch for demo purposes
    UserSetVal initiators;

    private ReceiveInstance(String globalId, String localId, EventElement baseElement,
                            UserSetVal initiators) {
        super(globalId, localId, baseElement);
        this.initiators = initiators;
    }

    static ReceiveInstance of(String localUID, String remoteID,
                              ReceiveEventElement baseElement) {
        return new ReceiveInstance(localUID, remoteID, baseElement,
                new SetUnionVal(List.of()));
    }

    static ReceiveInstance of(String localUID, String remoteID,
                              ReceiveEventElement baseElement, UserSetVal initiators) {
        return new ReceiveInstance(localUID, remoteID, baseElement, initiators);
    }
//    ReceiveInstance(String globalId, String localId, EventElement baseElement,
//    UserSetVal initiators) {
//        super(globalId, localId, baseElement);
//    }

    // TODO [remove] this is a temporary broken fix
    @Override
    public UserSetVal initiatorUsers() {
        return initiators;
    }

    @Override
    public String toString() {
        return String.format("[RX] %s - %s(%s : %s) [%s] (%s) [%s]", localUID(),
                marking().toStringPrefix(), remoteID(), label(), valueType(), value(),
                remoteParticipants().map(r -> String.format("%s -> @self", r)).get());
    }

    @Override
    String unparse(String indent) {
        return String.format("%sReceive: %s  ( %s )", indent, localUID(), value());
    }

    // FIXME .get()
    @Override
    public UserSetExpression getSenderExpr() {
        return remoteParticipants().get();
    }
}