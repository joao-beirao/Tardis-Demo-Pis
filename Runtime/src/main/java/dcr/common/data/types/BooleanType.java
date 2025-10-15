package dcr.common.data.types;

import java.io.Serial;

// TODO [javadoc]
/**
 * Boolean Type
 */
public final class BooleanType
        implements PrimitiveType {
    @Serial
    private static final long serialVersionUID = -828418645904156111L;
    private static final BooleanType INSTANCE = new BooleanType();
    private static final String TO_STRING_VAL = "Boolean";

    public static BooleanType singleton() {return INSTANCE;}

    private BooleanType() {}

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
