package org.wso2.sample.siddhi.decision.point.js;

import org.wso2.carbon.identity.adaptive.auth.util.SiddhiConfigManager;
import org.wso2.siddhi.core.SiddhiAppRuntime;

public class SiddhiAppDeployImpl implements SiddhiAppDeploy {

    @Override
    public void deploySiddhiApp(String siddhiApp) {
        SiddhiConfigManager siddhiConfigManager = SiddhiConfigManager.getInstance();
        SiddhiAppRuntime siddhiAppRuntime = siddhiConfigManager.createAppRuntime(siddhiApp);
        siddhiAppRuntime.start();
    }
}
