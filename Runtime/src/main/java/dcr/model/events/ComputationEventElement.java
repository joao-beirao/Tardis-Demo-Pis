package dcr.model.events;

import dcr.common.data.computation.BooleanExpression;
import dcr.common.data.computation.ComputationExpression;
import dcr.common.events.userset.expressions.UserSetExpression;
import dcr.runtime.elements.events.ComputationEventInstance;

import java.util.Optional;
import java.util.function.Function;

public sealed interface ComputationEventElement
        extends EventElement, dcr.common.events.ComputationEvent
        permits ComputationEvent {

    @Override
    Optional<UserSetExpression> receivers();
}

final class ComputationEvent
        extends GenericEventElement
        implements ComputationEventElement {
    private final ComputationExpression computationExpression;
    Function<ComputationEvent, ComputationEventInstance> builder;

    ComputationEvent(String choreoElementUID, String endpointElementUID, String localId, String eventType,
            ComputationExpression computationExpression, MarkingElement marking,
            UserSetExpression receivers, BooleanExpression instantiationConstraint,
            BooleanExpression ifcConstraint) {
        super(choreoElementUID, endpointElementUID, localId, eventType, marking, receivers,
                instantiationConstraint,
                ifcConstraint);
        this.computationExpression = computationExpression;
    }

    @Override
    public ComputationExpression computationExpression() {
        return computationExpression;
    }

    @Override
    public Optional<UserSetExpression> receivers() {
        return remoteParticipants();
    }

    @Override
    public String toString() {
        return String.format("<%s, %s> %s(%s: %s) [%s] (%s) [%s] (when: %s)", choreoElementUID(),
                endpointElementUID(),
                this.marking().toStringPrefix(), remoteID(), label(), computationExpression(),
                value(), receivers().map(r -> String.format("@self -> %s", r)).orElse(
                "Local"), instantiationConstraint());
    }

    @Override
    public String unparse() {
        return String.format("ComputationEventElement<%s>[ (%s: %s) [%s] [%s] ]", choreoElementUID(),
                remoteID(), label(), computationExpression(), receivers());
    }
}


