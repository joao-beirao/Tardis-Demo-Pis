package app.presentation.endpoint;

import app.presentation.endpoint.events.EventDTO;
import com.fasterxml.jackson.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
//@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
public record EndpointsDTO(Map<String, EndpointDTO> endpoints) {

//    public EndpointsDTO(Map<String, EndpointDTO> endpoints) {
//        this.endpoints = endpoints;
//    }

    @JsonCreator
    public EndpointsDTO(List<EndpointDTO> endpoints) {
        this ((endpoints != null && !endpoints.isEmpty())
                ? endpoints.stream().collect(Collectors.toMap(endpoint -> endpoint.role().label(),
                Function.identity()))
//                ? Collections.unmodifiableList(endpoints)
                : Collections.emptyMap());
    }
}
