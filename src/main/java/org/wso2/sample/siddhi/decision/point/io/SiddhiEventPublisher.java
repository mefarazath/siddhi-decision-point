package org.wso2.sample.siddhi.decision.point.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.adaptive.auth.util.SiddhiConfigManager;
import org.wso2.carbon.identity.core.bean.context.MessageContext;
import org.wso2.carbon.identity.data.publisher.application.authentication.AbstractAuthenticationDataPublisher;
import org.wso2.carbon.identity.data.publisher.application.authentication.model.AuthenticationData;
import org.wso2.carbon.identity.data.publisher.application.authentication.model.SessionData;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.stream.input.InputHandler;

public class SiddhiEventPublisher extends AbstractAuthenticationDataPublisher {

    private static final Log log = LogFactory.getLog(SiddhiEventPublisher.class);

    @Override
    public void doPublishAuthenticationStepSuccess(AuthenticationData authenticationData) {

    }

    @Override
    public void doPublishAuthenticationStepFailure(AuthenticationData authenticationData) {

    }

    @Override
    public void doPublishAuthenticationSuccess(AuthenticationData authenticationData) {


    }

    @Override
    public void doPublishAuthenticationFailure(AuthenticationData authenticationData) {
        SiddhiAppRuntime appRunTime = SiddhiConfigManager.getInstance().getAppRunTime(Constants.APP_NAME);
        if (appRunTime == null) {
            log.info("Siddhi app: " + Constants.APP_NAME + " is not registered/started.");
        } else {
            InputHandler inputHandler = appRunTime.getInputHandler("login_failure_stream");

            String username = authenticationData.getUsername();
            try {
                inputHandler.send(new Object[] {username, 1f});
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
    }

    @Override
    public void doPublishSessionCreation(SessionData sessionData) {

    }

    @Override
    public void doPublishSessionUpdate(SessionData sessionData) {

    }

    @Override
    public void doPublishSessionTermination(SessionData sessionData) {

    }

    @Override
    public String getName() {
        return "SiddiLoginEventPublisher";
    }

    @Override
    public boolean isEnabled(MessageContext messageContext) {
        return true;
    }
}
