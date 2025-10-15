package protocols.dcr;


import dcr.common.events.Event;
import dcr.common.events.userset.values.UserVal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocols.application.DCRApp;
import protocols.application.requests.AppRequest;
import protocols.application.utils.NetworkingUtilities;
import protocols.dcr.messages.DCRRequest;
import protocols.dcr.messages.PongMessage;
import pt.unl.fct.di.novasys.babel.core.GenericProtocol;
import pt.unl.fct.di.novasys.babel.exceptions.HandlerRegistrationException;
import pt.unl.fct.di.novasys.babel.generic.ProtoMessage;
import pt.unl.fct.di.novasys.channel.tcp.TCPChannel;
import pt.unl.fct.di.novasys.channel.tcp.events.*;
import pt.unl.fct.di.novasys.network.data.Host;

import java.io.IOException;
import java.util.Properties;

public class DistributedDCRProtocol
        extends GenericProtocol {
    private static final Logger logger = LogManager.getLogger(DistributedDCRProtocol.class);

    public static final String PROTOCOL_NAME = "DistributedDCRProtocol";
    public static final short PROTO_ID = 1;
    public static final int DEFAULT_PORT = 9000;

    private int channelId;

    public DistributedDCRProtocol() {
        super(PROTOCOL_NAME, PROTO_ID);
    }

    public void init(Properties props) throws IOException, HandlerRegistrationException {
        Properties channelProps = new Properties();
        if (props.containsKey("interface")) {
            channelProps.setProperty(TCPChannel.ADDRESS_KEY,
                    NetworkingUtilities.getAddress(props.getProperty("interface")));
        }
        else if (props.containsKey("address")) {
            channelProps.setProperty(TCPChannel.ADDRESS_KEY, props.getProperty("address"));
        }
        else {
            channelProps.setProperty(TCPChannel.ADDRESS_KEY,
                    NetworkingUtilities.getAddress("eth0"));
        }
        // set network port to listen on
        if (props.containsKey("port")) {
            // if port is defined, used defined port
            channelProps.setProperty(TCPChannel.PORT_KEY, props.getProperty("port"));
        }
        else {
            channelProps.setProperty(TCPChannel.PORT_KEY, DEFAULT_PORT + "");
        }
        // create the channel with the provided properties
        channelId = createChannel(TCPChannel.NAME, channelProps);

        // TODO: Create new message class for the dcr event execution.

        // register protocol handlers
        // register channel event handlers
        registerChannelEventHandler(channelId, InConnectionDown.EVENT_ID,
                this::uponInConnectionDown);
        registerChannelEventHandler(channelId, InConnectionUp.EVENT_ID, this::uponInConnectionUp);
        registerChannelEventHandler(channelId, OutConnectionDown.EVENT_ID,
                this::uponOutConnectionDown);
        registerChannelEventHandler(channelId, OutConnectionUp.EVENT_ID, this::uponOutConnectionUp);
        registerChannelEventHandler(channelId, OutConnectionFailed.EVENT_ID,
                this::uponOutConnectionFailed);

        // register message serializers
        registerMessageSerializer(channelId, DCRRequest.MSG_ID, DCRRequest.serializer);
        registerMessageSerializer(channelId, PongMessage.MSG_ID, PongMessage.serializer);

        // register protocol handlers
        // register message handlers
        registerMessageHandler(channelId, DCRRequest.MSG_ID, this::uponReceivedDcrMessage,
                this::uponMessageFailed);
        registerMessageHandler(channelId, PongMessage.MSG_ID, this::uponReceivePongMessage,
                this::uponMessageFailed);

        // register request handlers
        registerRequestHandler(AppRequest.REQUEST_ID, this::uponSendRequest);

        logger.info("DcrProtocol initialized, running on "
                + channelProps.getProperty(TCPChannel.ADDRESS_KEY) + ":"
                + channelProps.getProperty(TCPChannel.PORT_KEY));
    }

    private void uponSendRequest(AppRequest appRequest, short sourceProtocol) {
        logger.info("Sending request to {} with message {} and extension {}",
                appRequest.getDestination(), appRequest.getEventId(),
                appRequest.getIdExtensionToken());
        var msg = new DCRRequest(appRequest.getEventId(), appRequest.getMarking(),
                appRequest.getSender(), appRequest.getIdExtensionToken());
        openConnection(appRequest.getDestination(), channelId);
        sendMessage(channelId,
                msg,
                appRequest.getDestination());
        // closeConnection(dcrRequest.getDestination(), channelId);
    }

    /**
     * Handle when an open connection operation succeeded Start the periodic timer to send Ping
     * pingpong.messages
     *
     * @param event
     *         OutConnectionUp event
     * @param channel
     *         Channel ID
     */
    private void uponOutConnectionUp(OutConnectionUp event, int channel) {
        logger.debug("Connection to {} is now up", event.getNode());
    }

    /**
     * Handle when an open connection operation has failed Print error message and exit
     *
     * @param event
     *         OutConnectionFailed event
     * @param channel
     *         Channel ID
     */
    private void uponOutConnectionFailed(OutConnectionFailed<ProtoMessage> event, int channel) {
        logger.debug(event);
        //System.exit(1);
    }

    /**
     * Send Ping message to Host destination with the given string message
     *
     * @param destination
     *         Host destination
     * @param message
     *         String message
     */
    public void sendDcrMessage(Host destination, String message, Event.Marking marking,
            String idExtensionToken, UserVal sender) {
        logger.debug("Sending DCR Message to {} on channel {} with message {}", destination,
                channelId, message);
        sendMessage(channelId,
                new DCRRequest(message, marking, sender, idExtensionToken),
                destination);
        // logger.debug("Ping message sent");
        // closeConnection(destination, channelId);
    }
    // public void sendPingMessage(Host destination, String message, String marking) {
    //     logger.debug("Sending Ping Message to {} on channel {} with message {}", destination,
    //     channelId, message);
    //     sendMessage(channelId, new PingMessage(++nextPingId, message, marking), destination);
    //     // logger.debug("Ping message sent");
    //     // closeConnection(destination, channelId);
    // }

    /**
     * Handle a newly received Ping Message Reply to the Source Host with a Pong Message
     *
     * @param msg
     *         PingMessage
     * @param from
     *         Source Host
     * @param sourceProto
     *         Source protocol ID
     * @param channelId
     *         Source channel ID (from which channel was the message was received)
     */
    public void uponReceivedDcrMessage(DCRRequest msg, Host from, short sourceProto,
            int channelId) {
        logger.info("Received DCR message from {}; message: {}; " +
                        "idExtensionToken: {};" +
                        " sender: {}",
                // msg.getPingId(),
                from.toString(),
                msg.getMessage(),
                msg.getIdExtensionToken(),
                msg.getSender());
        // FIXME dummy null fields
        AppRequest request =
                new AppRequest(msg.getMessage(), msg.getMarking(), from, msg.getSender(),
                        msg.getIdExtensionToken());
        // App.PROTO_ID hardcoded just for quick-and-dirty proof of concept
        sendRequest(request, DCRApp.PROTO_ID);
    }

    /**
     * Handle a newly received Pong Message Print received pingId and message
     *
     * @param msg
     *         PongMessage
     * @param from
     *         Source Host
     * @param sourceProto
     *         Source protocol ID
     * @param channelId
     *         Source channel ID (from which channel was the message was received)
     */
    public void uponReceivePongMessage(PongMessage msg, Host from, short sourceProto,
            int channelId) {
        logger.debug("Received PongMessage with id: {} and message: {}", msg.getPingId(),
                msg.getMessage());
    }

    /**
     * Handle the case when a message fails to be (confirmed to be) delivered to the destination
     * Print the error
     *
     * @param msg
     *         the message that failed delivery
     * @param host
     *         the destination host
     * @param destProto
     *         the destination protocol ID
     * @param error
     *         the error that caused the failure
     * @param channelId
     *         the channel ID (from which channel was the message was sent)
     */
    private void uponMessageFailed(ProtoMessage msg, Host host, short destProto, Throwable error,
            int channelId) {
        logger.warn("Unable to deliver DCR message: {} to host: {} with error: {}", msg, host,
                error.getMessage());
    }

    /**
     * Handle the case when someone opened a connection to this node Print the event
     *
     * @param event
     *         the event containing the connection information
     * @param channel
     *         the channel ID (from which channel the event was received)
     */
    private void uponInConnectionUp(InConnectionUp event, int channel) {
        logger.debug(event);
    }

    /**
     * Handle the case when someone closed a connection to this node Print the event
     *
     * @param event
     *         the event containing the connection information
     * @param channel
     *         the channel ID (from which channel the event was received)
     */
    private void uponInConnectionDown(InConnectionDown event, int channel) {
        logger.info(event);
    }

    /**
     * Handle the case when a connection to a remote node went down or was closed Print the event
     *
     * @param event
     *         the event containing the connection information
     * @param channel
     *         the channel ID (from which channel the event was received)
     */
    private void uponOutConnectionDown(OutConnectionDown event, int channel) {
        logger.warn(event);
    }

}