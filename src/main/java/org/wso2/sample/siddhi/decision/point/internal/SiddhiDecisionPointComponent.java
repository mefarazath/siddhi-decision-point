package org.wso2.sample.siddhi.decision.point.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.wso2.carbon.identity.adaptive.auth.util.SiddhiConfigManager;
import org.wso2.carbon.identity.application.authentication.framework.AuthenticationDataPublisher;
import org.wso2.sample.siddhi.decision.point.io.SiddhiEventPublisher;
import org.wso2.sample.siddhi.decision.point.js.SiddhiJsFunctions;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.output.StreamCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component(
        name = "org.wso2.sample.siddhi.decision.point",
        immediate = true
)
public class SiddhiDecisionPointComponent {

    private static final Log log = LogFactory.getLog(SiddhiDecisionPointComponent.class);
    private static final int UNLOCK_TIME_IN_MINUTES = 5;

    private BundleContext bundleContext;

    @Activate
    protected void activate(ComponentContext context) {

        bundleContext = context.getBundleContext();
        log.info("-------------- SiddhiDecisionPointComponent ACTIVATION STARTED ----------------");

        try {
            String siddhiApp = getSiddhiApp();

            SiddhiConfigManager siddhiConfigManager = SiddhiConfigManager.getInstance();
            SiddhiAppRuntime siddhiAppRuntime = siddhiConfigManager.createAppRuntime(siddhiApp);
            siddhiAppRuntime.start();

            // register callback
            siddhiAppRuntime.addCallback("account_lock_stream", new StreamCallback() {
                @Override
                public void receive(Event[] events) {

                    for (Event event : events) {
                        Object[] data = event.getData();
                        String username = (String) data[0];
                        SiddhiJsFunctions.lockAccount(username);
                    }
                }
            });

            // register callback
            siddhiAppRuntime.addCallback("account_unlock_stream", new StreamCallback() {
                @Override
                public void receive(Event[] events) {

                    for (Event event : events) {
                        Object[] data = event.getData();
                        String username = (String) data[0];
                        SiddhiJsFunctions.unlockAccount(username);
                    }
                }
            });

            bundleContext = context.getBundleContext();
            context.getBundleContext().registerService(AuthenticationDataPublisher.class, new SiddhiEventPublisher(), null);

        } catch (Throwable throwable) {
            log.error(throwable);
        }

        log.info("-------------- SiddhiDecisionPointComponent ACTIVATION COMPLETED ----------------");
    }

    private int getUnlockTimeInMillis() {

        return UNLOCK_TIME_IN_MINUTES * 60 * 60 * 100;
    }

    private String getSiddhiApp() throws IOException {

        InputStream stream = getClass().getClassLoader().getResourceAsStream("accountLockOnFailureApp.siddhi");
        return readInputStream(stream);
    }

    private String readInputStream(InputStream input) throws IOException {

        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {

        bundleContext = null;
        log.info("SiddhiDecisionPointComponent bundle is deactivated");
    }
}
