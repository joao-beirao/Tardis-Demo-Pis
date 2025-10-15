package protocols.application;

import dcr.common.Record;
import dcr.common.data.types.Type;
import dcr.model.GraphElement;

public record Endpoint(Role role, GraphElement graphElement) {
    public record Role(String roleName, Record<Type> params) {}
}
