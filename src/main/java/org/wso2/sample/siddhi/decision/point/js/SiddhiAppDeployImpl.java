package org.wso2.sample.siddhi.decision.point.js;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.sample.siddhi.decision.point.EmbeddedSiddhiEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class SiddhiAppDeployImpl implements SiddhiAppDeploy {

    private static final Log log = LogFactory.getLog(SiddhiAppDeployImpl.class);

    @Override
    public boolean deploySiddhiApp(String siddhiApp) {

        return EmbeddedSiddhiEngine.getInstance().deployApp(siddhiApp);
    }

    private String readInputStream(InputStream input) throws IOException {

        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }
}
