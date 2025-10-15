package dcr.common.data.computation;

import dcr.common.Environment;
import dcr.common.data.values.PropBasedVal;
import dcr.common.data.values.Value;

import javax.annotation.Nonnull;

// A leaf-node expression holding an event identifier (e.g., e1)
public record RefExpr(String eventId)
        implements PropBasedExpr {

    @Override
    public PropBasedVal eval(Environment<Value> env) {
        return (PropBasedVal) env.lookup(eventId)
                .map(Environment.Binding::value)
                .orElseThrow(() -> new IllegalStateException(
                        "Internal Error: Environment was expected to contain a binding for " +
                                "event " + eventId));
    }

    @Nonnull
    @Override
    public String toString() {return eventId;}
}