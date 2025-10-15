package rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public record EndpointDTO(@JsonProperty(value = "self") UserDTO self,
                          @JsonProperty(value = "events") List<EventDTO> events) {}
