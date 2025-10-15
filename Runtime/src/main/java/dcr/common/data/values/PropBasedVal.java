package dcr.common.data.values;

public sealed interface PropBasedVal
        extends Value
        permits EventVal, RecordVal {

    Value fetchProp(String propName);
}
