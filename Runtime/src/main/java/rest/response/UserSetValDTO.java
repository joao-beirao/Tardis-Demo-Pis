package rest.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY,
//        use = JsonTypeInfo.Id.NAME,
//        property = "type")
//@JsonSubTypes({
//        @JsonSubTypes.Type(UnitTypeDTO.class),
//        @JsonSubTypes.Type(BooleanTypeDTO.class),
//        @JsonSubTypes.Type(IntegerTypeDTO.class),
//        @JsonSubTypes.Type(StringTypeDTO.class),
//        @JsonSubTypes.Type(RecordTypeDTO.class)})
public record UserSetValDTO(
        @JsonProperty(value = "userVals", required = true) List<RoleValDTO> userVals) {
}

//@JsonTypeName(value = "RoleVal")
record RoleValDTO(@JsonProperty(value = "role", required = true) String role,
                  @JsonProperty(value = "constrainedParams",
                          required = true) Map<String, ValueDTO> constrainedParams,
                  @JsonProperty(value = "freeParams",
                          required = true) Set<String> freeParams) {}

