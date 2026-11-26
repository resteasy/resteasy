package org.jboss.resteasy.client.jaxrs.i18n;

import java.lang.invoke.MethodHandles;

import javax.net.ssl.SSLPeerUnverifiedException;

import jakarta.ws.rs.ProcessingException;

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
    @Message(id = 4671, value = "Ignoring exception thrown within InvocationCallback")
    void exceptionIgnored(@Cause Throwable ex);

    @LogMessage(level = Level.DEBUG)
    @Message(id = 4672, value = "Client send processing failure.")
    void clientSendProcessingFailure(@Cause Throwable cause);

    @LogMessage(level = Level.WARN)
    @Message(id = 4687, value = "Closing a %s instance for you. Please close clients yourself.")
    void closingForYou(Class<?> clazz);

    @LogMessage(level = Level.WARN)
    @Message(id = 4800, value = "Could not determine the protocol from %s. Defaulting to %s.")
    void invalidProtocol(Object found, String defaultValue);

    @LogMessage(level = Level.WARN)
    @Message(id = 4900, value = "Could not determine the HttpClient.Version from %s.")
    void invalidVersion(Object found);

    @Message(id = 4910, value = "Client is closed.")
    ProcessingException clientIsClosed();

    @Message(id = 4920, value = "Unable to invoke request: %s")
    ProcessingException unableToInvokeRequest(@Cause Throwable cause, String msg);

    @Message(id = 4930, value = "Request method %s cannot have a body.")
    ProcessingException bodyNotAllowed(String method);

    @Message(id = 4940, value = "Host name %s was rejected by the HostnameVerifier.")
    SSLPeerUnverifiedException hostnameVerificationFailed(String host);

    @Message(id = 4950, value = "Operation %s is not supported by the host name verifying SSLContext.")
    UnsupportedOperationException unsupportedSslContextOperation(String operation);

    @Message(id = 4960, value = "The default SSLContext is not available.")
    IllegalStateException defaultSslContextUnavailable(@Cause Throwable cause);

}
