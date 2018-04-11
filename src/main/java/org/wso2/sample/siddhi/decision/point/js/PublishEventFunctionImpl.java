package org.wso2.sample.siddhi.decision.point.js;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.adaptive.auth.util.SiddhiConfigManager;
import org.wso2.sample.siddhi.decision.point.io.Constants;
import org.wso2.sample.siddhi.decision.point.io.SiddhiEventPublisher;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.query.api.definition.StreamDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PublishEventFunctionImpl implements PublishEventFunction {

    private static final Log log = LogFactory.getLog(SiddhiEventPublisher.class);

    @Override
    public void publishEvent(String siddhiAppName, String streamName, Map<String, Object> payloadData) {

        SiddhiAppRuntime appRunTime = SiddhiConfigManager.getInstance().getAppRunTime(siddhiAppName);
        if (appRunTime == null) {
            log.error("Siddhi app: " + siddhiAppName + " is not registered/started.");
        } else {
            InputHandler inputHandler = appRunTime.getInputHandler(streamName);
            StreamDefinition streamDefinition = appRunTime.getStreamDefinitionMap().get(streamName);
            if (streamDefinition == null) {
                log.error("StreamDefinition for " + streamName + " does not exist. Cannot publish the event.");
                return;
            }

            List<Object> eventPayload = new ArrayList<>();
            Stream.of(streamDefinition.getAttributeNameArray()).forEach( x -> {
                eventPayload.add(payloadData.get(x));
            });

            try {
                inputHandler.send(eventPayload.toArray());
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
    }
}
