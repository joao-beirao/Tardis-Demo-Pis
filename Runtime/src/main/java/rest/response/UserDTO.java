package rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record UserDTO(@JsonProperty(value = "role")String role,
                      @JsonProperty(value = "params") Map<String, ValueDTO> params) {
}
