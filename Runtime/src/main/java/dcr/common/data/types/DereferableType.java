package dcr.common.data.types;

public sealed interface DereferableType extends Type
        permits EventType, RecordType {
}
