package app.presentation.endpoint.data.computation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("stringLit")
public record StringLiteralDTO(@JsonProperty(value = "value", required = true) String value)
        implements ComputationExprDTO {}
