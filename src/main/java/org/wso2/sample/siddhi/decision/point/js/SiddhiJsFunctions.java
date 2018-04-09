package org.wso2.sample.siddhi.decision.point.js;

import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SiddhiJsFunctions {

    private static Map<String, Boolean> accountLockStatusMap = new ConcurrentHashMap<>();


    public static Boolean isAccountLocked(JsAuthenticationContext context) {
        String user = context.getWrapped().getSubject().getAuthenticatedSubjectIdentifier();
        return accountLockStatusMap.getOrDefault(user, Boolean.FALSE);
    }

    public static void lockAccount(String userId) {
        accountLockStatusMap.put(userId, true);
    }

    public static void unlockAccount(String userId) {
        accountLockStatusMap.remove(userId);
    }
}
