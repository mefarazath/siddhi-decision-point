package org.wso2.sample.siddhi.decision.point.js;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.adaptive.auth.util.SiddhiConfigManager;
import org.wso2.sample.siddhi.decision.point.io.Constants;
import org.wso2.sample.siddhi.decision.point.io.SiddhiEventPublisher;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.stream.input.InputHandler;

public class PublishEventFunctionImpl implements PublishEventFunction {

    private static final Log log = LogFactory.getLog(SiddhiEventPublisher.class);

    @Override
    public void publishEvent(String siddhiAppName, String streamName, String payload) {

        log.info("..............FUNCTION PUBLISH EVENT IS CALLED........");
        SiddhiAppRuntime appRunTime = SiddhiConfigManager.getInstance().getAppRunTime(siddhiAppName);
        if (appRunTime == null) {
            log.info("Siddhi app: " + Constants.APP_NAME + " is not registered/started.");
        } else {
            InputHandler inputHandler = appRunTime.getInputHandler(streamName);
            String username = payload;
            try {
                inputHandler.send(new Object[]{username, 1f});
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
    }
}
