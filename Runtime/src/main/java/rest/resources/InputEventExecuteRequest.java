package rest.resources;

import dcr.common.data.values.*;

public record InputEventExecuteRequest(String eventId, Value value) {
}
