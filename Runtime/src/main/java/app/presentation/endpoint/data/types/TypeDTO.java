package app.presentation.endpoint.data.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(ValueTypeDTO.class),
        @JsonSubTypes.Type(RecordTypeDTO.class),
        @JsonSubTypes.Type(EventTypeDTO.class),
}
)
public sealed interface TypeDTO
        permits RefTypeDTO, ValueTypeDTO {
}
