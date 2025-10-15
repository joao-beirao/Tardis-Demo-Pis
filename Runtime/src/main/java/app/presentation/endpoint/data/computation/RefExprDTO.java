package app.presentation.endpoint.data.computation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("eventRef")
public record RefExprDTO(@JsonProperty(value = "value", required = true) String value)
        implements PropBasedExprDTO {
    @NotNull
    @Override
    public String toString() {
        return value();
    }
}
