package org.wso2.sample.siddhi.decision.point.js;

import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;

@FunctionalInterface
public interface FailAuthenticationFunction {
    void failAuthentication(String msg) throws AuthenticationFailedException;
}
