package app.presentation.endpoint.events;

import app.presentation.endpoint.data.computation.ComputationExprDTO;
import app.presentation.endpoint.events.participants.UserSetExprDTO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Collections;
import java.util.List;

@JsonTypeName("computationEvent")
public final class ComputationEventDTO
        extends EventDTO {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(value = "dataExpr", required = true)
    public final ComputationExprDTO dataExpr;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(value = "receivers")
    public final List<UserSetExprDTO> receivers;

    @JsonCreator
    public ComputationEventDTO(@JsonProperty(value = "common", required = true) Common common,
            @JsonProperty(value = "dataExpr", required = true) ComputationExprDTO dataExpr,
            @JsonProperty(value = "receivers") List<UserSetExprDTO> receivers) {
        super(common);
        this.dataExpr = dataExpr;
        this.receivers = (receivers == null || receivers.isEmpty())
                ? Collections.emptyList()
                : Collections.unmodifiableList(receivers);
    }

    // public ComputationEventDTO(
    //         @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "choreoElementUID", required =
    //                 true) String choreoElementUID,
    //         @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "id", required =
    //                 true) String id,
    //         @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "eventType",
    //                 required = true) EventTypeDTO eventType,
    //         @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "dataType",
    //                 required = true) TypeDTO dataType,
    //         @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "marking",
    //                 required = true) EventDTO.MarkingDTO marking,
    //         @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "dataExpr",
    //                 required = true) ComputationExprDTO dataExpr,
    //         @JsonProperty(value = "instantiationConstraint") Optional<ComputationExprDTO>
    //         instantiationConstraint,
    //         @JsonProperty(value = "ifcConstraint") Optional<ComputationExprDTO> ifcConstraint,
    //         @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "receivers")
    //         List<UserSetExprDTO> receivers) {
    //     super(choreoElementUID, id, eventType, dataType, marking, instantiationConstraint, ifcConstraint);
    //     this.dataExpr = dataExpr;
    //     this.receivers = (receivers == null || receivers.isEmpty())
    //             ? Collections.emptyList()
    //             : Collections.unmodifiableList(receivers);
    // }
}