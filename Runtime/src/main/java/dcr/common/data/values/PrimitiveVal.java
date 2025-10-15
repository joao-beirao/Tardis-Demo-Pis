package dcr.common.data.values;

public sealed interface PrimitiveVal
        extends Value
        permits BoolVal, IntVal, StringVal {
}
