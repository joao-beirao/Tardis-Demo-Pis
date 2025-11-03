package rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import rest.request.EndpointReconfigurationRequestDTO;
import rest.request.InputRequestDTO;

public interface DCRGraphAPI {
    String PATH = "/dcr";
    String ENDPOINT = "/endpoint";
    String RECONFIGURATION = "/reconfiguration";
    String EVENTS = "/events";
    String ENABLE = "/enable";
    String COMPUTATION = "/computation";
    String INPUT = "/input";
    String EVENT_ID = "eventId";

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    Response endpointProcess();

    @GET
    @Path("/endpoint")
    @Produces(MediaType.TEXT_PLAIN)
    void endpointProcess(
            @Suspended
            final AsyncResponse ar);

    @GET
    @Path(EVENTS)
    @Produces(MediaType.APPLICATION_JSON)
    void getEvents(
            @Suspended AsyncResponse ar);

    @GET
    @Path(EVENTS + "/{" + EVENT_ID + "}")
    @Produces(MediaType.APPLICATION_JSON)
    void getEvent(
            @Suspended AsyncResponse ar,
            @PathParam(EVENT_ID) String eventId);

    @GET
    @Path(EVENTS + ENABLE)
    @Produces(MediaType.APPLICATION_JSON)
    void getEnableEvents(
            @Suspended AsyncResponse ar);

    @PUT
    @Path(EVENTS + COMPUTATION + "/{" + EVENT_ID + "}")
    void executeComputationEvent(
            @Suspended AsyncResponse ar,
            @PathParam(EVENT_ID) String eventId);

    @PUT
    @Path(EVENTS + INPUT + "/{" + EVENT_ID + "}")
    @Consumes(MediaType.APPLICATION_JSON)
    void executeInputEvent(
            @Suspended AsyncResponse ar,
            @PathParam(EVENT_ID) String eventId,
            InputRequestDTO input);

    @PUT
    @Path(RECONFIGURATION)
    @Consumes(MediaType.APPLICATION_JSON)
    void reconfigureEndpoint(
            @Suspended AsyncResponse ar,
            EndpointReconfigurationRequestDTO input);
}
