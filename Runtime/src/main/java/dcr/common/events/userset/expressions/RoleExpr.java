package dcr.common.events.userset.expressions;

import dcr.common.Environment;
import dcr.common.Record;
import dcr.common.data.computation.ComputationExpression;
import dcr.common.data.values.PrimitiveVal;
import dcr.common.data.values.Value;
import dcr.common.events.userset.RoleParams;
import dcr.common.events.userset.values.RoleVal;
import dcr.common.events.userset.values.UserSetVal;
import dcr.common.events.userset.values.UserVal;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

// TODO [sanitize args]

public record RoleExpr(String role,
                       Set<String> unconstrainedParams, Record<ComputationExpression> constrainedParams)
        implements UserSetExpression {

    public static RoleExpr of(String role) {
        return new RoleExpr(role, Collections.emptySet(), Record.empty());
    }

    public static RoleExpr of(String role, Record<ComputationExpression> params) {
        return new RoleExpr(role, Collections.emptySet(), params);
    }

    public static RoleExpr of(String role,
            Set<String> unconstrainedParams, Record<ComputationExpression> params) {
        return new RoleExpr(role, unconstrainedParams, params);
    }

    @Override
    public UserSetVal eval(Environment<Value> valueEnv, Environment<Pair<UserVal, UserVal>> userEnv) {
        var evalParams = Record.<PrimitiveVal>builder();
        constrainedParams.fields()
                .stream()
                .map(field -> Record.Field.of(field.name(),
                        (PrimitiveVal) field.value().eval(valueEnv)))
                .forEach(evalParams::addField);
        return RoleVal.of(role, evalParams.build(), unconstrainedParams);
    }

    // TODO revisit to include unconstrainedParams
    @NotNull
    @Override
    public String toString() {
        return String.format("%s(%s)", role,
                constrainedParams.stream().map(p -> String.format("%s=%s", p.name(), p.value())).collect(
                        Collectors.joining(", ")));
    }
}
