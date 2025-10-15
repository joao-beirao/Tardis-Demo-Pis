package dcr.common.data.types;

import java.io.Serial;

// TODO [revisit] really need this?
// TODO [javadoc]

/**
 *
 */
public final class StringType
        implements PrimitiveType {

    @Serial
    private static final long serialVersionUID = -1661775326115213130L;
    private static final StringType INSTANCE = new StringType();
    private static final String TO_STRING_VAL = "String";


    public static StringType singleton() {return INSTANCE;}

    private StringType() {}

    // reminder: it's a singleton, therefore it's safe to delegate equals and hashcode


    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {return false;}
        if (this == obj) {return true;}
        // reminder: class equality suffices here (singleton)
        return getClass().equals(obj.getClass());
    }

    //TODO [make string consts]
    @Override
    public String toString() {return TO_STRING_VAL;}

    // @Override
    // public String unparse() {return "GenericStringType";}
}
