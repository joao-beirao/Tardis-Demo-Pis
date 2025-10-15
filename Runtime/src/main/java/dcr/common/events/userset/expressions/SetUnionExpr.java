package dcr.common.events.userset.expressions;


import dcr.common.Environment;
import dcr.common.data.values.Value;
import dcr.common.events.userset.values.SetUnionVal;
import dcr.common.events.userset.values.UserVal;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// TODO [clarify] thinking of this as a generic catch all to represent a set of users from any
//  given role, which should filter out duplicates on eval
public record SetUnionExpr(Collection<? extends UserSetExpression> userSetExprs)
        implements UserSetExpression {
    public SetUnionExpr(Collection<? extends UserSetExpression> userSetExprs) {
        Objects.requireNonNull(userSetExprs);
        if (userSetExprs.isEmpty()) {
            throw new IllegalArgumentException("Empty UnionSet expression not allowed");
        }
        if (userSetExprs.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("List argument contains null entries - not allowed");
        }
        this.userSetExprs = List.copyOf(userSetExprs);
    }

    public static SetUnionExpr of(Collection<? extends UserSetExpression> userSetExprs) {
        return new SetUnionExpr(userSetExprs);
    }

    // TODO - filter out duplicates
    @Override
    public SetUnionVal eval(Environment<Value> valueEnv,
            Environment<Pair<UserVal, UserVal>> userEnv) {
        return SetUnionVal.of(
                userSetExprs.stream().map(expr -> expr.eval(valueEnv, userEnv)).toList());
    }

    @NotNull
    @Override
    public String toString() {
        return userSetExprs.stream()
                .map(UserSetExpression::toString)
                .collect(Collectors.joining(","));
    }
}
