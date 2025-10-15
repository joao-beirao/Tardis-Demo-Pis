package protocols.application.requests;

import dcr.common.events.Event;
import dcr.common.events.userset.values.UserVal;
import pt.unl.fct.di.novasys.babel.generic.ProtoRequest;
import pt.unl.fct.di.novasys.network.data.Host;

import java.util.Objects;

public class AppRequest
        extends ProtoRequest {

    public static final short REQUEST_ID = 1;
    private final Host destination;
    private final String eventId;
    private final Event.Marking marking;
    private final UserVal user;
    private final String idExtensionToken;

    // TODO [sanitize args]
    public AppRequest(String eventId, Event.Marking marking, Host destination, UserVal user,
            String idExtensionToken) {
        super(REQUEST_ID);
        this.destination = destination;
        this.eventId = eventId;
        this.marking = Objects.requireNonNull(marking);
        this.user = user;
        this.idExtensionToken = idExtensionToken;
    }

    public Host getDestination() {
        return destination;
    }

    public String getEventId() {
        return eventId;
    }

    public Event.Marking getMarking() {
        return marking;
    }

    public UserVal getSender() {return user;}

    public String getIdExtensionToken() {return idExtensionToken;}

    /*
     * public DcrReply produceReply(String receiver, long rtt) {
     * return new DcrReply(message, receiver, destination, rtt);
     * }
     */

}
