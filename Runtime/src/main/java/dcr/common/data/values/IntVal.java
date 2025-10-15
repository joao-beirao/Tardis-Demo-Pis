package dcr.common.data.values;

import dcr.common.data.types.IntegerType;

import java.io.Serial;
import java.util.Objects;


// TODO [revisit] maybe rename both here and ReGraDa? Number -> Integer

/**
 * An immutable integer literal {@link Value value}.
 */
public final class IntVal
        implements PrimitiveVal, Comparable<IntVal> {
    @Serial
    private static final long serialVersionUID = -6289916328707077719L;

    private final int value;

    public static IntVal of(int value) {return new IntVal(value);}

    private IntVal(int value) {this.value = value;}

    @Override
    public IntegerType type() {return IntegerType.singleton();}

    public int value() {
        return this.value;
    }

    @Override
    public int compareTo(IntVal other) {
        return Integer.compare(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null) {return false;}
        if (getClass() != obj.getClass()) {return false;}
        ;
        return value == (((IntVal) obj).value);
    }

    public String toString() {
        return String.valueOf(value);
    }
}
