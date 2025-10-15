package rest.request;

import rest.response.ValueDTO;

public record InputRequestDTO(String eventID, ValueDTO value) {}
