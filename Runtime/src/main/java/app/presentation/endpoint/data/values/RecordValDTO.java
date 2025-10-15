package app.presentation.endpoint.data.values;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeName("recordVal")
public record RecordValDTO(
        @JsonProperty(value = "fields", required = true) List<FieldDTO> fields)
        implements ValueDTO {
    public record FieldDTO(@JsonProperty(value = "name", required = true) String name,
                           @JsonProperty(value = "value", required = true) ValueDTO value) {}
}
