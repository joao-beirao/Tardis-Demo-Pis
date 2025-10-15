package dcr.common.data.types;

public sealed interface PrimitiveType extends Type
        permits BooleanType, IntegerType, StringType, VoidType {

    // TODO move default hashcode and equals in here
}
