package dcr.common.events.userset.values;


import dcr.common.data.values.PrimitiveVal;
import dcr.common.Record;
import dcr.common.events.userset.RoleParams;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

// TODO [sanitize args]
public record RoleVal(String role, Record<PrimitiveVal> params, Set<String> unconstrainedParams)
        implements UserSetVal {

    public static RoleVal of(String role) {
        return new RoleVal(role, Record.empty(), Collections.emptySet());
    }

    // TODO make this a Record<PrimitiveVal>
    public static RoleVal of(String role, Record<PrimitiveVal> params, Set<String> unconstrainedParams) {
        return new RoleVal(role, params, unconstrainedParams);
    }

    @NotNull
    @Override
    public String toString() {
        return String.format("%s(%s,%s)", role, params, unconstrainedParams);
    }
}
