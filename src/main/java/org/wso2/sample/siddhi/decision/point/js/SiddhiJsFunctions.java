package org.wso2.sample.siddhi.decision.point.js;

import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticatedUser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SiddhiJsFunctions {

    private static Map<String, Boolean> accountLockStatusMap = new ConcurrentHashMap<>();

    public static Boolean isAccountLocked(JsAuthenticatedUser jsAuthenticatedUser) {
        String user = jsAuthenticatedUser.getWrapped().getUserName();
        return accountLockStatusMap.getOrDefault(user, Boolean.FALSE);
    }

    public static void lockAccount(String userId) {
        accountLockStatusMap.put(userId, true);
    }

    public static void unlockAccount(String userId) {
        accountLockStatusMap.remove(userId);
    }
}
