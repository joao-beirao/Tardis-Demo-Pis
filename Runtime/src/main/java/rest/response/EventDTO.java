package rest.response;

import com.fasterxml.jackson.annotation.*;

public class EventDTO {
    private static final String COMPUTATION_ACTION = "computation";
    private static final String INPUT_ACTION = "input";

    @JsonProperty(value = "id", required = true)
    private final String id;
    @JsonProperty(value = "label", required = true)
    private final String label;
    @JsonProperty(value = "action", required = true)
    private final String action;
    @JsonProperty(value = "kind", required = true)
    private final KindDTO kind;
    @JsonProperty(value = "receivers", required = false)
    private final UserSetValDTO receivers;
    @JsonProperty(value = "marking", required = true)
    private final MarkingDTO marking;
    @JsonProperty(value = "typeExpr", required = true)
    private final TypeDTO typeExpr;
    @JsonProperty(value = "timestamp", required = true)
    private final long timestamp;

    private EventDTO(String id, String label, String action, UserSetValDTO receivers, TypeDTO typeExpr, KindDTO kind,
                     MarkingDTO marking, long timestamp) {
        this.id = id;
        this.label = label;
        this.action = action;
        this.receivers = receivers;
        this.typeExpr = typeExpr;
        this.kind = kind;
        this.marking = marking;
        this.timestamp = timestamp;
    }

    static EventDTO newComputationEventDTO(String id, String label, UserSetValDTO receivers, TypeDTO typeExpr, KindDTO kind,
                                           MarkingDTO marking, long timestamp) {
        return new EventDTO(id, label, COMPUTATION_ACTION, receivers,typeExpr, kind, marking, timestamp);
    }

    static EventDTO newInputEventDTO(String id, String label, UserSetValDTO receivers, TypeDTO typeExpr, KindDTO kind,
                                     MarkingDTO marking, long timestamp) {
        return new EventDTO(id, label, INPUT_ACTION, receivers ,typeExpr, kind, marking, timestamp);
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
record MarkingDTO(
        @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "hasExecuted",
                required = true) boolean hasExecuted,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "isPending",
                required = true) boolean isPending,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "isIncluded",
                required = true) boolean isIncluded,
        @JsonProperty("value") ValueDTO value) {
}

enum KindDTO {
    COMPUTATION("computation-action"), COMPUTATION_SEND("computation-send"),
    INPUT_SEND("input-send"), INPUT("input-action");

    @JsonProperty(value = "value")
    private final String value;

    @JsonCreator KindDTO(@JsonProperty(value = "value") String v) {
        value = v;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}