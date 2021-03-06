package cgl.iotcloud.core;

import cgl.iotcloud.core.config.SCCConfigurationFactory;
import cgl.iotcloud.core.config.SCConfiguration;
import cgl.iotcloud.core.jetty.SGCHTTPServer;
import cgl.iotcloud.streaming.http.server.StreamingHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Starts a SGX server. This is the mail class for starting a SGX Server. This class creates
 * the necessary configurations and start the server.
 */
public class ServerManager {
    private static Logger log = LoggerFactory.getLogger(ServerManager.class);

    public static void main(String[] args) {
        log.info("Starting the SCCloud");
        /// create the configuration
        SCCConfigurationFactory fac = new SCCConfigurationFactory();
        SCConfiguration config = fac.create(Constants.BROKER_CONFIG_FILE);
        final IoTCloud cloud = new IoTCloud(config);
        // initialize the configurations
        cloud.init();

        new Thread(
                new Runnable() {
                    public void run() {
                        // start the HTTP server
                        SGCHTTPServer httpServer = new SGCHTTPServer(cloud);
                        httpServer.start();
                    }
                }).start();

        StreamingHttpServer streamingHttpServer = new StreamingHttpServer();
        streamingHttpServer.start();

    }
}
