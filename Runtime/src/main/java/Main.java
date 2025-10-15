import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;
import protocols.application.DCRApp;
import protocols.dcr.DistributedDCRProtocol;
import pt.unl.di.novasys.babel.webservices.rest.GenericREST;
import pt.unl.di.novasys.babel.webservices.utils.ServerConfig;
import pt.unl.di.novasys.babel.webservices.websocket.GenericWebSocket;
import pt.unl.fct.di.novasys.babel.core.Babel;

import java.util.Properties;
import java.util.Set;
import java.util.UUID;


public class Main {

    private static Logger logger;
    private static final String DEFAULT_CONF = "config.properties";

    public static void main(String[] args) throws Exception {
        Main.configLogger();

        Properties props = Babel.loadConfig(args, DEFAULT_CONF);
        Babel babel = Babel.getInstance();

        // protocols
        DistributedDCRProtocol dcrProtocol = new DistributedDCRProtocol();
        DCRApp app = DCRApp.getInstance();

        // protocol-registering
        babel.registerProtocol(dcrProtocol);
        babel.registerProtocol(app);

        // protocol init
        dcrProtocol.init(props);
        app.init(props);

        // web-services setup
        Set<Class<? extends GenericREST>> restServices = ServerConfig.generateRestServices
                (props);
        Set<Class<? extends GenericWebSocket>> wsServices =
                ServerConfig.generateWebsocketServices(props);
        ServletContextHandler serverContext =
                ServerConfig.createServerContextWithStaticFiles(wsServices, restServices,
                        app,
                        props);
        Server server = ServerConfig.createServer(serverContext, props);

        babel.start();
        server.start();

        logger.info("Server started on {}", server.getURI());
        Runtime.getRuntime()
                .addShutdownHook(new Thread(() -> logger.info("Server stopped!")));
    }

    private static void configLogger() {
        System.setProperty("log4j.configurationFile", "log4j2.xml");
        if (System.getProperty("logFileName") == null) {
            System.setProperty("logFileName", generateLogFileName());
        }
        logger = LogManager.getLogger(Main.class);
    }

    private static String generateLogFileName() {
        return UUID.randomUUID() + ".log";
    }
}