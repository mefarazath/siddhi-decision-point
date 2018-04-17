package org.wso2.sample.siddhi.decision.point.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.wso2.sample.siddhi.decision.point.EmbeddedSiddhiEngine;

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

    @Activate
    protected void activate(ComponentContext context) {

        log.info("-------------- SiddhiDecisionPointComponent ACTIVATION STARTED ----------------");

        try {
            String siddhiApp = getSiddhiApp();
            EmbeddedSiddhiEngine.getInstance().deployApp(siddhiApp);
        } catch (Throwable throwable) {
            log.error(throwable);
        }

        log.info("-------------- SiddhiDecisionPointComponent ACTIVATION COMPLETED ----------------");
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

        log.info("SiddhiDecisionPointComponent bundle is deactivated");
    }
}
