package app.presentation.endpoint.events;

import app.presentation.endpoint.events.participants.UserSetExprDTO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName("receiveEvent")
public final class ReceiveEventDto
        extends EventDTO {
    @JsonProperty(value = "initiators", required = true)
    public final List<UserSetExprDTO> initiators;


    @JsonCreator
    public ReceiveEventDto(@JsonProperty(value = "common", required = true) Common common,
             @JsonInclude(JsonInclude.Include.NON_EMPTY) @JsonProperty(value = "initiators",
                    required = true) List<UserSetExprDTO> initiators) {
        super(common);
        this.initiators = initiators;
    }
}