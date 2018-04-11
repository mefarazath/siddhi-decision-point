function(context) {
    executeStep({
        id: '1',
        on: {
            success: function (context) {
                // DUMMY TEST
                var user = context.subject;
                var accountLocked = isAccountLocked(user);
                if (accountLocked) {
                    Log.info("---------------------- ACCOUNT LOCKED FOR USER: " + user.username + ' ---------------------------');
                    sendError({});
                }
            },
            fail : function (context) {
                // DUMMY TEST
                var appName = 'LockAccountOnFailureApp';
                var streamName = 'login_failure_stream';

                var user = context.lastAttemptedSubject;
                var username = context.lastAttemptedSubject.username;
                var sp = context.serviceProviderName;

                var payload = {'user' : username, 'service_provider' : sp };
                publishEvent(appName, streamName, payload);

                var accountLocked = isAccountLocked(user);
                Log.info("---------------------- isAccountLocked: " + accountLocked + ' -------------------------');
            }
        }
    });
}
