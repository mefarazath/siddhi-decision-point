package org.wso2.sample.siddhi.decision.point.js;

import java.util.Map;

@FunctionalInterface
public interface PublishEventFunction {

    void publishEvent(String siddhiAppName, String streamName, Map<String, Object> payloadData);
}
