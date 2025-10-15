package dcr.common.events.userset.values;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


// TODO [sanitize args]
public record SetUnionVal(Collection<? extends UserSetVal> userSetVals)
        implements UserSetVal {
    public SetUnionVal(Collection<? extends UserSetVal> userSetVals) {
        Objects.requireNonNull(userSetVals);
//        if (userSetVals.isEmpty()) {
//            throw new IllegalArgumentException(
//                    "Requires at least two expressions: have " + userSetVals.size());
//        }
        if (userSetVals.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("List argument contains null entries - not allowed");
        }
        this.userSetVals = List.copyOf(userSetVals);
    }

    public static SetUnionVal of(Collection<? extends UserSetVal> userSetVals) {
        return new SetUnionVal(userSetVals);
    }

    @Override
    public String toString() {
        return userSetVals.stream().map(Objects::toString).collect(Collectors.joining("{", ",",
                "}"));
    }
}
