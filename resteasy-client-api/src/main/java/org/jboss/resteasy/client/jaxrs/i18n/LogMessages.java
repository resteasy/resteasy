package org.jboss.resteasy.client.jaxrs.i18n;

import java.lang.invoke.MethodHandles;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Aug 26, 2015
 */
@MessageLogger(projectCode = "RESTEASY")
public interface LogMessages extends BasicLogger {
    LogMessages LOGGER = Logger.getMessageLogger(MethodHandles.lookup(), LogMessages.class,
            LogMessages.class.getPackage().getName());

    @LogMessage(level = Level.DEBUG)
    @Message(id = Messages.BASE + 171, value = "Ignoring exception thrown within InvocationCallback")
    void exceptionIgnored(@Cause Throwable ex);

    @LogMessage(level = Level.DEBUG)
    @Message(id = Messages.BASE + 172, value = "Client send processing failure.")
    void clientSendProcessingFailure(@Cause Throwable cause);

    @LogMessage(level = Level.WARN)
    @Message(id = Messages.BASE + 187, value = "Closing a %s instance for you. Please close clients yourself.")
    void closingForYou(Class<?> clazz);

    @LogMessage(level = Level.WARN)
    @Message(id = Messages.BASE + 300, value = "Could not determine the protocol from %s. Defaulting to %s.")
    void invalidProtocol(Object found, String defaultValue);

}
