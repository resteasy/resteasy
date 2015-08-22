package org.jboss.resteasy.resteasy_jaxrs.i18n;

import javax.ws.rs.core.MediaType;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.logging.annotations.MessageLogger;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 13, 2015
 */
@MessageLogger(projectCode = "RESTEASY")
public interface LogMessages extends BasicLogger
{
   LogMessages LOGGER = Logger.getMessageLogger(LogMessages.class, LogMessages.class.getPackage().getName());
   int BASE = 0;
   
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  FATAL                                                //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   
   /* Empty */
   
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  ERROR                                                //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////   

   @LogMessage(level = Level.ERROR)
   @Message(id = BASE + 100, value = "Error resuming failed async operation", format=Format.MESSAGE_FORMAT)
   void errorResumingFailedAsynchOperation(@Cause Throwable cause);
   
   @LogMessage(level = Level.ERROR)
   @Message(id = BASE + 105, value = "Failed executing {0} {1}", format=Format.MESSAGE_FORMAT)
   void failedExecutingError(String method, String path, @Cause Throwable cause);
   
   @LogMessage(level = Level.ERROR)
   @Message(id = BASE + 110, value = "Failed to execute")
   void failedToExecute(@Cause Throwable cause);
   
   @LogMessage(level = Level.ERROR)
   @Message(id = BASE + 115, value = "Failed to invoke asynchronously")
   void failedToInvokeAsynchronously(@Cause Throwable ignored);
   
   @LogMessage(level = Level.ERROR)
   @Message(id = BASE + 120, value = "Unhandled asynchronous exception, sending back 500", format=Format.MESSAGE_FORMAT)
   void unhandledAsynchronousException(@Cause Throwable ignored);
   
   @LogMessage(level = Level.ERROR)
   @Message(id = BASE + 125, value = "Unknown exception while executing {0} {1}", format=Format.MESSAGE_FORMAT)
   void unknownException(String method, String path, @Cause Throwable cause);   
   
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  WARN                                                 //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 200, value = "Accept extensions not supported.")
   void acceptExtensionsNotSupported();

   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 205, value = "Ambiguity constructors are found in %s. More details please refer to http://jsr311.java.net/nonav/releases/1.1/spec/spec.html")
   void ambiguousConstructorsFound(Class<?> clazz);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 210, value = "Attempting to register empty contracts for %s")
   void attemptingToRegisterEmptyContracts(String className);   

   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 215, value = "Attempting to register unassignable contract for %s")
   void attemptingToRegisterUnassignableContract(String className);   
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 220, value = "ClassNotFoundException: Unable to load builtin provider: %s")
   void classNotFoundException(String line);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 225, value = "Could not delete file '%s' for request: ")
   void couldNotDeleteFile(String path, @Cause Throwable cause);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 230, value = "Failed to parse request.")
   void failedToParseRequest(@Cause Throwable cause);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 235, value = "Ignoring unsupported locale: %s")
   void ignoringUnsupportedLocale(String locale);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 240, value =  "JAX-RS annotations found at non-public method: {0}.{1}(); Only public methods may be exposed as resource methods.", format=Format.MESSAGE_FORMAT)
   void JAXRSAnnotationsFoundAtNonPublicMethod(String className, String method);  

   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 245, value = "NoClassDefFoundError: Unable to load builtin provider: %s")
   void noClassDefFoundErrorError(String line, @Cause Throwable cause);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 250, value = "%s is no longer supported.  Use a servlet 3.0 container and the ResteasyServletInitializer")
   void noLongerSupported(String param);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 255, value = "Provider class {0} is already registered.  2nd registration is being ignored.", format=Format.MESSAGE_FORMAT)
   void providerClassAlreadyRegistered(String className);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 260, value = "Provider instance {0} is already registered.  2nd registration is being ignored.", format=Format.MESSAGE_FORMAT)
   void providerInstanceAlreadyRegistered(String className);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 265, value = "No valueOf() method available for %s, trying constructor...")
   void noValueOfMethodAvailable(String className);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 270, value = "A reader for {0} was not found. This provider is currently configured to handle only {1}", format=Format.MESSAGE_FORMAT)
   void readerNotFound(MediaType mediaType, String[] availableTypes);
      
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 275, value = "The use of %s is deprecated, please use javax.ws.rs.Application as a context-param instead")
   void useOfApplicationClass(String className);
   
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  INFO                                                 //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////

   @LogMessage(level = Level.INFO)
   @Message(id = BASE + 300, value = "Adding class resource {0} from Application {1}", format=Format.MESSAGE_FORMAT )
   void addingClassResource(String className, Class<?> clazz);

   @LogMessage(level = Level.INFO)
   @Message(id = BASE + 305, value = "Adding provider class {0} from Application {1}", format=Format.MESSAGE_FORMAT )
   void addingProviderClass(String className, Class<?> clazz);
   
   @LogMessage(level = Level.INFO)
   @Message(id = BASE + 310, value = "Adding provider singleton {0} from Application {1}", format=Format.MESSAGE_FORMAT)
   void addingProviderSingleton(String className, Class<?> application);
   
   @LogMessage(level = Level.INFO)
   @Message(id = BASE + 315, value = "Adding singleton provider {0} from Application {1}", format=Format.MESSAGE_FORMAT)
   void addingSingletonProvider(String className, Class<?> application);
   
   @LogMessage(level = Level.INFO)
   @Message(id = BASE + 320, value = "Adding singleton resource {0} from Application {1}", format=Format.MESSAGE_FORMAT)
   void addingSingletonResource(String className, Class<?> application);
   
   @LogMessage(level = Level.INFO)
   @Message(id = BASE + 325, value = "Deploying {0}: {1}", format=Format.MESSAGE_FORMAT)
   void deployingApplication(String className, Class<?> clazz);

   @LogMessage(level = Level.INFO)
   @Message(id = BASE + 330, value = "unable to close entity stream")
   void unableToCloseEntityStream(@Cause Throwable cause);
   
   
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  DEBUG                                                //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 400, value = "Creating context object <{0} : {1}> ", format=Format.MESSAGE_FORMAT)
   void creatingContextObject(String key, String value);
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 405, value = "Failed executing {0} {1}", format=Format.MESSAGE_FORMAT)
   void failedExecutingDebug(String method, String path, @Cause Throwable cause);
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 410, value = "IN ONE WAY!!!!!")
   void inOneWay();
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 415, value = "PathInfo: %s")
   void pathInfo(String path); 
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 420, value = "RUNNING JOB!!!!")
   void runningJob();
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 425, value = "Unable to retrieve config: disableDTDs defaults to true")
   void unableToRetrieveConfigDTDs();
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 430, value = "Unable to retrieve config: expandEntityReferences defaults to false")
   void unableToRetrieveConfigExpand();
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 435, value = "Unable to retrieve config: enableSecureProcessingFeature defaults to true")
   void unableToRetrieveConfigSecure();
   

   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  TRACE                                                //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   
   /* Empty */
}
