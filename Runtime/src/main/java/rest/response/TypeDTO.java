package rest.response;

import com.fasterxml.jackson.annotation.*;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(UnitTypeDTO.class),
        @JsonSubTypes.Type(BooleanTypeDTO.class),
        @JsonSubTypes.Type(IntegerTypeDTO.class),
        @JsonSubTypes.Type(StringTypeDTO.class),
        @JsonSubTypes.Type(RecordTypeDTO.class)})
public sealed interface TypeDTO
        permits BooleanTypeDTO, IntegerTypeDTO, RecordTypeDTO, StringTypeDTO, UnitTypeDTO {
}

@JsonTypeName(value = "Unit")
record UnitTypeDTO() implements TypeDTO {
}

@JsonTypeName(value = "Boolean")
record BooleanTypeDTO() implements TypeDTO {
}

@JsonTypeName(value = "Number")
record IntegerTypeDTO() implements TypeDTO {
}

@JsonTypeName(value = "String")
record StringTypeDTO() implements TypeDTO {
}

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.NAME, property = "type")
@JsonTypeName("Record")
record RecordTypeDTO(
        @JsonProperty(value = "fields", required = true) Map<String, TypeDTO> fields) implements TypeDTO {
}

