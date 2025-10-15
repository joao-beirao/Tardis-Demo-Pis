package dcr.common.events.userset.values;


import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// not yet in use
public record SetDiffVal(UserSetVal positiveSet, UserSetVal negativeSet)
        implements UserSetVal {

    public static SetDiffVal of(UserSetVal positiveSet, UserSetVal negativeSet) {
        return new SetDiffVal(positiveSet, negativeSet);
    }

    @NotNull
    @Override
    public String toString() {
        return String.format("%s \\ %s", positiveSet, negativeSet);
    }
}