package org.wso2.sample.siddhi.decision.point.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.sample.siddhi.decision.point.deployer.SiddhiAppDeployer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Component(
        name = "org.wso2.sample.siddhi.decision.point",
        immediate = true
)
public class SiddhiDecisionPointComponent {

    private static final Log log = LogFactory.getLog(SiddhiDecisionPointComponent.class);
    private SiddhiAppDeployer deployer = null;

    @Activate
    protected void activate(ComponentContext context) {

        log.info("-------------- SiddhiDecisionPointComponent ACTIVATION STARTED ----------------");

        try {
            Path siddhiAppRootPath = Paths.get(CarbonUtils.getCarbonRepository(), "siddhiApps");
            if(Files.notExists(siddhiAppRootPath)) {
                Files.createDirectory(siddhiAppRootPath);
            }
            // Copy our app to
            Path toWrite = Paths.get(siddhiAppRootPath.toAbsolutePath().toString(), "accountLockApp.siddhi");
            Files.write(toWrite, getSampleSiddhiApp().getBytes(StandardCharsets.UTF_8));

            deployer = new SiddhiAppDeployer(siddhiAppRootPath);
            deployer.start();
        } catch (Throwable throwable) {
            log.error(throwable);
        }

        log.info("-------------- SiddhiDecisionPointComponent ACTIVATION COMPLETED ----------------");
    }

    private String getSampleSiddhiApp() throws IOException {

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
        if (deployer != null) {
            deployer.stop();
        }
    }
}
