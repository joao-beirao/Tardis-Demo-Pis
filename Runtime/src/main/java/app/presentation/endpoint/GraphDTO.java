package app.presentation.endpoint;

import app.presentation.endpoint.events.EventDTO;
import app.presentation.endpoint.relations.RelationDTO;
import com.fasterxml.jackson.annotation.*;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
public record GraphDTO(@JsonProperty(value = "events") List<EventDTO> events,
                       @JsonProperty(value = "relations") List<RelationDTO> relations) {
    @JsonCreator
    public GraphDTO(@JsonProperty(value = "events") List<EventDTO> events,
            @JsonProperty(value = "relations") List<RelationDTO> relations) {
        this.events = (events != null && !events.isEmpty())
                ? Collections.unmodifiableList(events)
                : Collections.emptyList();
        this.relations = (relations != null && !relations.isEmpty())
                ? Collections.unmodifiableList(relations)
                : Collections.emptyList();
    }
}