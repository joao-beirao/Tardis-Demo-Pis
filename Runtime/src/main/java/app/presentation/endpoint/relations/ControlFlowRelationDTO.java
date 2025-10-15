package app.presentation.endpoint.relations;

import com.fasterxml.jackson.annotation.*;

@JsonTypeName("controlFlowRelation")
public record ControlFlowRelationDTO(
        @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "relationCommon", required =
                true) RelationDTO.Common common,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "targetId", required =
                true) String targetId,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "relationType",
                required = true) RelationTypeDTO relationType)
        implements RelationDTO {

    public enum RelationTypeDTO {
        INCLUDE("include"), EXCLUDE("exclude"), RESPONSE("response"), CONDITION(
                "condition"), MILESTONE("milestone");

        @JsonProperty(value = "op", required = true)
        private final String value;

        @JsonCreator
        RelationTypeDTO(@JsonProperty(value = "dataExpr", required = true) String value) {
            this.value = value;
        }

        @JsonValue
        public String value() {return value;}
    }
}

