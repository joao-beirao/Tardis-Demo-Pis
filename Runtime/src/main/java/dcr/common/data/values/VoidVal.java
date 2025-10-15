package dcr.common.data.values;

import dcr.common.data.types.Type;
import dcr.common.data.types.VoidType;

import java.io.Serial;

//TODO deprecate
public final class VoidVal
        implements Value {
    @Serial
    private static final long serialVersionUID = -5587609113242528555L;
    private static final VoidVal INSTANCE = new VoidVal();


    private VoidVal() {}

    public static VoidVal instance() {return INSTANCE;}

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public Type type() {
        return VoidType.singleton();
    }
}
