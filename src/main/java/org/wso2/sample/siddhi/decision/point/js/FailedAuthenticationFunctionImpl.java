package org.wso2.sample.siddhi.decision.point.js;

import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;

public class FailedAuthenticationFunctionImpl implements FailAuthenticationFunction {

    @Override
    public void failAuthentication(String msg) throws AuthenticationFailedException {
        throw new AuthenticationFailedException(msg);
    }
}
