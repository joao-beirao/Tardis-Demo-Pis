package app.presentation.endpoint.events.participants;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jetbrains.annotations.NotNull;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeName("initiatorExpr")
public record InitiatorExprDTO(@JsonProperty(value = "eventId", required = true) String eventId)
        implements UserSetExprDTO {

    @NotNull
    @Override
    public String toString() {
        return String.format("@Initiator(%s)", eventId);
    }
}
