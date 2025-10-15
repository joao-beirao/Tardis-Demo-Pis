package dcr.common.data.computation;

import dcr.common.Environment;
import dcr.common.data.values.PropBasedVal;
import dcr.common.data.values.Value;


public sealed interface PropBasedExpr extends ComputationExpression
        permits RefExpr, RecordExpr {
    @Override
    PropBasedVal eval(Environment<Value> env);
}
