package org.wso2.sample.siddhi.decision.point.js;

import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;

@FunctionalInterface
public interface PublishEventFunction {
    void publishEvent (String siddhiAppName, String streamName, String payload);
}
