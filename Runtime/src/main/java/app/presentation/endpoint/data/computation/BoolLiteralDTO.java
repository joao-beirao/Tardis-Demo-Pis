package app.presentation.endpoint.data.computation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("boolLit")
public record BoolLiteralDTO(@JsonProperty(value = "value", required = true) boolean value)
        implements ComputationExprDTO {

    @NotNull
    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
