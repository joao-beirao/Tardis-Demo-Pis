package dcr.common.data.values;

import dcr.common.data.types.BooleanType;

import java.io.Serial;
import java.util.Objects;

/**
 * An immutable boolean literal {@link Value value}.
 */
public final class BoolVal
        implements PrimitiveVal {

    @Serial
    private static final long serialVersionUID = 3976540914858038158L;
    public static final BoolVal TRUE = new BoolVal(true);
    public static final BoolVal FALSE = new BoolVal(false);
    private final boolean value;

    public static BoolVal of(boolean value) {
        return value ? TRUE : FALSE;
    }

    private BoolVal(boolean value) {this.value = value;}

    /**
     * Retrieves the boolean literal enclosed by this Value
     *
     * @return the boolean literal enclosed by this Value
     */
    public boolean value() {
        return value;
    }

    @Override
    public BooleanType type() {return BooleanType.singleton();}

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null) {return false;}
        if (getClass() != obj.getClass()) {return false;};
        return value == (((BoolVal)obj).value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
