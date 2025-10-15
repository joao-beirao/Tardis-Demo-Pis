package dcr.common.data.types;

import java.io.Serial;

// TODO [revisit]

/**
 * Void Type
 */
public final class VoidType
        implements PrimitiveType {

    @Serial
    private static final long serialVersionUID = 5082518372256734393L;
    private static final VoidType SINGLETON = new VoidType();
    private static final String TO_STRING_VAL = "Void";

    public static VoidType singleton() {return SINGLETON;}

    private VoidType() {}

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
