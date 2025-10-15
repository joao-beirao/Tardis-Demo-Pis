package dcr.common.data.computation;


import dcr.common.Environment;
import dcr.common.data.types.Type;
import dcr.common.data.values.EventVal;
import dcr.common.data.values.Value;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// Current implementation assumes that a computation expression only dereferences the value
// property of an Event - which is the only one currently supported
// TODO [sanitize args]
public record EventValueDeref(
        ComputationExpression eventExpr) {
    //     implements ComputationExpression {
    //
    // public static <T extends Type> EventValueDeref of(
    //         ComputationExpression eventExpr) {
    //     return new EventValueDeref(eventExpr);
    // }
    //
    // public EventValueDeref {Objects.requireNonNull(eventExpr);}
    //
    // // TODO wrap in try-catch-throw->IllegalStateException - flag implementation error
    // @Override
    // public Value eval(Environment<Value> env) {
    //     var evalResult = eventExpr.eval(env);
    //     if (evalResult instanceof EventVal eventVal) {
    //         return (Value) eventVal.value();
    //     }
    //     // there are currently no other Values of EventType
    //     throw new IllegalStateException(
    //             "Internal Error: expecting an EventVal value, but got " + "this instead: " +
    //                     evalResult);
    // }
    //
    //
    // @NotNull
    // @Override
    // public String toString() {
    //     return String.format("%s.value", eventExpr);
    // }

}
