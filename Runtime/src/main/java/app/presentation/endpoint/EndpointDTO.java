package app.presentation.endpoint;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
public record EndpointDTO(
        @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "role", required =
                true) RoleDTO role,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "graph", required =
                true) GraphDTO graph
) {

}
