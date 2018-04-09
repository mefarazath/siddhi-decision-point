package org.wso2.sample.siddhi.decision.point.js;

import org.wso2.carbon.identity.application.authentication.framework.config.model.graph.js.JsAuthenticationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SiddhiJsFunctions {

    private static Map<String, AccountLockStatus> accountLockStatusMap = new ConcurrentHashMap<>();


    public static Boolean isAccountLocked(JsAuthenticationContext context) {
        String user = context.getWrapped().getSubject().getAuthenticatedSubjectIdentifier();
        AccountLockStatus accountLockStatus = accountLockStatusMap.get(user);
        return accountLockStatus != null && accountLockStatus.isLocked();
    }

    public static void lockAccount(String userId, long unlockTime) {
        accountLockStatusMap.put(userId, new AccountLockStatus(true, unlockTime));
    }

    public static void lockAccount(String userId) {
        accountLockStatusMap.put(userId, new AccountLockStatus(true, -1));
    }

    public static void unlockAccount(String userId) {
        accountLockStatusMap.remove(userId);
    }

    public static class AccountLockStatus {
        private boolean isLocked;
        private long unlockTime;

        public AccountLockStatus(boolean status, long unlockTime) {
            this.isLocked = status;
            this.unlockTime = unlockTime;
        }

        public boolean isLocked() {

            return isLocked;
        }

        public long getUnlockTime() {

            return unlockTime;
        }
    }
}
