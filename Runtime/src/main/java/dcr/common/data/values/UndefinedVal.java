package dcr.common.data.values;

import dcr.common.data.types.Type;
import dcr.common.data.types.VoidType;

import java.io.Serial;

/**
 * A special {@link Value} denoting the absence of value (or an undefined value) for a given type
 * (the  value of any event which has not yet executed and for which an initial default value has
 * not been provided.
 */
public final class UndefinedVal<T extends Type>
        implements Value {

    @Serial
    private static final long serialVersionUID = -1588707935989188517L;
    // private static final UndefinedVal<VoidType> VOID = new UndefinedVal<>(VoidType.singleton());
    private static final String TO_STRING_VAL = "<undefined value>";

    //  TODO [revise] any workaround to avoid storing typeInstance? thinking no
    private final T typeInstance;

    // public static UndefinedVal<VoidType> ofVoid() {
    //     return VOID;
    // }

    public static <T extends Type> UndefinedVal<T> of(T typeInstance) {
        return new UndefinedVal<>(typeInstance);
    }

    UndefinedVal(T typeInstance) {
        this.typeInstance = typeInstance;
    }

    @Override
    public T type() {
        return typeInstance;
    }

    @Override
    public int hashCode() {
        return typeInstance.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {return false;}
        if (this == obj) {return true;}
        if (getClass() != obj.getClass()) {return false;}
        return typeInstance.equals(((UndefinedVal<?>) obj).typeInstance);
    }

    @Override
    public String toString() {return TO_STRING_VAL;}
}
