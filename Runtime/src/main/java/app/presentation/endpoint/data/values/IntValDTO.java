package app.presentation.endpoint.data.values;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "intLit")
public record IntValDTO(@JsonProperty(value = "value", required = true) int value)
        implements ValueDTO {}
