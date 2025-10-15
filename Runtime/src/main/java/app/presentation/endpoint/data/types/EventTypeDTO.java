package app.presentation.endpoint.data.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "eventType")
public record EventTypeDTO(
        @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "label", required =
                true) String eventType)
        implements RefTypeDTO {}
