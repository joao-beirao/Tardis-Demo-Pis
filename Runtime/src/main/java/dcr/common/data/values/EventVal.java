package dcr.common.data.values;

import dcr.common.data.types.EventType;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;


// TODO remove type - add to Value interface a default so that a value can return the EventType
//  of an Event enclosing it
public record EventVal(Value value, EventType type)
        implements PropBasedVal {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null) {return false;}
        if (getClass() != obj.getClass()) {return false;};
        return value.equals(((EventVal)obj).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public Value fetchProp(String propName) {
        throw new NotImplementedException("TODO");
    }
}
