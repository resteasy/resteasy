package org.jboss.resteasy.i18n;

import java.net.URL;

import javax.ws.rs.core.MediaType;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.Message.Format;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Sep 20, 2014
 */
@MessageLogger(projectCode = "RESTEASY")
public interface LogMessages extends BasicLogger
{
   LogMessages LOGGER = Logger.getMessageLogger(LogMessages.class, LogMessages.class.getPackage().getName());
   
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  FATAL                                                //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   
   /* Empty */
   
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  ERROR                                                //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////   

   @LogMessage(level = Level.ERROR)
   @Message(id = 100, value = "Failed executing {0} {1}", format=Format.MESSAGE_FORMAT)
   void failedExecutingError(String method, String path, @Cause Throwable cause);
   
   @LogMessage(level = Level.ERROR)
   @Message(id = 105, value = "Failed to execute")
   void failedToExecute(@Cause Throwable cause);
   
   @LogMessage(level = Level.ERROR)
   @Message(id = 110, value = "Failed to invoke asynchronously")
   void failedToInvokeAsynchronously(@Cause Throwable ignored);

   @LogMessage(level = Level.ERROR)
   @Message(id = 115, value = "NoClassDefFoundError: Unable to load builtin provider: %s")
   void noClassDefFoundErrorError(String line, @Cause Throwable cause);
   
   @LogMessage(level = Level.ERROR)
   @Message(id = 120, value = "Unknown exception while executing {0} {1}", format=Format.MESSAGE_FORMAT)
   void unknownException(String method, String path, @Cause Throwable cause);
   
   
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  WARN                                                 //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   
   @LogMessage(level = Level.WARN)
   @Message(id = 200, value = "Accept extensions not supported.")
   void acceptExtensionsNotSupported();
   
   @LogMessage(level = Level.WARN)
   @Message(id = 205, value = "Could not delete file '%s' for request: ")
   void couldNotDeleteFile(String path, @Cause Throwable cause);
   
   @LogMessage(level = Level.WARN)
   @Message(id = 210, value = "Failed to parse request.")
   void failedToParseRequest(@Cause Throwable cause);
   
   @LogMessage(level = Level.WARN)
   @Message(id = 215, value =  "Field {0} of subresource {1} will not be injected according to spec", format=Format.MESSAGE_FORMAT)
   void fieldOfSubesourceWillNotBeInjected(String field, String subresource);
   
   @LogMessage(level = Level.WARN)
   @Message(id = 220, value = "Ignoring unsupported locale: %s")
   void ignoringUnsupportedLocale(String locale);
   
   @LogMessage(level = Level.WARN)
   @Message(id = 225, value =  "JAX-RS annotations found at non-public method: {0}.{1}(); Only public methods may be exposed as resource methods.", format=Format.MESSAGE_FORMAT)
   void JAXRSAnnotationsFoundAtNonPublicMethod(String className, String method);  
   
   @LogMessage(level = Level.WARN)
   @Message(id = 230, value = "ClassNotFoundException: Unable to load builtin provider: %s")
   void classNotFoundException(String line);
   
   @LogMessage(level = Level.WARN)
   @Message(id = 235, value = "A reader for {0} was not found. This provider is currently configured to handle only {1}", format=Format.MESSAGE_FORMAT)
   void readerNotFound(MediaType mediaType, String[] availableTypes);
   
   @LogMessage(level = Level.WARN)
   @Message(id = 240, value = "The use of %s is deprecated, please use javax.ws.rs.Application as a context-param instead")
   void useOfApplicationClass(String className);
   
   
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  INFO                                                 //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   
   @LogMessage(level = Level.INFO)
   @Message(id = 300, value = "Adding scanned @Provider: %s")
   void addingScannedProvider(String className);
   
   @LogMessage(level = Level.INFO)
   @Message(id = 305, value = "Adding scanned resource: %s")
   void addingScannedResource(String className);
   
   @LogMessage(level = Level.INFO)
   @Message(id = 310, value = "Adding singleton provider {0} from Application {1}", format=Format.MESSAGE_FORMAT)
   void addingSingletonProvider(String className, String application);
   
   @LogMessage(level = Level.INFO)
   @Message(id = 315, value = "Adding singleton resource {0} from Application {1}", format=Format.MESSAGE_FORMAT)
   void addingSingletonResource(String className, String application);
   
   @LogMessage(level = Level.INFO)
   @Message(id = 320, value = "Deploying {0}: {1}", format=Format.MESSAGE_FORMAT)
   void deployingApplication(String className, Class<?> clazz);

   @LogMessage(level = Level.INFO)
   @Message(id = 325, value = "unable to close entity stream")
   void unableToCloseEntityStream(@Cause Throwable cause);
   
   
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  DEBUG                                                //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = 400, value = "Creating context object <{0} : {1}> ", format=Format.MESSAGE_FORMAT)
   void creatingContextObject(String key, String value);
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = 405, value = "Failed executing {0} {1}", format=Format.MESSAGE_FORMAT)
   void failedExecutingDebug(String method, String path, @Cause Throwable cause);
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = 410, value = "IN ONE WAY!!!!!")
   void inOneWay();
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = 415, value = "PathInfo: %s")
   void pathInfo(String path); 
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = 420, value = "RUNNING JOB!!!!")
   void runningJob();
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = 425, value = "Scanning..")
   void scanning();
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = 430, value = "Scanning archive: %s")
   void scanningArchive(URL url);
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = 435, value = "Unable to retrieve config: disableDTDs defaults to true")
   void unableToRetrieveConfigDTDs();
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = 440, value = "Unable to retrieve config: expandEntityReferences defaults to false")
   void unableToRetrieveConfigExpand();
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = 445, value = "Unable to retrieve config: enableSecureProcessingFeature defaults to true")
   void unableToRetrieveConfigSecure();
   

   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  TRACE                                                //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   
   /* Empty */
}
