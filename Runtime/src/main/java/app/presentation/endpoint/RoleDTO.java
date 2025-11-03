package app.presentation.endpoint;


import app.presentation.endpoint.data.types.TypeDTO;
import com.fasterxml.jackson.annotation.*;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonTypeName(value = "membershipDTO")
public record RoleDTO(@JsonProperty(value = "label", required = true) String label,
                      @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "params") List<ParamDTO> params) {
    @JsonCreator
    public RoleDTO(@JsonProperty(value = "label", required = true) String label,
                   @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "params") List<ParamDTO> params) {
        this.label = label;
        this.params = (params != null && !params.isEmpty())
                ? Collections.unmodifiableList(params)
                : Collections.emptyList();
    }

    public record ParamDTO(@JsonProperty(value = "name", required = true) String name,
                           @JsonProperty(value = "type", required = true) TypeDTO type) {
    }
}

