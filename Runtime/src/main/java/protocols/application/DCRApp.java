package protocols.application;

import app.presentation.endpoint.EndpointsDTO;
import app.presentation.mappers.EndpointMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import dcr.common.Record;
import dcr.common.data.types.BooleanType;
import dcr.common.data.types.IntegerType;
import dcr.common.data.types.StringType;
import dcr.common.data.types.Type;
import dcr.common.data.values.*;
import dcr.common.events.Event;
import dcr.common.events.userset.values.UserSetVal;
import dcr.common.events.userset.values.UserVal;
import dcr.model.GraphElement;
import dcr.runtime.GraphRunner;
import dcr.runtime.communication.CommunicationLayer;
import dcr.runtime.communication.MembershipLayer;
import dcr.runtime.elements.events.EventInstance;
import dcr.runtime.monitoring.GraphObserver;
import dcr.runtime.monitoring.StateUpdate;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocols.application.requests.AppRequest;
import protocols.application.requests.InformationFlowException;
import protocols.dcr.DistributedDCRProtocol;
import pt.unl.di.novasys.babel.webservices.WebAPICallback;
import pt.unl.di.novasys.babel.webservices.application.GenericWebServiceProtocol;
import pt.unl.di.novasys.babel.webservices.utils.EndpointPath;
import pt.unl.di.novasys.babel.webservices.utils.GenericWebAPIResponse;
import pt.unl.fct.di.novasys.babel.exceptions.HandlerRegistrationException;
import pt.unl.fct.di.novasys.network.data.Host;
import rest.DCRGraphREST;
import rest.resources.EndpointReconfigurationRequest;
import rest.resources.InputEventExecuteRequest;
import rest.response.Mappers;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public final class DCRApp
        extends GenericWebServiceProtocol
        implements GraphObserver, CommunicationLayer {

    private static final Logger logger = LogManager.getLogger(DCRApp.class);

    private static final class LazyHolder {
        static final DCRApp INSTANCE = new DCRApp();
    }

    public static final String PROTOCOL_NAME = "DCRApp";
    public static final short PROTO_ID = 51;

    private static final int DEFAULT_PORT = 9000; // default port to listen on
    private static final String CLI_ROLE_ARG = "role";

    private GraphRunner runner = null;
    //    TODO [revisit] not being used at this point
    private Endpoint endpoint = null;

    public static DCRApp getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static UserVal instantiateSelf(Properties props, Endpoint.Role roleDecl) {
        return UserVal.of(roleDecl.roleName(), Record.ofEntries(roleDecl.params()
                .stream()
                .map(param -> fetchSelfParamField(props, param.name(), param.value()))
                .collect(Collectors.toMap(Record.Field::name, Record.Field::value))));
    }

    private static Record.Field<Value> fetchSelfParamField(Properties props, String key,
                                                           Type type) {
        var prop = props.getProperty(key);
        return Record.Field.of(key, switch (type) {
            case BooleanType ignored -> BoolVal.of(Boolean.parseBoolean(prop));
            case IntegerType ignored -> IntVal.of(Integer.parseInt(prop));
            case StringType ignored -> StringVal.of(prop);
            default -> throw new IllegalStateException(
                    "Unexpected value for membershipDTO param: " + type);
        });
    }

    private static Endpoint decodeEndpoint(String jsonEncodedEndpoint, String role)
            throws JsonProcessingException {
//        TODO use membershipDTO to deserialize
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        var deserializedEndpoints =
                objectMapper.readValue(jsonEncodedEndpoint, EndpointsDTO.class);
        for (String r : deserializedEndpoints.endpoints().keySet()) {
            System.err.println(r);
        }
        System.err.println("role: " + role);
        var deserializedEndpoint = deserializedEndpoints.endpoints().get(role);
        Objects.requireNonNull(deserializedEndpoint);
//        var deserializedEndpoint = objectMapper.readValue(jsonEncodedEndpoint,
//        EndpointDTO.class);
        return EndpointMapper.mapEndpoint(deserializedEndpoint);
    }

    private DCRApp() {
        super(PROTOCOL_NAME, PROTO_ID);
    }

    @Override
    public void init(Properties properties)
            throws HandlerRegistrationException, IOException {
        logger.info("Initializing DCRApp");

        registerRequestHandler(AppRequest.REQUEST_ID, this::uponReceiveDcrRequest);
        // observation: bootstrap requires parameterized membershipDTO parameters:
        // - a 'membershipDTO' param is required, and determines the membershipDTO this
        // endpoint should enact - based
        // on the membershipDTO, the json-encoded endpoint resource is loaded and used
        // to instantiate,
        // both the DCR Model and the Role for the actirve participant;
        // - similarly, CLI params are used to inject the runtime parameter values for the
        // membershipDTO (when applicable), and are expected to follow the parameter
        // names declared by the
        // selected endpoint.
        try (InputStream in = DCRApp.class.getResourceAsStream(
//                TODO revisit/clean up
                String.format("choreo.json", properties.getProperty(CLI_ROLE_ARG)))) {
//                String.format("%s.json", properties.getProperty(CLI_ROLE_ARG)))) {
            assert in != null;
            // the user associated with this endpoint
            UserVal self;
            // the behavior this endpoint will enact
            GraphElement graphElement;
            {
                // load the information required to deploy this endpoint
                var jsonEncodedEndpoint =
                        new String(in.readAllBytes(), StandardCharsets.UTF_8);
                var endpoint = decodeEndpoint(jsonEncodedEndpoint,
                        properties.getProperty(CLI_ROLE_ARG));
                this.endpoint = endpoint;
                // inject runtime parameters into self
                self = instantiateSelf(properties, endpoint.role());
                graphElement = endpoint.graphElement();
            }
            // aggregates CLI-based functionality and callbacks (replaceable with
            // GUI/REST/...)
            CLI cmdLineRunner = new CLI(this);
            // setup graph runner
            runner = new GraphRunner(self, this);
            runner.registerGraphObserver(this);
            runner.init(graphElement);
            // start CLI-based interaction
//            cmdLineRunner.init();
        } catch (Exception e) {
            logger.error("Caught runtime exception {}", e.getMessage());
            e.printStackTrace();
        }
    }


    // ========================================================================
    // CommunicationLayer  (callback to request communication to other nodes)
    // ========================================================================

    @Override
    public Set<UserVal> uponSendRequest(UserVal requester, String eventId,
                                        UserSetVal receivers,
                                        Event.Marking marking, String uidExtension) {
        var neighbours = DummyMembershipLayer.instance()
                .resolveParticipants(receivers)
                .stream()
                .filter(n -> !n.user().equals(requester))
                .collect(Collectors.toSet());
        var reachable = new HashSet<MembershipLayer.Neighbour>();
        neighbours.forEach(neighbour -> {
            if (deliverMessage(neighbour, eventId, marking, requester, uidExtension)) {
                reachable.add(neighbour);
            }
        });
        return reachable.stream().map(MembershipLayer.Neighbour::user)
                .collect(Collectors.toSet());
    }

    // ========================================================================
    // UI (GUI/CLI) callbacks
    // ========================================================================

    void onEndpointConfiguration(UserVal self, GraphElement graphElement) {
        runner = new GraphRunner(self, this);
        runner.registerGraphObserver(this);
        runner.init(graphElement);
        logger.info("Endpoint configuration completed.");
    }


    // current graph as string
    String onDisplayGraph() {
        return runner.toString();
    }

    // called from UI
    String onListEnabledEvents() {
        return runner.enabledEvents()
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
    }

    // returns currently enabled events
    List<EventInstance> getEnabledEvents() {
        return runner.enabledEvents();
    }

    // returns all currently instantiated events
    List<EventInstance> getAllEvents() {
        return runner.events();
    }

    // on request to execute a Computation event
    void onExecuteComputationEvent(String eventId) throws InformationFlowException {
        logger.info("Executing Computation event '{}'", eventId);
        try {
            runner.executeComputationEvent(eventId);
        } catch (InformationFlowException e) {
            throw new InformationFlowException(e.getMessage());
        } catch (Exception e) {
            logger.error("Error executing Computation Event '{}': {}", eventId,
                    e.getMessage());
            e.printStackTrace();
        }
    }

    // on request to execute an Input event
    void onExecuteInputEvent(String eventId, Value inputValue)
            throws InformationFlowException {
        logger.info("Executing Input event '{}' with input value {}", eventId,
                inputValue);
        try {
            runner.executeInputEvent(eventId, inputValue);
        } catch (InformationFlowException e) {
            throw new InformationFlowException(e.getMessage());
        } catch (Exception e) {
            logger.error("Error executing Input Event '{}': {}", eventId, e.getMessage());
            e.printStackTrace();
        }
    }

    // on request to execute an "empty" Input event
    void onExecuteInputEvent(String eventId) throws InformationFlowException {
        logger.info("Executing (void) Input event '{}'", eventId);
        try {
            runner.executeInputEvent(eventId);
        } catch (InformationFlowException e) {
            throw new InformationFlowException(e.getMessage());
        } catch (Exception e) {
            logger.error("Error executing empty Input Event '{}': {}", eventId,
                    e.getMessage());
            e.printStackTrace();
        }
    }

    // on request to execute a Receive event (called from Babel's internal network
    // after a remote event has been executed
    private void onExecuteReceiveEvent(GraphRunner runner, String eventId,
                                       Event.Marking marking,
                                       UserVal sender, String uidExtension) {
        logger.info("Executing Receive event '{}': received {}", eventId, marking);
        try {
            runner.onReceiveEvent(eventId, marking.value(), sender, uidExtension);
        } catch (Exception e) {
            logger.error("Error executing Receive Event '{}': {}", eventId,
                    e.getMessage());
            e.printStackTrace();
        }
    }

    // (Distributed-DCR protocol) propagate call to execute Rx events (following a Tx)
    private boolean deliverMessage(MembershipLayer.Neighbour receiver, String eventId,
                                   Event.Marking marking, UserVal user,
                                   String uidExtension) {
        try {
            String hostName = receiver.hostName();
            InetAddress targetAddr = InetAddress.getByName(hostName);
            Host destination = new Host(targetAddr, DEFAULT_PORT);
            var request =
                    new AppRequest(eventId, marking, destination, user, uidExtension);
            sendRequest(request, DistributedDCRProtocol.PROTO_ID);
            return true;
        } catch (UnknownHostException e) {
            logger.warn("Unable to deliver message: {}", e.getMessage());
            return false;
        }
    }

    /* =======================
     * DCR Protocol handlers
     * ======================= */

    // handle incoming (inner-process) DCRProtocol request (Rx)
    private void uponReceiveDcrRequest(AppRequest appRequest, short sourceProtocol) {
        try {
            onExecuteReceiveEvent(runner, appRequest.getEventId(),
                    appRequest.getMarking(),
                    appRequest.getSender(), appRequest.getIdExtensionToken());
            for (var socket : super.getWSInstances()) {
                socket.sendMessage(
                        Mappers.fromEndpoint(this.runner.self, this.getEnabledEvents()));
            }
        } catch (Exception e) {
            logger.error("Error reading command: {}", e.getMessage());
        }
    }

    /* =======================
     * Observer callback
     * ======================= */

    @Override
    public void onUpdate(List<StateUpdate> update) {
        logger.info("Observed state update");
    }

    /* =====================
     * WebService Handlers
     * ===================== */

    @Override
    protected void createAsync(String opUniqueID, Object o, WebAPICallback webAPICallback,
                               Optional<EndpointPath> endpointPath) {
        // not used in this application
        if (endpointPath.isEmpty()) {
            logger.info("Unexpected call from POST endpoint");
        } else {
            logger.info("Unexpected call from POST endpoint: {}", endpointPath.get());
        }
    }

    @Override
    protected void updateAsync(String s, Object o, WebAPICallback webAPICallback,
                               Optional<EndpointPath> optional) {
        if (optional.isEmpty()) {return;}

        GenericWebAPIResponse response;
        switch ((DCRGraphREST.DCREndpoints) optional.get()) {
            case COMPUTATION:
                try {
                    this.onExecuteComputationEvent((String) o);
                    response = new GenericWebAPIResponse("Update computation event",
                            Response.Status.NO_CONTENT);

                } catch (InformationFlowException e) {
                    e.printStackTrace();
                    response = new GenericWebAPIResponse(e.getMessage(),
                            Response.Status.FORBIDDEN);
                }
                webAPICallback.triggerResponse(s, response);
                break;
            case INPUT:
                var input = (InputEventExecuteRequest) o;
                try {
                    Value val = input.value();
                    if (!(val instanceof VoidVal)) {
                        runner.executeInputEvent(input.eventId(), val);
                    } else {
                        runner.executeInputEvent(input.eventId());
                    }
                    logger.info("\n\n Executed update\n\n");
                    response = new GenericWebAPIResponse("Update input event",
                            Response.Status.NO_CONTENT);
                    webAPICallback.triggerResponse(s, response);
                } catch (InformationFlowException e) {
                    e.printStackTrace();
                    response = new GenericWebAPIResponse(e.getMessage(),
                            Response.Status.FORBIDDEN);
                    webAPICallback.triggerResponse(s, response);
                } catch (Exception e) {
                    logger.error("\nError executing Input Event '{}': {}\n",
                            input.eventId(), e.getMessage());
                    e.printStackTrace();
                    response = new GenericWebAPIResponse("Error executing input event",
                            Response.Status.INTERNAL_SERVER_ERROR);
                    webAPICallback.triggerResponse(s, response);
                }
                break;
            case RECONFIGURATION:
                var reconfiguration = (EndpointReconfigurationRequest) o;
                this.onEndpointConfiguration(reconfiguration.self(),
                        reconfiguration.graphElement());
                logger.info("Executed endpoint reconfiguration\n\n");
                response = new GenericWebAPIResponse("Update: endpoint reconfiguration",
                        Response.Status.NO_CONTENT);
                webAPICallback.triggerResponse(s, response);
                break;
            default:
                logger.info("Unexpected endpointPath call: {}", optional.get());
        }
        for (var socket : super.getWSInstances()) {
            socket.sendMessage(
                    Mappers.fromEndpoint(this.runner.self, this.getEnabledEvents()));
        }
    }

    @Override
    protected void readAsync(String opUniqueID, Object o, WebAPICallback webAPICallback,
                             Optional<EndpointPath> endpointPath) {
        if (endpointPath.isEmpty()) {return;}
        var path = (DCRGraphREST.DCREndpoints) endpointPath.get();
        List<EventInstance> events = this.getEnabledEvents();
        switch (path) {
            case ENDPOINT_PROCESS -> {
                var response =
                        new GenericWebAPIResponse("Returned endpoint-process", null);
                webAPICallback.triggerResponse(opUniqueID, response);
            }
            case ENABLE -> {
                events = this.getEnabledEvents();
                var response =
                        new GenericWebAPIResponse("Returned enabled events", events);
                webAPICallback.triggerResponse(opUniqueID, response);
            }
            case EVENTS -> {
                events = this.getAllEvents();
                var response = new GenericWebAPIResponse("Returned all events", events);
                webAPICallback.triggerResponse(opUniqueID, response);
            }
            default ->
                    logger.error("Unexpected endpointPath call: {}", endpointPath.get());

        }
        for (var socket : super.getWSInstances()) {
            socket.sendMessage(
                    Mappers.fromEndpoint(this.runner.self, events));
        }
    }

    @Override
    protected void deleteAsync(String s, Object o, WebAPICallback webAPICallback,
                               Optional<EndpointPath> endpointPath) {
        // not used in this application
        if (endpointPath.isEmpty()) {
            logger.info("Unexpected call from DELETE endpoint");
        } else {
            logger.info("Unexpected call from DELETE endpoint: {}", endpointPath.get());
        }
    }
}
