package org.jboss.resteasy.resteasy_jaxrs.i18n;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.net.URL;
import java.util.function.Consumer;

import jakarta.ws.rs.core.MediaType;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.Once;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Aug 13, 2015
 */
@MessageLogger(projectCode = "RESTEASY")
public interface LogMessages extends BasicLogger {
    LogMessages LOGGER = LoggingSupport.getMessageLogger(MethodHandles.lookup(), LogMessages.class,
            LogMessages.class.getPackage().getName());

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                  FATAL                                                //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* Empty */

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                  ERROR                                                //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @LogMessage(level = Level.ERROR)
    @Message(id = 2000, value = "Error resuming failed async operation", format = Format.MESSAGE_FORMAT)
    void errorResumingFailedAsynchOperation(@Cause Throwable cause);

    @LogMessage(level = Level.ERROR)
    @Message(id = 2005, value = "Failed executing {0} {1}", format = Format.MESSAGE_FORMAT)
    void failedExecutingError(String method, String path, @Cause Throwable cause);

    @LogMessage(level = Level.ERROR)
    @Message(id = 2010, value = "Failed to execute")
    void failedToExecute(@Cause Throwable cause);

    @LogMessage(level = Level.ERROR)
    @Message(id = 2015, value = "Failed to invoke asynchronously")
    void failedToInvokeAsynchronously(@Cause Throwable ignored);

    @LogMessage(level = Level.ERROR)
    @Message(id = 2020, value = "Unhandled asynchronous exception, sending back 500", format = Format.MESSAGE_FORMAT)
    void unhandledAsynchronousException(@Cause Throwable ignored);

    @LogMessage(level = Level.ERROR)
    @Message(id = 2025, value = "Unknown exception while executing {0} {1}", format = Format.MESSAGE_FORMAT)
    void unknownException(String method, String path, @Cause Throwable cause);

    @LogMessage(level = Level.DEBUG)
    @Message(id = 2030, value = "Failed to write event {0}", format = Format.MESSAGE_FORMAT)
    void failedToWriteSseEvent(String event, @Cause Throwable cause);

    @LogMessage(level = Level.ERROR)
    @Message(id = 2035, value = "Cannot register {0} as HeaderDelegate: it has no type parameter", format = Format.MESSAGE_FORMAT)
    void cannotRegisterheaderDelegate(Class<?> clazz);

    @LogMessage(level = Level.ERROR)
    @Message(id = 2040, value = "GET method returns the patch/merge json object target for request {0} not found", format = Format.MESSAGE_FORMAT)
    void patchTargetMethodNotFound(String requestURI);

    @LogMessage(level = Level.ERROR)
    @Message(id = 2041, value = "Failed to get the patch/merge target for request {0}", format = Format.MESSAGE_FORMAT)
    void errorPatchTarget(String requestURI);

    @LogMessage(level = Level.ERROR)
    @Message(id = 2050, value = "The set instance of %s for property %s is not a valid %s.")
    void invalidPropertyType(Object instance, String name, String type);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                  WARN                                                 //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @LogMessage(level = Level.WARN)
    @Message(id = 2100, value = "Accept extensions not supported.")
    void acceptExtensionsNotSupported();

    @LogMessage(level = Level.WARN)
    @Message(id = 2105, value = "Ambiguity constructors are found in %s. More details please refer to http://jsr311.java.net/nonav/releases/1.1/spec/spec.html")
    void ambiguousConstructorsFound(Class<?> clazz);

    @LogMessage(level = Level.WARN)
    @Message(id = 2110, value = "Attempting to register empty contracts for %s")
    void attemptingToRegisterEmptyContracts(String className);

    @LogMessage(level = Level.WARN)
    @Message(id = 2115, value = "Attempting to register unassignable contract for %s")
    void attemptingToRegisterUnassignableContract(String className);

    @LogMessage(level = Level.WARN)
    @Message(id = 2117, value = "Charset %s unavailable.")
    void charsetUnavailable(String charset);

    @LogMessage(level = Level.WARN)
    @Message(id = 2120, value = "ClassNotFoundException: Unable to load builtin provider {0} from {1}", format = Format.MESSAGE_FORMAT)
    void classNotFoundException(String line, URL url, @Cause Throwable cause);

    @LogMessage(level = Level.WARN)
    @Message(id = 2123, value = "Could not bind to specified download directory %s so will use temp dir.")
    void couldNotBindToDirectory(String directory);

    @LogMessage(level = Level.WARN)
    @Message(id = 2125, value = "Marking file '%s' to be deleted, as it could not be deleted while processing request:")
    void couldNotDeleteFile(String path, @Cause Throwable cause);

    @LogMessage(level = Level.WARN)
    @Message(id = 2130, value = "Failed to parse request.")
    void failedToParseRequest(@Cause Throwable cause);

    @LogMessage(level = Level.WARN)
    @Message(id = 2135, value = "Ignoring unsupported locale: %s")
    void ignoringUnsupportedLocale(String locale);

    @LogMessage(level = Level.WARN)
    @Message(id = 2137, value = "Invalid format for {0}, using default value {1}", format = Format.MESSAGE_FORMAT)
    void invalidFormat(String parameterName, String defaultValue);

    @LogMessage(level = Level.WARN)
    @Message(id = 2138, value = "Invalid regex for {0}: {2}", format = Format.MESSAGE_FORMAT)
    void invalidRegex(String className, String regex);

    @LogMessage(level = Level.WARN)
    @Message(id = 2140, value = "Qualifying annotations found at non-public method: {0}.{1}(); Only public methods may be exposed as resource methods.", format = Format.MESSAGE_FORMAT)
    void JAXRSAnnotationsFoundAtNonPublicMethod(String className, String method);

    @LogMessage(level = Level.WARN)
    @Message(id = 2142, value = "Multiple resource methods match request {0}. Selecting one. Matching methods: {1}", format = Format.MESSAGE_FORMAT)
    void multipleMethodsMatch(String request, String[] methods);

    @LogMessage(level = Level.WARN)
    @Message(id = 2145, value = "NoClassDefFoundError: Unable to load builtin provider {0} from {1}", format = Format.MESSAGE_FORMAT)
    void noClassDefFoundErrorError(String line, URL url, @Cause Throwable cause);

    @LogMessage(level = Level.WARN)
    @Message(id = 2150, value = "%s is no longer supported.  Use a servlet 3.0 container and the ResteasyServletInitializer")
    void noLongerSupported(String param);

    @LogMessage(level = Level.WARN)
    @Message(id = 2155, value = "Provider class {0} is already registered.  2nd registration is being ignored.", format = Format.MESSAGE_FORMAT)
    void providerClassAlreadyRegistered(String className);

    @LogMessage(level = Level.WARN)
    @Message(id = 2160, value = "Provider instance {0} is already registered.  2nd registration is being ignored.", format = Format.MESSAGE_FORMAT)
    void providerInstanceAlreadyRegistered(String className);

    @LogMessage(level = Level.WARN)
    @Message(id = 2165, value = "No valueOf() method available for %s, trying constructor...")
    void noValueOfMethodAvailable(String className);

    @LogMessage(level = Level.WARN)
    @Message(id = 2170, value = "A reader for {0} was not found. This provider is currently configured to handle only {1}", format = Format.MESSAGE_FORMAT)
    void readerNotFound(MediaType mediaType, String[] availableTypes);

    @LogMessage(level = Level.WARN)
    @Message(id = 2172, value = "Singleton {0} object class {1} already deployed. Singleton ignored.", format = Format.MESSAGE_FORMAT)
    void singletonClassAlreadyDeployed(String type, String className);

    @LogMessage(level = Level.WARN)
    @Message(id = 2175, value = "The use of %s is deprecated, please use jakarta.ws.rs.Application as a context-param instead")
    void useOfApplicationClass(String className);

    @LogMessage(level = Level.WARN)
    @Message(id = 2180, value = "Skip illegal field [%s] in value: [%s]")
    void skipIllegalField(String filed, String value);

    @LogMessage(level = Level.WARN)
    @Message(id = 2185, value = "Skip unknown field [%s]")
    void skipUnkownFiled(String filed);

    @LogMessage(level = Level.WARN)
    @Message(id = 2186, value = "Failed to set servlet request into asynchronous mode, server sent events may not work")
    void failedToSetRequestAsync();

    @LogMessage(level = Level.WARN)
    @Message(id = 2190, value = "Annotation, @PreMaching, not valid on ClientRequestFilter implementation, [%s].  Annotation is being ignored.")
    void warningPreMatchingSupport(String clazzname);

    @LogMessage(level = Level.WARN)
    @Message(id = 2195, value = "The following sub-resource methods and sub-resource locators have the same path, [%s].  The paths should be unique.  [%s]")
    void uriAmbiguity(String path, String methodList);

    @LogMessage(level = Level.WARN)
    @Message(id = 2196, value = "Component of type %s can't be dynamically bound to a resource method as a %s provider.")
    void providerCantBeDynamicallyBoundToMethod(Class<?> componentClass, Class<?> providerType);

    @LogMessage(level = Level.WARN)
    @Message(id = 2197, value = "The previous response status was {0} {1}. The status should be changed before the entity is set.", format = Format.MESSAGE_FORMAT)
    void statusNotSet(int statusCode, String reasonPhrase);

    @LogMessage(level = Level.WARN)
    @Once
    @Message(id = 2198, value = "Context parameter '%s' is deprecated and will be removed in a future release. Use '%s' to control which JSON provider is used for PATCH filter processing.")
    void patchFilterLegacyDeprecated(String legacyProperty, String jacksonProperty);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                  INFO                                                 //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @LogMessage(level = Level.INFO)
    @Message(id = 2200, value = "Adding class resource {0} from Application {1}", format = Format.MESSAGE_FORMAT)
    void addingClassResource(String className, Class<?> clazz);

    @LogMessage(level = Level.INFO)
    @Message(id = 2205, value = "Adding provider class {0} from Application {1}", format = Format.MESSAGE_FORMAT)
    void addingProviderClass(String className, Class<?> clazz);

    @LogMessage(level = Level.INFO)
    @Message(id = 2210, value = "Adding provider singleton {0} from Application {1}", format = Format.MESSAGE_FORMAT)
    void addingProviderSingleton(String className, Class<?> application);

    @LogMessage(level = Level.INFO)
    @Message(id = 2215, value = "Adding singleton provider {0} from Application {1}", format = Format.MESSAGE_FORMAT)
    void addingSingletonProvider(String className, Class<?> application);

    @LogMessage(level = Level.INFO)
    @Message(id = 2220, value = "Adding singleton resource {0} from Application {1}", format = Format.MESSAGE_FORMAT)
    void addingSingletonResource(String className, Class<?> application);

    @LogMessage(level = Level.INFO)
    @Message(id = 2225, value = "Deploying {0}: {1}", format = Format.MESSAGE_FORMAT)
    void deployingApplication(String className, Class<?> clazz);

    @LogMessage(level = Level.INFO)
    @Message(id = 2227, value = "MediaType {0} on {1}() lacks charset. Consider setting charset or turning on context parameter resteasy.add.charset", format = Format.MESSAGE_FORMAT)
    void mediaTypeLacksCharset(MediaType mediaType, String method);

    @LogMessage(level = Level.INFO)
    @Message(id = 2230, value = "Unable to close entity stream")
    void unableToCloseEntityStream(@Cause Throwable cause);

    @LogMessage(level = Level.INFO)
    @Message(id = 2235, value = "Unable to decode GZIP compressed Base64 data")
    void unableToDecodeGZIPBase64(@Cause Throwable cause);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                  DEBUG                                                //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @LogMessage(level = Level.DEBUG)
    @Message(id = 2300, value = "Creating context object <{0} : {1}> ", format = Format.MESSAGE_FORMAT)
    void creatingContextObject(String key, String value);

    @LogMessage(level = Level.DEBUG)
    @Message(id = 2305, value = "Failed executing {0} {1}", format = Format.MESSAGE_FORMAT)
    void failedExecutingDebug(String method, String path, @Cause Throwable cause);

    @LogMessage(level = Level.DEBUG)
    @Message(id = 2307, value = "Failed to execute")
    void failedToExecuteDebug(@Cause Throwable cause);

    @LogMessage(level = Level.DEBUG)
    @Message(id = 2310, value = "IN ONE WAY!!!!!")
    void inOneWay();

    @LogMessage(level = Level.DEBUG)
    @Message(id = 2315, value = "PathInfo: %s")
    void pathInfo(String path);

    @LogMessage(level = Level.DEBUG)
    @Message(id = 2320, value = "RUNNING JOB!!!!")
    void runningJob();

    @LogMessage(level = Level.DEBUG)
    @Message(id = 2322, value = "Temporary file %s has been created. Consider deleting after it has been used.")
    void temporaryFileCreated(String fileName);

    @LogMessage(level = Level.DEBUG)
    @Message(id = 2325, value = "Unable to retrieve config: disableDTDs defaults to true")
    void unableToRetrieveConfigDTDs();

    @LogMessage(level = Level.DEBUG)
    @Message(id = 2330, value = "Unable to retrieve config: expandEntityReferences defaults to false")
    void unableToRetrieveConfigExpand();

    @LogMessage(level = Level.DEBUG)
    @Message(id = 2335, value = "Unable to retrieve config: enableSecureProcessingFeature defaults to true")
    void unableToRetrieveConfigSecure();

    @LogMessage(level = Level.DEBUG)
    @Message(id = 2340, value = "Client receive processing failure.")
    void clientReceiveProcessingFailure(@Cause Throwable cause);

    @LogMessage(level = Level.DEBUG)
    @Message("Unable to extract parameter from http request: %s value is '%s' for %s")
    void unableToExtractParameter(@Cause Throwable cause, String paramSignature, String strVal, AccessibleObject target);

    @LogMessage(level = Level.WARN)
    @Message(id = 2350, value = "Failed to reset the context on thread: %s")
    void unableToResetThreadContext(@Cause Throwable cause, String threadName);

    @LogMessage(level = Level.WARN)
    @Message(id = 2360, value = "Failed to look up JNDI resource \"%s\". Using a default ExecutorService.")
    void failedToLookupManagedExecutorService(@Cause Throwable cause, String name);

    @LogMessage(level = Level.WARN)
    @Message(id = 2370, value = "Failed to load: %s")
    void failedToLoad(@Cause Throwable cause, String resourceName);

    @LogMessage(level = Level.ERROR)
    @Message(id = 2375, value = "Error processing unknown request.")
    void defaultExceptionMapper(@Cause Throwable cause);

    @LogMessage(level = Level.ERROR)
    @Message("Error processing request %s")
    void defaultExceptionMapper(@Cause Throwable cause, CharSequence resourceInfo);

    @LogMessage(level = Level.ERROR)
    @Message(id = 2385, value = "Failed to stop the SeBootstrap instance.")
    void failedStopOnShutdown(@Cause Throwable cause);

    @LogMessage(level = Level.ERROR)
    @Message(id = 2386, value = "Failed to execute shutdown call back %s")
    void failedToExecuteCallback(@Cause Throwable cause, Consumer<?> callback);

    @LogMessage(level = Level.WARN)
    @Message(id = 2390, value = "Unable to commit SSE response headers, client connection may have closed")
    void failedToCommitSseHeaders();

    @LogMessage(level = Level.WARN)
    @Message(id = 2400, value = "Failed to set attribute '%s' to value '%s'")
    void transformerPropertyNotSupported(@Cause Throwable cause, String name, String value);

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                  TRACE                                                //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* Empty */
}
