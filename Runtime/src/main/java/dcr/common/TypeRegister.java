package dcr.common;

import dcr.common.data.types.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// TODO [likely discard]
public final class TypeRegister {


    private static final class Register {
        static final Map<String, Type> mapping = new HashMap<>();
    }

    private TypeRegister() {}

    public static Optional<Type> register(String label, Type type) {
        return Optional.ofNullable(Register.mapping.put(label, type));
    }

    public static Optional<Type> typeOf(String label) {
        return Optional.ofNullable(Register.mapping.get(label));
    }
}
