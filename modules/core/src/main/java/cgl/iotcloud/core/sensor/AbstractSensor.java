package cgl.iotcloud.core.sensor;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.Endpoint;
import cgl.iotcloud.core.ManagedLifeCycle;
import cgl.iotcloud.core.broker.Listener;
import cgl.iotcloud.core.broker.ListenerFactory;
import cgl.iotcloud.core.broker.Sender;
import cgl.iotcloud.core.broker.SenderFactory;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the commons functions across sensors.
 */
public abstract class AbstractSensor implements Sensor, ManagedLifeCycle {
    private Logger log;
    /** If of the sensor */
    protected String id;
    /** Sensor type */
    protected String type = Constants.SENSOR_TYPE_BLOCK;
    /** Sensor name */
    protected String name = "";
    /** Sender for data messages */
    private Sender sender = null;
    /** Listener for control messages */
    private Listener listener = null;
    /** Sender information */
    private Endpoint dataEndpoint;
    /** Listener information */
    private Endpoint controlEndpoint;
    /** Update information about the sensor */
    private Endpoint updateEndpoint;
    /** Update information sender */
    private Sender updateSender = null;

    public AbstractSensor(String type, String name) {
        this.type = type;
        this.name = name;
        log = LoggerFactory.getLogger(this.getClass());
    }

    public void destroy() {
        sender.destroy();
        listener.destroy();
    }

    public void init() {
        SenderFactory senderFactory = new SenderFactory();
        sender = senderFactory.create(dataEndpoint);

        if (updateEndpoint != null) {
            updateSender = senderFactory.create(updateEndpoint);
        }

        ListenerFactory listenerFactory = new ListenerFactory();
        listener = listenerFactory.createControlListener(
                controlEndpoint, new ControlMessageReceiver());

        if (log.isDebugEnabled()) {
            log.debug("Initializing the Sender for the Sensor with id: " + id);
        }
        sender.init();

        if (log.isDebugEnabled()) {
            log.debug("Initializing the Listener for the Sensor with id: " + id);
        }
        listener.init();

        if (log.isDebugEnabled()) {
            log.debug("Starting the Sender for the Sensor with id: " + id);
        }
        sender.start();

        if (log.isDebugEnabled()) {
            log.debug("Starting the Listener for the Sensor with id: " + id);
        }
        listener.start();

        if (updateEndpoint != null) {
            if (log.isDebugEnabled()) {
                log.debug("Initializing the Update Sender for the Sensor with id: " + id);
            }
            updateSender.init();
            if (log.isDebugEnabled()) {
                log.debug("Starting the Update Sender for the Sensor with id: " + id);
            }
            updateSender.start();
        }
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void sendMessage(SensorMessage message) {
        sender.send(message);
    }

    public Endpoint getDataEndpoint() {
        return dataEndpoint;
    }

    public Endpoint getControlEndpoint() {
        return controlEndpoint;
    }

    public void setControlEndpoint(Endpoint controlEndpoint) {
        this.controlEndpoint = controlEndpoint;
    }

    public void setDataEndpoint(Endpoint dataEndpoint) {
        this.dataEndpoint = dataEndpoint;
    }

    public Endpoint getUpdateEndpoint() {
        return updateEndpoint;
    }

    public void setUpdateEndpoint(Endpoint endpoint) {
        this.updateEndpoint = endpoint;
    }

    /**
     * User should implement this to handle control messages
     * @param message the message to be handled
     * */
    public abstract void onControlMessage(SensorMessage message);

    private class ControlMessageReceiver implements MessageHandler {
        public void onMessage(SensorMessage message) {
            onControlMessage(message);
        }
    }
}
