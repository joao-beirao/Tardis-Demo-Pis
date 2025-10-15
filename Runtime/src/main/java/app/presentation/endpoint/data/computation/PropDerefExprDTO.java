package app.presentation.endpoint.data.computation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("propDeref")
public record PropDerefExprDTO(
        @JsonProperty(value = "propBasedExpr", required = true) PropBasedExprDTO expr,
        @JsonProperty(value = "prop", required = true) String prop
) implements PropBasedExprDTO {

    @NotNull
    @Override
    public String toString() {
        return String.format("%s.%s", expr, prop);
    }
}
