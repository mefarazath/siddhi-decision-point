package org.wso2.sample.siddhi.decision.point.internal;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.wso2.carbon.identity.adaptive.auth.util.SiddhiConfigManager;
import org.wso2.carbon.identity.application.authentication.framework.AuthenticationDataPublisher;
import org.wso2.sample.siddhi.decision.point.io.Constants;
import org.wso2.sample.siddhi.decision.point.io.SiddhiEventPublisher;
import org.wso2.sample.siddhi.decision.point.js.SiddhiJsFunctions;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.core.util.EventPrinter;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component(
        name = "org.wso2.sample.siddhi.decision.point",
        immediate = true
)
public class SiddhiDecisionPointComponent {

    private static final Log log = LogFactory.getLog(SiddhiDecisionPointComponent.class);
    public static final int UNLOCK_TIME_IN_MINUTES = 5;

    @Activate
    protected void activate(ComponentContext context) {

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
                        String sp = (String) data[0];
                        SiddhiJsFunctions.lockAccount(username, System.currentTimeMillis() + getUnlockTimeInMillis());
                    }
                }
            });

            siddhiAppRuntime.addCallback("account_lock_query", new QueryCallback() {
                @Override
                public void receive(long l, Event[] events, Event[] expiredEvent) {
                    if (expiredEvent != null && expiredEvent.length >= 1) {
                        // Unlock event
                        for (Event event : expiredEvent) {
                            Object[] data = event.getData();
                            String username = (String) data[0];
                            SiddhiJsFunctions.unlockAccount(username);
                        }
                    }

                    if (events != null && events.length >= 1) {
                        // Unlock event
                        for (Event event : events) {
                            Object[] data = event.getData();
                            String username = (String) data[0];
                            SiddhiJsFunctions.lockAccount(username);
                        }
                    }
                }
            });


            context.getBundleContext().registerService(AuthenticationDataPublisher.class, new SiddhiEventPublisher(), null);

        } catch (Throwable throwable) {
            log.error(throwable);
        }

        log.info("-------------- SiddhiDecisionPointComponent ACTIVATION COMPLETED ----------------");
    }

    private int getUnlockTimeInMillis() {
        return UNLOCK_TIME_IN_MINUTES * 60 * 60 * 100;
    }

    private String getSiddhiApp() {


        return "@App:name('LockAccountOnFailureApp') \n" +
                "     \n" +
                "@sink(type='log')      \n" +
                "define stream login_failure_stream (user string, service_provider string);\n" +
                "\n" +
                "@sink(type='log')      \n" +
                "define stream login_failure_count_stream (user string, counter long);\n" +
                "\n" +
                "@sink(type='log')      \n" +
                "define stream account_lock_stream (user string);\n" +
                "\n" +
                "from login_failure_stream#window.time(60 sec)\n" +
                "select user, minimum(3l, count()) as counter group by user\n" +
                "insert into login_failure_count_stream;\n" +
                "\n" +
                "from login_failure_count_stream[(counter >= 3)]\n" +
                "select user\n" +
                "insert into account_lock_stream;\n" +
                "\n" +
                "@info(name='account_lock_query') \n" +
                "from account_lock_stream#window.time(300 sec)\n" +
                "select user\n" +
                "insert into locked_users;";
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        log.info("SiddhiDecisionPointComponent bundle is deactivated");
    }
}
