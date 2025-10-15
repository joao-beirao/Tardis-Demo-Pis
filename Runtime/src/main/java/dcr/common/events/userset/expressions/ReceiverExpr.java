package dcr.common.events.userset.expressions;

import dcr.common.Environment;
import dcr.common.data.values.Value;
import dcr.common.events.userset.values.UserSetVal;
import dcr.common.events.userset.values.UserVal;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// TODO [sanitize args]
public record ReceiverExpr(String eventId)
        implements UserSetExpression {

    public ReceiverExpr {
        Objects.requireNonNull(eventId);
    }

    public static ReceiverExpr of(String eventId) {
        return new ReceiverExpr(eventId);
    }

    @Override
    public UserSetVal eval(Environment<Value> valueEnv, Environment<Pair<UserVal, UserVal>> userEnv) {
        return userEnv.lookup(eventId)
                .orElseThrow(() -> new IllegalStateException(
                        "Internal Error: bad environment - missing binding for event " +
                                eventId))
                .value().getRight();
    }

    @NotNull
    @Override
    public String toString() {
        return String.format("@Receiver(%s)", eventId);
    }
}
