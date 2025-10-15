package dcr.common.data.computation;

import dcr.common.Environment;
import dcr.common.data.values.BoolVal;
import dcr.common.data.values.Value;

public final class NegationExpr
        implements ComputationExpression {

    public final ComputationExpression expr;

    private NegationExpr(ComputationExpression expr) {this.expr = expr;}

    public static NegationExpr of(ComputationExpression expr) {
        return new NegationExpr(expr);
    }

    @Override
    public BoolVal eval(Environment<Value> env) {
        return BoolVal.of(
                !((BoolVal)expr.eval(env)).value());
    }

    @Override
    public String toString() {
        return String.format("!%s", expr);
    }
}
