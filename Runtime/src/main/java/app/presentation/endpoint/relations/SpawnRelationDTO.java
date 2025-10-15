package app.presentation.endpoint.relations;

import app.presentation.endpoint.GraphDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("spawnRelation")
public record SpawnRelationDTO(
        @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "relationCommon",
                required =
                true) Common common,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "triggerId", required
                = true) String triggerId,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "graph", required =
                true) GraphDTO graph)
        implements RelationDTO {}

