package rest.request;

import app.presentation.endpoint.EndpointsDTO;
import app.presentation.endpoint.data.values.ValueDTO;

import java.util.Map;

public record EndpointReconfigurationRequestDTO(MembershipDTO membershipDTO, EndpointsDTO endpointsDTO) {

    public record MembershipDTO(String role, Map<String, ValueDTO> params) {
    }
}
