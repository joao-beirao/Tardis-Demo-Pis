package dcr.model.events;

import dcr.common.data.computation.BooleanExpression;
import dcr.common.events.userset.expressions.UserSetExpression;
import dcr.model.GenericElement;

import java.util.Objects;
import java.util.Optional;

public sealed abstract class GenericEventElement
        extends GenericElement
        implements EventElement
        permits ComputationEvent, InputEvent, ReceiveEvent {

    private final String choreoElementUID;
    private final String localId;
    private final String label;
    private final MarkingElement initialMarking;
    private final UserSetExpression passiveParticipants;
    private final BooleanExpression instantiationConstraint;
    private final BooleanExpression ifcConstraint;


    GenericEventElement(String choreoElementUID, String endpointUID, String localId, String eventType,
            MarkingElement initialMarking, UserSetExpression passiveParticipants,
            BooleanExpression constraint, BooleanExpression ifcConstraint) {
        super(endpointUID);
        this.choreoElementUID = choreoElementUID;
        this.localId = Objects.requireNonNull(localId);
        this.label = Objects.requireNonNull(eventType);
        this.initialMarking = Objects.requireNonNull(initialMarking);
        this.passiveParticipants = passiveParticipants;
        this.instantiationConstraint = Objects.requireNonNull(constraint);
        this.ifcConstraint = ifcConstraint;
    }

    @Override
    public String choreoElementUID() {
        return choreoElementUID;
    }

    @Override
    public String remoteID() {
        return localId;
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public MarkingElement marking() {
        return initialMarking;
    }

    // TODO Revisit - make abstract, move to concrete class and adjust constructor
    @Override
    public Optional<UserSetExpression> remoteParticipants() {
        return Optional.ofNullable(passiveParticipants);
    }

    @Override
    public BooleanExpression instantiationConstraint() {
        return instantiationConstraint;
    }

    @Override
    public BooleanExpression ifcConstraint() {
        return ifcConstraint;
    }
}
