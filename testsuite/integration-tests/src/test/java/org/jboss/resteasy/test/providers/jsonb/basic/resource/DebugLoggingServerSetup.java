package org.jboss.resteasy.test.providers.jsonb.basic.resource;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.resteasy.setup.LoggingSetupTask;

/**
 * @author Marek Kopecky mkopecky@redhat.com
 * @deprecated use {@link org.jboss.resteasy.setup.LoggingSetupTask}
 */
@Deprecated(forRemoval = true)
public class DebugLoggingServerSetup extends LoggingSetupTask implements ServerSetupTask {
}
