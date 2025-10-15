package websocket;

import jakarta.websocket.EncodeException;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import pt.unl.di.novasys.babel.webservices.application.GenericWebServiceProtocol;
import pt.unl.di.novasys.babel.webservices.utils.GenericWebAPIResponse;
import pt.unl.di.novasys.babel.webservices.websocket.GenericWebSocket;

import java.io.IOException;

@ServerEndpoint(value = DCRWS.PATH, encoders = JacksonEncoder.class)
public class DCRWS extends GenericWebSocket {
    public static final String PATH = "/dcr";

    public DCRWS(GenericWebServiceProtocol babelApp) {
        super(babelApp);
    }



    @Override
    public void onMessage(Session session, String message) {
        // not used in this app.
    }

    @Override
    public void sendMessage(Object value) {
        try {
            session.getBasicRemote().sendObject(value);
//            session.getBasicRemote().flushBatch();
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void triggerResponse(String opUniqueID, GenericWebAPIResponse response) {
        // not used in this app.
    }
}

