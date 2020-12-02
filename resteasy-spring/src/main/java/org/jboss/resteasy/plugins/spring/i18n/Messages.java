package org.jboss.resteasy.plugins.spring.i18n;

import java.lang.reflect.Type;

import jakarta.ws.rs.core.MediaType;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.logging.annotations.Message.Format;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 29, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 13000;

   @Message(id = BASE + 0, value = "You cannot use resteasy.scan, resteasy.scan.resources, or resteasy.scan.providers with the SpringContextLoaderLister as this may cause serious deployment errors in your application")
   String cannotUseScanParameters();

   @Message(id = BASE + 5, value = "Could not convert '%s' to a class.")
   String couldNotConvertBeanToClass(String bean);

   @Message(id = BASE + 10, value = "Could not find message body reader for type: {0} of content type: {1}", format=Format.MESSAGE_FORMAT)
   String couldNotFindMessageBodyReader(Type type, MediaType mediaType);

   @Message(id = BASE + 15, value = "could not find the type for bean named %s")
   String couldNotFindTypeForBean(String bean);

   @Message(id = BASE + 20, value = "Could not retrieve bean %s")
   String couldNotRetrieveBean(String bean);

   @Message(id = BASE + 25, value = "RESTeasy Dispatcher is null, do you have the ResteasyBootstrap listener configured?")
   String dispatcherIsNull();

   @Message(id = BASE + 30, value = "Exception while shutting down Jetty")
   String exceptionShuttingDownJetty();

   @Message(id = BASE + 35, value = "Exception while starting up Jetty")
   String exceptionStartingUpJetty();

   @Message(id = BASE + 40, value = "Interrupted while starting up Jetty")
   String interruptedStartingUpJetty();

   @Message(id = BASE + 42, value = "Not allowed")
   String notAllowed();

   @Message(id = BASE + 43, value = "Not supported")
   String notSupported();

   @Message(id = BASE + 45, value = "%s is not initial request.  Its suspended and retried.  Aborting.")
   String pathNotInitialRequest(String path);

   @Message(id = BASE + 50, value = "RESTeasy Provider Factory is null, do you have the ResteasyBootstrap listener configured?")
   String providerFactoryIsNull();

   @Message(id = BASE + 55, value = "Provider %s is not a singleton.  That's not allowed")
   String providerIsNotSingleton(String provider);

   @Message(id = BASE + 60, value = "RESTeasy Registry is null, do you have the ResteasyBootstrap listener configured?")
   String registryIsNull();

   @Message(id = BASE + 62, value = "The requested media type is not acceptable.")
   String requestedMediaNotAcceptable();

   @Message(id = BASE + 65, value = "ResourceFailure: %s")
   String resourceFailure(String message);

   @Message(id = BASE + 70, value = "Resource Not Found: %s")
   String resourceNotFound(String message);

   @Message(id = BASE + 75, value = "ResteasyHandlerMapping has the default order and throwNotFound settings.  Consider adding explicit ordering to your HandlerMappings, with ResteasyHandlerMapping being lsat, and set throwNotFound = true.")
   String resteasyHandlerMappingHasDefaultOrder();

   @Message(id = BASE + 80, value = "ResteasyRegistration references must be String values or a reference to a bean name")
   String resteasyRegistrationReferences();

   @Message(id = BASE + 85, value = "Shutting down Jetty")
   String shuttingDownJetty();

   @Message(id = BASE + 90, value = "Starting up Jetty")
   String startingUpJetty();

   @Message(id = BASE + 95, value = "RESTeasy Deployment is null, do you have the ResteasyBootstrap listener configured?")
   String deploymentIsNull();
}
