package dcr.common.data.values;

import dcr.common.data.types.Type;

import java.io.Serializable;

// TODO [?] consider @Overriding equals across values?
public sealed interface Value
        extends Serializable
        permits PrimitiveVal, PropBasedVal, UndefinedVal, VoidVal {
    Type type();
}
