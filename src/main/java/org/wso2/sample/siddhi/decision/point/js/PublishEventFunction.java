package org.wso2.sample.siddhi.decision.point.js;

@FunctionalInterface
public interface PublishEventFunction {

    void publishEvent(String siddhiAppName, String streamName, String payload);
}
