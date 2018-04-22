package org.jboss.resteasy.resteasy_jaxrs.i18n;

import java.net.URL;

import javax.ws.rs.core.MediaType;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
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
   int BASE = 2000;
   
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  FATAL                                                //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   
   /* Empty */
   
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  ERROR                                                //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////   

   @LogMessage(level = Level.ERROR)
   @Message(id = BASE + 0, value = "Error resuming failed async operation", format=Format.MESSAGE_FORMAT)
   void errorResumingFailedAsynchOperation(@Cause Throwable cause);
   
   @LogMessage(level = Level.ERROR)
   @Message(id = BASE + 5, value = "Failed executing {0} {1}", format=Format.MESSAGE_FORMAT)
   void failedExecutingError(String method, String path, @Cause Throwable cause);
   
   @LogMessage(level = Level.ERROR)
   @Message(id = BASE + 10, value = "Failed to execute")
   void failedToExecute(@Cause Throwable cause);
   
   @LogMessage(level = Level.ERROR)
   @Message(id = BASE + 15, value = "Failed to invoke asynchronously")
   void failedToInvokeAsynchronously(@Cause Throwable ignored);
   
   @LogMessage(level = Level.ERROR)
   @Message(id = BASE + 20, value = "Unhandled asynchronous exception, sending back 500", format=Format.MESSAGE_FORMAT)
   void unhandledAsynchronousException(@Cause Throwable ignored);
   
   @LogMessage(level = Level.ERROR)
   @Message(id = BASE + 25, value = "Unknown exception while executing {0} {1}", format=Format.MESSAGE_FORMAT)
   void unknownException(String method, String path, @Cause Throwable cause);
   
   @LogMessage(level = Level.ERROR)
   @Message(id = BASE + 30, value = "Failed to write event {0}", format=Format.MESSAGE_FORMAT)
   void failedToWriteSseEvent(String event, @Cause Throwable cause);   
   
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  WARN                                                 //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 100, value = "Accept extensions not supported.")
   void acceptExtensionsNotSupported();

   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 105, value = "Ambiguity constructors are found in %s. More details please refer to http://jsr311.java.net/nonav/releases/1.1/spec/spec.html")
   void ambiguousConstructorsFound(Class<?> clazz);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 110, value = "Attempting to register empty contracts for %s")
   void attemptingToRegisterEmptyContracts(String className);   

   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 115, value = "Attempting to register unassignable contract for %s")
   void attemptingToRegisterUnassignableContract(String className);   

   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 117, value = "Charset %s unavailable.")
   void charsetUnavailable(String charset); 
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 120, value = "ClassNotFoundException: Unable to load builtin provider {0} from {1}", format=Format.MESSAGE_FORMAT)
   void classNotFoundException(String line, URL url, @Cause Throwable cause);

   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 123, value = "Could not bind to specified download directory %s so will use temp dir.")
   void couldNotBindToDirectory(String directory);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 125, value = "Marking file '%s' to be deleted, as it could not be deleted while processing request:")
   void couldNotDeleteFile(String path, @Cause Throwable cause);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 130, value = "Failed to parse request.")
   void failedToParseRequest(@Cause Throwable cause);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 135, value = "Ignoring unsupported locale: %s")
   void ignoringUnsupportedLocale(String locale);

   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 137, value =  "Invalid format for {0}, using default value {1}", format=Format.MESSAGE_FORMAT)
   void invalidFormat(String parameterName, String defaultValue); 

   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 140, value =  "JAX-RS annotations found at non-public method: {0}.{1}(); Only public methods may be exposed as resource methods.", format=Format.MESSAGE_FORMAT)
   void JAXRSAnnotationsFoundAtNonPublicMethod(String className, String method);  
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 142, value = "Multiple resource methods match request {0}. Selecting one. Matching methods: {1}", format=Format.MESSAGE_FORMAT)
   void multipleMethodsMatch(String request, String[] methods);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 145, value = "NoClassDefFoundError: Unable to load builtin provider {0} from {1}", format=Format.MESSAGE_FORMAT)
   void noClassDefFoundErrorError(String line, URL url, @Cause Throwable cause);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 150, value = "%s is no longer supported.  Use a servlet 3.0 container and the ResteasyServletInitializer")
   void noLongerSupported(String param);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 155, value = "Provider class {0} is already registered.  2nd registration is being ignored.", format=Format.MESSAGE_FORMAT)
   void providerClassAlreadyRegistered(String className);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 160, value = "Provider instance {0} is already registered.  2nd registration is being ignored.", format=Format.MESSAGE_FORMAT)
   void providerInstanceAlreadyRegistered(String className);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 165, value = "No valueOf() method available for %s, trying constructor...")
   void noValueOfMethodAvailable(String className);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 170, value = "A reader for {0} was not found. This provider is currently configured to handle only {1}", format=Format.MESSAGE_FORMAT)
   void readerNotFound(MediaType mediaType, String[] availableTypes);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 172, value = "Singleton {0} object class {1} already deployed. Singleton ignored.", format=Format.MESSAGE_FORMAT)
   void singletonClassAlreadyDeployed(String type, String className);
      
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 175, value = "The use of %s is deprecated, please use javax.ws.rs.Application as a context-param instead")
   void useOfApplicationClass(String className);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 180, value = "Skip illegal field [%s] in value: [%s]")
   void skipIllegalField(String filed, String value);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 185, value = "Skip unknown field [%s]")
   void skipUnkownFiled(String filed);
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 186, value = "Failed to set servlet request into asynchronous mode, server sent events may not work")
   void failedToSetRequestAsync();
   
   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 190, value = "Annotation, @PreMaching, not valid on ClientRequestFilter implementation, [%s].  Annotation is being ignored.")
   void warningPreMatchingSupport(String clazzname);

   @LogMessage(level = Level.WARN)
   @Message(id = BASE + 195, value = "The following sub-resource methods and sub-resource locators have the same path, [%s].  The paths should be unique.  [%s]")
   void uriAmbiguity(String path, String methodList);

   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  INFO                                                 //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////

   @LogMessage(level = Level.INFO)
   @Message(id = BASE + 200, value = "Adding class resource {0} from Application {1}", format=Format.MESSAGE_FORMAT )
   void addingClassResource(String className, Class<?> clazz);

   @LogMessage(level = Level.INFO)
   @Message(id = BASE + 205, value = "Adding provider class {0} from Application {1}", format=Format.MESSAGE_FORMAT )
   void addingProviderClass(String className, Class<?> clazz);
   
   @LogMessage(level = Level.INFO)
   @Message(id = BASE + 210, value = "Adding provider singleton {0} from Application {1}", format=Format.MESSAGE_FORMAT)
   void addingProviderSingleton(String className, Class<?> application);
   
   @LogMessage(level = Level.INFO)
   @Message(id = BASE + 215, value = "Adding singleton provider {0} from Application {1}", format=Format.MESSAGE_FORMAT)
   void addingSingletonProvider(String className, Class<?> application);
   
   @LogMessage(level = Level.INFO)
   @Message(id = BASE + 220, value = "Adding singleton resource {0} from Application {1}", format=Format.MESSAGE_FORMAT)
   void addingSingletonResource(String className, Class<?> application);
   
   @LogMessage(level = Level.INFO)
   @Message(id = BASE + 225, value = "Deploying {0}: {1}", format=Format.MESSAGE_FORMAT)
   void deployingApplication(String className, Class<?> clazz);

   @LogMessage(level = Level.INFO)
   @Message(id = BASE + 227, value = "MediaType {0} on {1}() lacks charset. Consider setting charset or turning on context parameter " + ResteasyContextParameters.RESTEASY_ADD_CHARSET, format=Format.MESSAGE_FORMAT)
   void mediaTypeLacksCharset(MediaType mediaType, String method);
   
   @LogMessage(level = Level.INFO)
   @Message(id = BASE + 230, value = "Unable to close entity stream")
   void unableToCloseEntityStream(@Cause Throwable cause);

   @LogMessage(level = Level.INFO)
   @Message(id = BASE + 235, value = "Unable to decode GZIP compressed Base64 data")
   void unableToDecodeGZIPBase64(@Cause Throwable cause);
   
   
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  DEBUG                                                //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 300, value = "Creating context object <{0} : {1}> ", format=Format.MESSAGE_FORMAT)
   void creatingContextObject(String key, String value);
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 305, value = "Failed executing {0} {1}", format=Format.MESSAGE_FORMAT)
   void failedExecutingDebug(String method, String path, @Cause Throwable cause);
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 307, value = "Failed to execute")
   void failedToExecuteDebug(@Cause Throwable cause);
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 310, value = "IN ONE WAY!!!!!")
   void inOneWay();
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 315, value = "PathInfo: %s")
   void pathInfo(String path); 
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 320, value = "RUNNING JOB!!!!")
   void runningJob();
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 322, value = "Temporary file %s has been created. Consider deleting after it has been used.")
   void temporaryFileCreated(String fileName);
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 325, value = "Unable to retrieve config: disableDTDs defaults to true")
   void unableToRetrieveConfigDTDs();
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 330, value = "Unable to retrieve config: expandEntityReferences defaults to false")
   void unableToRetrieveConfigExpand();
   
   @LogMessage(level = Level.DEBUG)
   @Message(id = BASE + 335, value = "Unable to retrieve config: enableSecureProcessingFeature defaults to true")
   void unableToRetrieveConfigSecure();
   

   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   //                                                  TRACE                                                //
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////
   
   /* Empty */
}
