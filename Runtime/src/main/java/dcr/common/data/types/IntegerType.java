package dcr.common.data.types;

import java.io.Serial;

// TODO [javadoc]

/**
 * Integer Type
 */
public final class IntegerType
        implements PrimitiveType {
    @Serial
    private static final long serialVersionUID = 9032272504700169178L;
    private static final IntegerType INSTANCE = new IntegerType();
    private static final String TO_STRING_VAL = "Integer";

    public static IntegerType singleton() {return INSTANCE;}

    private IntegerType() {}

    @Override
    public int hashCode() {return 1;}

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {return false;}
        if (this == obj) {return true;}
        // reminder: class equality suffices here (singleton)
        return getClass().equals(obj.getClass());
    }

    @Override
    public String toString() {return TO_STRING_VAL;}
}
