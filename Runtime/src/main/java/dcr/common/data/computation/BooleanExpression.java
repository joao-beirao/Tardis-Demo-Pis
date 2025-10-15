package dcr.common.data.computation;

import dcr.common.Environment;
import dcr.common.data.values.BoolVal;
import dcr.common.data.values.Value;
import org.jetbrains.annotations.NotNull;

// A convenience wrapper around a computation expression expected to return a boolean value
public record BooleanExpression(ComputationExpression expr) implements ComputationExpression {

    public static BooleanExpression of(ComputationExpression expr) {
        return new BooleanExpression(expr);
    }

    @Override
    public BoolVal eval(Environment<Value> env) {
        return (BoolVal)expr.eval(env);
    }

    @NotNull
    @Override
    public String toString() {
        return expr.toString();
    }

}
