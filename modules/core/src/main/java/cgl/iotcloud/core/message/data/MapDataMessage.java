package cgl.iotcloud.core.message.data;

import cgl.iotcloud.core.message.DataMessage;

import java.util.HashMap;
import java.util.Map;

public class MapDataMessage implements DataMessage {
    private Map<String, Object> map = new HashMap<String, Object>();

    public String getId() {
        return null;
    }

    public void setId(String id) {

    }
}
