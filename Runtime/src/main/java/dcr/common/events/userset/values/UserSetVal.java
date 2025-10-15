package dcr.common.events.userset.values;

public sealed interface UserSetVal
        permits RoleVal, SetDiffVal, SetUnionVal, UserVal {
}
