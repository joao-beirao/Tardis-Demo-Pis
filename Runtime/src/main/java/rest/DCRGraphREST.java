package rest;

import jakarta.inject.Singleton;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.unl.di.novasys.babel.webservices.application.GenericWebServiceProtocol.WebServiceOperation;
import pt.unl.di.novasys.babel.webservices.rest.GenericREST;
import pt.unl.di.novasys.babel.webservices.utils.EndpointPath;
import pt.unl.di.novasys.babel.webservices.utils.GenericWebAPIResponse;
import pt.unl.di.novasys.babel.webservices.utils.PendingResponse;
import rest.request.InputRequestDTO;
import rest.resources.InputEventExecuteRequest;
import rest.response.Mappers;

import java.util.concurrent.TimeUnit;

@Singleton @Path(DCRGraphAPI.PATH) public class DCRGraphREST extends
        GenericREST implements DCRGraphAPI {

    private static final String ERROR_MESSAGE = "Internal server error!";
    private static final Logger logger = LogManager.getLogger(DCRGraphREST.class);

    public enum DCREndpoints implements EndpointPath {
        ENDPOINT_PROCESS("endpoint_process"),
        EVENT("eventId"),
        EVENTS("events"),
        ENABLE("enable"),
        COMPUTATION("computation"),
        INPUT("input");

        private final String endpointPath;

        DCREndpoints(String endpointPath) {
            this.endpointPath = endpointPath;
        }

        @Override
        public String getPath() {return endpointPath;}
    }

    public DCRGraphREST() {super();}

    @Override
    public Response endpointProcess() {
        logger.info("\n\n\nEndpoint process requested");

        return Response.status(Response.Status.OK).entity("all good")
                .type(MediaType.TEXT_PLAIN).build();
    }

    @Override
    public void endpointProcess(
            @Suspended AsyncResponse ar) {
        this.sendRequest(WebServiceOperation.READ, DCREndpoints.ENDPOINT_PROCESS, ar);
    }

    @Override
    public void getEvents(
            @Suspended AsyncResponse ar) {
        logger.info("\n\nReceived request: get all events");
        this.sendRequest(WebServiceOperation.READ, DCREndpoints.EVENTS, ar);
    }

    @Override
    public void getEvent(
            @Suspended AsyncResponse ar, String eventId) {
        logger.info("\n\nReceived request: get event {}", eventId);
        this.sendRequest(WebServiceOperation.READ, eventId, DCREndpoints.EVENT, ar);
    }

    @Override
    public void getEnableEvents(
            @Suspended AsyncResponse ar) {
        logger.info("\n\nReceived request: get enabled events");
        this.sendRequest(WebServiceOperation.READ, DCREndpoints.ENABLE, ar);
    }

    @Override
    public void executeComputationEvent(
            @Suspended AsyncResponse ar, String eventId) {
        logger.info("\n\nReceived request: execute computation event {}", eventId);
        ar.setTimeout(60, TimeUnit.SECONDS);
        this.sendRequest(WebServiceOperation.UPDATE, eventId, DCREndpoints.COMPUTATION,
                ar);
    }

    @Override
    public void executeInputEvent(
            @Suspended AsyncResponse ar, String eventId, InputRequestDTO request) {
        logger.info("\n\nReceived request: execute Input event {} with value {}", eventId,
                request.value());
        var value = Mappers.toValue(request.value());
        this.sendRequest(WebServiceOperation.UPDATE,
                new InputEventExecuteRequest(request.eventID(), value),
                DCREndpoints.INPUT, ar);
    }

    @Override
    public void triggerResponse(String opUID, GenericWebAPIResponse genericResponse) {
        PendingResponse pendingResponse = super.removePendingResponse(opUID);
        var restEndpoint = (DCREndpoints) pendingResponse.getRestEnpoint();
        var ar = pendingResponse.getAr();
        switch (restEndpoint) {
            case ENDPOINT_PROCESS:
                if (genericResponse == null) {
                    sendStatusResponse(ar, Response.Status.INTERNAL_SERVER_ERROR,
                            ERROR_MESSAGE);
                } else {sendResponse(ar, genericResponse);}
            case EVENTS:
            case ENABLE:
                if (genericResponse == null) {
                    sendStatusResponse(ar, Response.Status.INTERNAL_SERVER_ERROR,
                            ERROR_MESSAGE);
                } else {sendResponse(ar, genericResponse.getValue());}
            case COMPUTATION:
            case INPUT:
                if (genericResponse == null) {
                    sendStatusResponse(ar, Response.Status.INTERNAL_SERVER_ERROR,
                            ERROR_MESSAGE);
                } else {
                    sendStatusResponse(ar, genericResponse.getStatusCode(),
                            genericResponse.getMessage());
                }
            default:
                break;
        }
    }

    private void sendResponse(AsyncResponse ar, Object value) {
        Response response = Response.status(Response.Status.OK)
//                .header("Access-Control-Allow-Origin", "*")
//                .header("Access-Control-Allow-Credentials", "true")
//                .header("Access-Control-Allow-Methods",
//                        "GET, POST, PUT, DELETE, OPTIONS, HEAD")
//                .header("Access-Control-Allow-Headers",
//                        "origin, content-type, accept, authorization,access-control-allow-origin")
                .entity(value)
                .build();
        ar.resume(response);
    }

    private void sendStatusResponse(AsyncResponse ar, Response.Status statusCode,
                                    String message) {
        Response response =
                Response.status(statusCode)
//                        .header("Access-Control-Allow-Origin", "*")
//                        .header("Access-Control-Allow-Credentials", "true")
//                        .header("Access-Control-Allow-Methods",
//                                "GET, POST, PUT, DELETE, OPTIONS, HEAD")
//                        .header("Access-Control-Allow-Headers",
//                                "origin, content-type, accept, authorization,access-control-allow-origin")
                        .entity(message).build();
        ar.resume(response);
    }

}
