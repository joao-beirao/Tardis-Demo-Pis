package rest.response;

import com.fasterxml.jackson.annotation.*;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(UnitDTO.class),
        @JsonSubTypes.Type(BooleanDTO.class),
        @JsonSubTypes.Type(IntDTO.class),
        @JsonSubTypes.Type(StringDTO.class),
        @JsonSubTypes.Type(RecordDTO.class)})
public sealed interface ValueDTO permits UnitDTO, BooleanDTO, IntDTO, StringDTO, RecordDTO {
}

@JsonTypeName(value = "Unit")
record UnitDTO(
        @JsonProperty(value = "value", required = true) String value) implements ValueDTO {
}

@JsonTypeName(value = "Boolean")
record BooleanDTO(
        @JsonProperty(value = "value", required = true) boolean value) implements ValueDTO {
}

@JsonTypeName(value = "Number")
record IntDTO(
        @JsonProperty(value = "value", required = true) int value) implements ValueDTO {
}

@JsonTypeName(value = "String")
record StringDTO(
        @JsonProperty(value = "value", required = true) String value) implements ValueDTO {
}

@JsonTypeName(value = "Record")
record RecordDTO(
        @JsonProperty(value = "value", required = true) Map<String, ValueDTO> value) implements ValueDTO {
}