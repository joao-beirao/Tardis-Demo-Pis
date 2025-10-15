package app.presentation.endpoint.data.values;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "boolLit")
public record BoolValDTO(@JsonProperty(value = "value", required = true) boolean value)
        implements ValueDTO {}