package app.presentation.endpoint.events;

import app.presentation.endpoint.data.computation.ComputationExprDTO;
import app.presentation.endpoint.data.types.TypeDTO;
import app.presentation.endpoint.data.values.ValueDTO;
import com.fasterxml.jackson.annotation.*;

import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({@JsonSubTypes.Type(ComputationEventDTO.class),
        @JsonSubTypes.Type(InputEventDTO.class), @JsonSubTypes.Type(ReceiveEventDto.class)})
public sealed abstract class EventDTO
        permits ComputationEventDTO, InputEventDTO, ReceiveEventDto {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)

    @JsonProperty(value = "common", required = true)
    public final Common common;

    @JsonCreator
    public EventDTO(
            @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "common", required
                    = true) Common common) {
        this.common = common;
    }

    public record Common(
            @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "choreoElementUID",
                    required = true) String choreoElementUID,
            @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "endpointElementUID"
                    , required = true) String endpointElementUID,
            @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "id", required =
                    true) String id,
            @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "label", required =
                    true) String label,
            @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "dataType",
                    required = true) TypeDTO dataType,
            @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "marking",
                    required = true) MarkingDTO marking,
            @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value =
                    "instantiationConstraint") Optional<ComputationExprDTO> instantiationConstraint,
            @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "ifcConstraint") Optional<ComputationExprDTO> ifcConstraint) {}


    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record MarkingDTO(
            @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "isIncluded",
                    required = true) boolean isIncluded,
            @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "isPending",
                    required = true) boolean isPending,
            @JsonProperty("defaultValue") Optional<ValueDTO> value) {}
}