/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.sample.siddhi.decision.point.js;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.sample.siddhi.decision.point.EmbeddedSiddhiEngine;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.query.api.definition.StreamDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PublishEventFunctionImpl implements PublishEventFunction {

    private static final Log log = LogFactory.getLog(PublishEventFunctionImpl.class);

    @Override
    public void publishEvent(String siddhiAppName, String streamName, Map<String, Object> payloadData) {

        SiddhiAppRuntime appRunTime = EmbeddedSiddhiEngine.getInstance().getAppRunTime(siddhiAppName);
        if (appRunTime == null) {
            log.error("Siddhi app: " + siddhiAppName + " is not registered/started.");
        } else {

            InputHandler inputHandler = appRunTime.getInputHandler(streamName);
            StreamDefinition streamDefinition = appRunTime.getStreamDefinitionMap().get(streamName);
            if (streamDefinition == null) {
                log.error("StreamDefinition for " + streamName + " does not exist. Cannot publish the event.");
                return;
            }

            List<Object> eventPayload = getEventPayload(payloadData, streamDefinition);
            try {
                inputHandler.send(eventPayload.toArray());
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
    }

    private List<Object> getEventPayload(Map<String, Object> payloadData, StreamDefinition streamDefinition) {

        List<Object> eventPayload = new ArrayList<>();
        Stream.of(streamDefinition.getAttributeNameArray()).forEach(x -> {
            eventPayload.add(payloadData.get(x));
        });
        return eventPayload;
    }
}
