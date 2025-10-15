package app.presentation.endpoint.data.computation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("intLit")
public record IntLiteralDTO(@JsonProperty(value = "value", required = true) int value)
        implements ComputationExprDTO {

    @NotNull
    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
