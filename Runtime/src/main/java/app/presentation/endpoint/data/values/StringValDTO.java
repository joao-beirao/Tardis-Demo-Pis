package app.presentation.endpoint.data.values;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "stringLit")
public record StringValDTO(@JsonProperty(value = "value", required = true) String value)
        implements ValueDTO {}
