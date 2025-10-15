package dcr.common.data.computation;

import dcr.common.data.values.Value;
import dcr.common.Environment;

public sealed interface ComputationExpression
        permits BinaryOpExpr,
                BoolLiteral,
                BooleanExpression,
                IfThenElseExpr,
                IntLiteral,
                NegationExpr,
                PropBasedExpr,
                PropDerefExpr,
                StringLiteral {

    @Override
    String toString();

    Value eval(Environment<Value> env);
}



