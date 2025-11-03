package rest.resources;

import dcr.common.events.userset.values.UserVal;
import dcr.model.GraphElement;

public record EndpointReconfigurationRequest(UserVal self, GraphElement graphElement) {
}
