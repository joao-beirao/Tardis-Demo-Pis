package dcr.common.events.userset;

import dcr.common.Record;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;

public record RoleParams<V>(@Nonnull Record<? extends V> params)
        implements Serializable {

    public static <V> RoleParams<V> of(@Nonnull Record<? extends V> params) {
        return new RoleParams<>(params);
    }

    public static <V> RoleParams<V> empty() {
        return new RoleParams<>(Record.empty());
    }

    public RoleParams {Objects.requireNonNull(params);}

    @Nonnull
    @Override
    public String toString() {
        return params().toString();
    }
}