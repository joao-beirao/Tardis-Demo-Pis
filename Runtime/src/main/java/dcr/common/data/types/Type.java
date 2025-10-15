package dcr.common.data.types;

import java.io.Serializable;

// TODO [javadoc] note: all implementors expected to override hashcode, equals, and toString
public sealed interface Type
        extends Serializable
        permits DereferableType, EventType, PrimitiveType, RecordType
{}
