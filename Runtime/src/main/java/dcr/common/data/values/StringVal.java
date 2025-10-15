package dcr.common.data.values;

import dcr.common.data.types.StringType;

import java.io.Serial;
import java.util.Objects;

// TODO [check args] reminder - catch empty string and return singleton

/**
 * An immutable String literal {@link Value value}
 */
public final class StringVal
        implements PrimitiveVal, Comparable<StringVal> {
    @Serial
    private static final long serialVersionUID = -617630784691869772L;
    private static final StringVal EMPTY_STRING = new StringVal("");
    // private static final Undefined<StringType> UNDEFINED = new Undefined<>(StringType.singleton());
    private final String value;

    public static StringVal of(String value) {
        return Objects.requireNonNull(value).isEmpty() ? EMPTY_STRING : new StringVal(value);
    }

    public static StringVal empty() {return EMPTY_STRING;}

    // public static Undefined<StringType> undefined() {return UNDEFINED;}


    private StringVal(String value) {this.value = value;}


    public String value() {return value;}

    @Override
    public StringType type() {
        return StringType.singleton();
    }

    // TODO have a compare with StringLiteral and base it on value()
    @Override
    public int compareTo(StringVal other) {return this.value.compareToIgnoreCase(other.value);}

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null) {return false;}
        if (getClass() != obj.getClass()) {return false;};
        return value.equals(((StringVal)obj).value);
    }

    @Override
    public String toString() {return String.format("'%s'", value);}
}
