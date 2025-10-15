package dcr.common.data.computation;

import dcr.common.Environment;
import dcr.common.data.types.Type;
import dcr.common.data.values.BoolVal;
import dcr.common.data.values.Value;

public record IfThenElseExpr(
        ComputationExpression conditionExpr,
        ComputationExpression thenExpr,
        ComputationExpression elseExpr)
        implements ComputationExpression {

    public IfThenElseExpr {
        // TODO validate args
    }

    public static <T extends Type> IfThenElseExpr of(
            ComputationExpression conditionExpr,
            ComputationExpression thenExpr,
            ComputationExpression elseExpr) {
        return new IfThenElseExpr(conditionExpr, thenExpr, elseExpr);
    }

    public Value eval(Environment<Value> env) {
        return ((BoolVal)conditionExpr.eval(env)).value() ? thenExpr.eval(env) : elseExpr.eval(env);
    }

    @Override
    public String toString() {
        return String.format("%s ? %s : %s ", conditionExpr, thenExpr, elseExpr);
    }

}
