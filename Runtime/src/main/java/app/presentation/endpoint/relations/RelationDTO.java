package app.presentation.endpoint.relations;

import app.presentation.endpoint.data.computation.ComputationExprDTO;
import com.fasterxml.jackson.annotation.*;

import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({@JsonSubTypes.Type(ControlFlowRelationDTO.class),
        @JsonSubTypes.Type(SpawnRelationDTO.class)})
public sealed interface RelationDTO
        permits ControlFlowRelationDTO, SpawnRelationDTO {

    record Common(
            @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "endpointElementUID"
                    , required = true) String endpointElementUID,
            @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "sourceId",
                    required = true) String sourceId,
            @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "guard") Optional<ComputationExprDTO> guard,
            @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value =
                    "instantiationConstraint") Optional<ComputationExprDTO> instantiationConstraint) {}

    Common common();


}
