package org.jboss.resteasy.cdi.i18n;

import java.lang.invoke.MethodHandles;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Aug 25, 2015
 */
@MessageLogger(projectCode = "RESTEASY")
public interface LogMessages extends BasicLogger {
    LogMessages LOGGER = Logger.getMessageLogger(MethodHandles.lookup(), LogMessages.class,
            LogMessages.class.getPackage().getName());

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 11000, value = "Failed to determine if %s is a CDI bean, falling back to non-CDI resource factory.")
    void failedToDiscoverCdiBean(@Cause Throwable cause, String name);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 11005, value = "The provider %s cannot be proxied and is treated as @Singleton. Declare @Singleton on the provider.")
    void unproxyableProvider(String type);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 11010, value = "The resource %s cannot be proxied and is treated as @Dependent. Declare @Dependent on the resource.")
    void unproxyableResource(String type);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 11015, value = "The CDI bean of the provider %s is @Dependent. It is created once, other injections of it get different instances and its @PreDestroy method is never invoked. Use the @ApplicationScoped or @Singleton scope for the provider's bean.")
    void dependentProvider(String type);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 11020, value = "%s declares type variables and is treated as @Dependent, as CDI requires. Declare @Dependent on it.")
    void genericBean(String type);

    @LogMessage(level = Logger.Level.WARN)
    @Message(id = 11025, value = "The @BeanParam type %s is %s. Declare @Dependent on it, as the fields of a client proxy do not hold the parameter values.")
    void normalScopedBeanParam(String type, String scope);
}
