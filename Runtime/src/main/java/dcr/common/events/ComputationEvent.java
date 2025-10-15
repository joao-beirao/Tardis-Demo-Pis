package dcr.common.events;

import dcr.common.data.computation.ComputationExpression;

// TODO [javadoc]

/**
 * A DCR Computation event
 */
public interface ComputationEvent
        extends LocallyInitiatedEvent {
    ComputationExpression computationExpression();
}


