package dcr.common.data.types;

// TODO [revisit - deprecate]
// something that needs to be looked up in the environment and promises to point to something of
// type T
public record ConstRefType<T extends Type>(T type)
        // implements Type, RefType<T>
{

    public static <T extends Type> ConstRefType<T> of(T type) {
        return new ConstRefType<>(type);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {return true;}
        if (other == null) {return false;}
        // TODO [revisit] maybe use getClass to check same type of ref (const vs non-const) - or
        //  maybe this is useless
        return other instanceof ConstRefType<?> oConstRefType && type.equals(oConstRefType.type);
    }

    @Override
    public String toString() {
        return type.toString();
    }

    // @Override
    // public String unparse() {
    //     return String.format("RefType(%s)", type.unparse());
    // }

    // TODO [discard tests]
    public static void main(String[] args) {

    }
}
