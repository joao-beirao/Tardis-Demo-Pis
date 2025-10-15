package app.presentation.endpoint.events.participants;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("userSetDiffExpr")
public record UserSetDiffExprDTO(
        @JsonProperty(value = "baseSet", required = true) UserSetExprDTO baseSet,
        @JsonProperty(value = "excludeSet", required = true) UserSetExprDTO excludeSet) implements
                                                                                        UserSetExprDTO {

    @NotNull
    @Override
    public String toString() {
        return String.format("%s\\%s", baseSet().toString(), excludeSet().toString());
    }
}
