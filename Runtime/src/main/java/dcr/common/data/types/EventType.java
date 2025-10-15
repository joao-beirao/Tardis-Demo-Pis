package dcr.common.data.types;

import java.util.Objects;

// TODO [sanitize fields]
// TODO [javadoc]

/**
 * @param typeAlias
 */
public record EventType(String typeAlias)
        implements Type, DereferableType {

    public static  EventType of(String typeAlias) {
        return new EventType(typeAlias);
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(typeAlias);
    }

    // note: equality-check trusts the contract, where an event's label determines its value type;
    // TODO [enforced at runtime - TypeRegister]
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null) {return false;}
        if(obj instanceof EventType(String typeAlias1)) {
            return typeAlias.equalsIgnoreCase(typeAlias1);
        }
        return false;
    }

    // TODO [tostring ?]
}
