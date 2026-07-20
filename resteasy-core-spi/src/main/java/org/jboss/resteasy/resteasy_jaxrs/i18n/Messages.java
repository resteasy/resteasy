/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.resteasy_jaxrs.i18n;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import jakarta.validation.ElementKind;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.resteasy.annotations.Stream;
import org.jboss.resteasy.api.validation.ConstraintType;
import org.jboss.resteasy.spi.config.Threshold;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Aug 13, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages {
    Messages MESSAGES = org.jboss.logging.Messages.getBundle(MethodHandles.lookup(), Messages.class);

    int BASE_ASYNC = 9500;

    int BASE_VALIDATOR_11 = 8500;

    @Message(id = BASE_VALIDATOR_11 + 0, value = "ResteasyViolationException has invalid format: %s")
    String exceptionHasInvalidFormat(String line);

    @Message(id = BASE_VALIDATOR_11 + 25, value = "Unable to parse ResteasyViolationException")
    String unableToParseException();

    @Message(id = BASE_VALIDATOR_11 + 30, value = "unexpected path node type: %s")
    String unexpectedPathNode(ElementKind kind);

    @Message(id = BASE_VALIDATOR_11 + 35, value = "unexpected path node type in method violation: %s")
    String unexpectedPathNodeViolation(ElementKind kind);

    @Message(id = BASE_VALIDATOR_11 + 40, value = "unexpected violation type: %s")
    String unexpectedViolationType(ConstraintType.Type type);

    @Message(id = BASE_VALIDATOR_11 + 45, value = "unknown object passed as constraint violation: %s")
    String unknownObjectPassedAsConstraintViolation(Object o);

    @Message(id = BASE_ASYNC + 0, value = "-- already canceled")
    String alreadyCanceled();

    @Message(id = BASE_ASYNC + 5, value = "-- already done")
    String alreadyDone();

    @Message(id = BASE_ASYNC + 10, value = "Already suspended")
    String alreadySuspended();

    @Message(id = BASE_ASYNC + 15, value = "cancel()")
    String cancel();

    @Message(id = BASE_ASYNC + 20, value = "-- cancelling with 503")
    String cancellingWith503();

    @Message(id = BASE_ASYNC + 25, value = "onComplete")
    String onComplete();

    @Message(id = BASE_ASYNC + 30, value = "onTimeout")
    String onTimeout();

    @Message(id = BASE_ASYNC + 35, value = "Request not suspended")
    String requestNotSuspended();

    @Message(id = BASE_ASYNC + 40, value = "scheduled timeout")
    String scheduledTimeout();

    @Message(id = BASE_ASYNC + 45, value = "scheduling timeout")
    String schedulingTimeout();

    @Message(id = 3000, value = "SelfExpandingBufferredInputStream is always marked at index 0.")
    String alwaysMarkedAtIndex0();

    @Message(id = 3005, value = "Ambiguous inherited qualifying annotations applied to method: %s")
    String ambiguousInheritedAnnotations(Method method);

    @Message(id = 3010, value = "annotations param was null")
    String annotationsParamNull();

    @Message(id = 3015, value = "application param was null")
    String applicationParamNull();

    @Message(id = 3017, value = "ClassCastException: attempting to cast {0} to {1}", format = Format.MESSAGE_FORMAT)
    String attemptingToCast(URL from, URL to);

    @Message(id = 3020, value = "Bad arguments passed to %s")
    String badArguments(String methodName);

    @Message(id = 3025, value = "Bad Base64 input character decimal {0} in array position {1}", format = Format.MESSAGE_FORMAT)
    String badBase64Character(int c, int pos);

    @Message(id = 3030, value = "Base64 input not properly padded.")
    String base64InputNotProperlyPadded();

    @Message(id = 3035, value = "Base64-encoded string must have at least four characters, but length specified was %s")
    String base64StringMustHaveFourCharacters(int len);

    @Message(id = 3040, value = "You have not set a base URI for the client proxy")
    String baseURINotSetForClientProxy();

    @Message(id = 3045, value = "CacheControl max-age header does not have a value: %s.")
    String cacheControlMaxAgeHeader(String value);

    @Message(id = 3050, value = "CacheControl s-maxage header does not have a value: %s.")
    String cacheControlSMaxAgeHeader(String value);

    @Message(id = 3055, value = "Cache-Control value is null")
    String cacheControlValueNull();

    @Message(id = 3060, value = "Callback was null")
    String callbackWasNull();

    @Message(id = 3065, value = "Cannot consume content type")
    String cannotConsumeContentType();

    @Message(id = 3070, value = "Cannot decode null source array.")
    String cannotDecodeNullSourceArray();

    @Message(id = 3075, value = "Cannot have length offset: %s")
    String cannotHaveLengthOffset(int len);

    @Message(id = 3080, value = "Cannot have negative offset: %s")
    String cannotHaveNegativeOffset(int off);

    @Message(id = 3085, value = "Cannot have offset of {0} and length of {1} with array of length {2}", format = Format.MESSAGE_FORMAT)
    String cannotHaveOffset(int off, int len, int srcLen);

    @Message(id = 3090, value = "You cannot inject AsynchronousResponse outside the scope of an HTTP request")
    String cannotInjectAsynchronousResponse();

    @Message(id = 3095, value = "You cannot inject into a form outside the scope of an HTTP request")
    String cannotInjectIntoForm();

    @Message(id = 3100, value = "You cannot send both form parameters and an entity body")
    String cannotSendFormParametersAndEntity();

    @Message(id = 3105, value = "Cannot serialize a null array.")
    String cannotSerializeNullArray();

    @Message(id = 3110, value = "Cannot serialize a null object.")
    String cannotSerializeNullObject();

    @Message(id = 3115, value = "You can only set one of LinkHeaderParam.rel() and LinkHeaderParam.title() for on {0}.{1}", format = Format.MESSAGE_FORMAT)
    String canOnlySetLinkHeaderRelOrTitle(String className, String methodName);

    @Message(id = 3120, value = "Can't set method after match")
    String cantSetMethod();

    @Message(id = 3125, value = "Can't set URI after match")
    String cantSetURI();

    @Message(id = 3130, value = "Class is not a root resource.  It, or one of its interfaces must be annotated with @Path: %s implements: ")
    String classIsNotRootResource(String className);

    @Message(id = 3135, value = "Class must be annotated with @Path to invoke path(Class)")
    String classMustBeAnnotatedWithPath();

    @Message(id = 3140, value = "ClientRequest doesn't implement Clonable.  Notify the RESTEasy staff right away.")
    String clientRequestDoesntSupportClonable();

    @Message(id = 3145, value = "Unable to find a MessageBodyReader of content-type {0} and type {1}", format = Format.MESSAGE_FORMAT)
    String clientResponseFailureMediaType(MediaType mediaType, Type type);

    @Message(id = 3150, value = "Error status {0} {1} returned", format = Format.MESSAGE_FORMAT)
    String clientResponseFailureStatus(int status, Status responseStatus);

    @Message(id = 3155, value = "Constructor arg paramMapping is invalid")
    String constructorMappingInvalid();

    @Message(id = 3160, value = "Control character in cookie value, consider BASE64 encoding your value")
    String controlCharacterInCookieValue();

    @Message(id = 3165, value = "Cookie header value was null")
    String cookieHeaderValueNull();

    @Message(id = 3170, value = "Could not create a default entity type factory of type {0}", format = Format.MESSAGE_FORMAT)
    String couldNotCreateEntityFactory(String className);

    @Message(id = 3175, value = "Could not create a default entity type factory of type {0}. {1}", format = Format.MESSAGE_FORMAT)
    String couldNotCreateEntityFactoryMessage(String className, String message);

    @Message(id = 3180, value = "Could not create a URI for {0} in {1}.{2}", format = Format.MESSAGE_FORMAT)
    String couldNotCreateURI(String uri, String className, String methodName);

    @Message(id = 3185, value = "Could not find class %s provided to JNDI Component Resource")
    String couldNotFindClassJndi(String className);

    @Message(id = 3190, value = "Could not find constructor for class: %s")
    String couldNotFindConstructor(String className);

    @Message(id = 3195, value = "URITemplateAnnotationResolver could not find a getter for param %s")
    String couldNotFindGetterForParam(String param);

    @Message(id = 3200, value = "Could not find message body reader for type: {0} of content type: {1}", format = Format.MESSAGE_FORMAT)
    String couldNotFindMessageBodyReader(Type type, MediaType mediaType);

    @Message(id = 3205, value = "Could not find a method for: %s")
    String couldNotFindMethod(Method method);

    @Message(id = 3210, value = "Could not find resource for full path: %s")
    String couldNotFindResourceForFullPath(URI uri);

    @Message(id = 3215, value = "could not find writer for content-type {0} type: {1}", format = Format.MESSAGE_FORMAT)
    String couldNotFindWriterForContentType(MediaType mediaType, String className);

    @Message(id = 3220, value = "URITemplateAnnotationResolver could not get a value for %s")
    String couldNotGetAValue(String param);

    @Message(id = 3225, value = "URITemplateAnnotationResolver could not introspect class %s")
    String couldNotIntrospectClass(String className);

    @Message(id = 3230, value = "Could not match up an implementation for LoggerType: %s")
    String couldNotMatchUpLoggerTypeImplementation(Class<?> loggerType);

    @Message(id = 3235, value = "Could not process method %s")
    String couldNotProcessMethod(Method method);

    @Message(id = 3240, value = "Could not read type {0} for media type {1}", format = Format.MESSAGE_FORMAT)
    String couldNotReadType(Type type, MediaType mediaType);

    @Message(id = 3245, value = "Date instances are not supported by this class.")
    String dateInstancesNotSupported();

    @Message(id = 3250, value = "date is null")
    String dateNull();

    @Message(id = 3255, value = "Data to encode was null.")
    String dataToEncodeNull();

    @Message(id = 3260, value = "dateValue is null")
    String dateValueNull();

    @Message(id = 3265, value = "Destination array with length {0} cannot have offset of {1} and still store three bytes.", format = Format.MESSAGE_FORMAT)
    String destinationArrayCannotStoreThreeBytes(int len, int off);

    @Message(id = 3270, value = "Destination array was null.")
    String destinationArrayNull();

    @Message(id = 3275, value = "Empty field in: %s.")
    String emptyFieldInHeader(String header);

    @Message(id = 3280, value = "empty host name")
    String emptyHostName();

    @Message(id = 3285, value = "The entity was already read, and it was of type %s")
    String entityAlreadyRead(Class<?> clazz);

    @Message(id = 3290, value = "Entity is not backed by an input stream")
    String entityNotBackedByInputStream();

    @Message(id = 3291, value = "Input stream was empty, there is no entity")
    String inputStreamWasEmpty();

    @Message(id = 3292, value = "Stream is closed")
    String streamIsClosed();

    @Message(id = 3295, value = "The object you supplied to registerInterceptor is not of an understood type")
    String entityNotOfUnderstoodType();

    @Message(id = 3300, value = "value of EntityTag is null")
    String entityTagValueNull();

    @Message(id = 3305, value = "Error in Base64 code reading stream.")
    String errorInBase64Stream();

    @Message(id = 3310, value = "eTag param null")
    String eTagParamNull();

    @Message(id = 3315, value = "You have exceeded your maximum forwards ResteasyProviderFactory allows.  Last good uri: %s")
    String excededMaximumForwards(String uri);

    @Message(id = 3316, value = "Expected '\', '\n', or '\r', got %s")
    String expectedExcapedCharacter(int n);

    @Message(id = 3317, value = "Expected Stream.MODE.GENERAL or Stream.MODE.RAW, got %s")
    String expectedStreamModeGeneralOrRaw(Stream.MODE mode);

    @Message(id = 3318, value = "Expected @Stream or @Produces(\"text/event-stream\")")
    String expectedStreamOrSseMediaType();

    @Message(id = 3319, value = "Expected String or MediaType, got %s")
    String expectedStringOrMediaType(Object o);

    @Message(id = 3320, value = "Failed processing arguments of %s")
    String failedProcessingArguments(String constructor);

    @Message(id = 3325, value = "Failed to construct %s")
    String failedToConstruct(String constructor);

    @Message(id = 3330, value = "Failed to create URI: %s")
    String failedToCreateUri(String buf);

    @Message(id = 3335, value = "Failed to parse cookie string '%s'")
    String failedToParseCookie(String value);

    @Message(id = 3340, value = "Failure parsing MediaType string: %s")
    String failureParsingMediaType(String type);

    @Message(id = 3345, value = "File is too big for this convenience method (%s bytes).")
    String fileTooBig(long len);

    @Message(id = 3350, value = "Garbage after quoted string: %s")
    String garbageAfterQuotedString(String header);

    @Message(id = 3355, value = "A GET request cannot have a body.")
    String getRequestCannotHaveBody();

    @Message(id = 3357, value = "GZIP input exceeds max size: %s")
    String gzipExceedsMaxSize(int size);

    @Message(id = 3360, value = "%s has no String constructor")
    String hasNoStringConstructor(String className);

    @Message(id = 3365, value = "Illegal hexadecimal character {0} at index {1}", format = Format.MESSAGE_FORMAT)
    String illegalHexadecimalCharacter(char ch, int index);

    @Message(id = 3370, value = "Illegal response media type: %s")
    String illegalResponseMediaType(String mediaType);

    @Message(id = 3375, value = "It is illegal to inject a @CookieParam into a singleton")
    String illegalToInjectCookieParam();

    @Message(id = 3380, value = "It is illegal to inject a @FormParam into a singleton")
    String illegalToInjectFormParam();

    @Message(id = 3385, value = "It is illegal to inject a @HeaderParam into a singleton")
    String illegalToInjectHeaderParam();

    @Message(id = 3390, value = "It is illegal to inject a @MatrixParam into a singleton")
    String illegalToInjectMatrixParam();

    @Message(id = 3395, value = "Illegal to inject a message body into a singleton into %s")
    String illegalToInjectMessageBody(AccessibleObject target);

    @Message(id = 3400, value = "Illegal to inject a non-interface type into a singleton")
    String illegalToInjectNonInterfaceType();

    @Message(id = 3405, value = "It is illegal to inject a @PathParam into a singleton")
    String illegalToInjectPathParam();

    @Message(id = 3410, value = "It is illegal to inject a @QueryParam into a singleton")
    String illegalToInjectQueryParam();

    @Message(id = 3415, value = "Illegal uri template: %s")
    String illegalUriTemplate(CharSequence template);

    @Message(id = 3420, value = "Improperly padded Base64 input.")
    String improperlyPaddedBase64Input();

    @Message(id = 3425, value = "Incorrect type parameter. ClientExceptionMapper requires a subclass of java.lang.Throwable as its type parameter.")
    String incorrectTypeParameterClientExceptionMapper();

    @Message(id = 3430, value = "Incorrect type parameter. ExceptionMapper requires a subclass of java.lang.Throwable as its type parameter.")
    String incorrectTypeParameterExceptionMapper();

    @Message(id = 3435, value = "Input stream was empty, there is no entity")
    String inputStreamEmpty();

    @Message(id = 3440, value = "Input string was null.")
    String inputStringNull();

    @Message(id = 3445, value = "Interceptor class must be annotated with @ServerInterceptor and/or @ClientInterceptor")
    String interceptorClassMustBeAnnotated();

    @Message(id = 3450, value = "Interceptor class %s must be annotated with @ServerInterceptor and/or @ClientInterceptor")
    String interceptorClassMustBeAnnotatedWithClass(Class<?> clazz);

    @Message(id = 3455, value = "interceptor null from class: %s")
    String interceptorNullFromClass(String className);

    @Message(id = 3460, value = "Invalid character in Base64 data.")
    String invalidCharacterInBase64Data();

    @Message(id = 3465, value = "Invalid escape character in cookie value.")
    String invalidEscapeCharacterInCookieValue();

    @Message(id = 3470, value = "invalid host")
    String invalidHost();

    @Message(id = 3475, value = "Invalid port value")
    String invalidPort();

    @Message(id = 3480, value = "%s is not initial request.  Its suspended and retried.  Aborting.")
    String isNotInitialRequest(String path);

    @Message(id = 3485, value = "JNDI Component Resource variable is not set correctly: jndi;class;true|false comma delimited")
    String jndiComponentResourceNotSetCorrectly();

    @Message(id = 3490, value = "The %s config in web.xml could not be parsed, accepted values are true,false or 1,0")
    String keyCouldNotBeParsed(String key);

    @Message(id = 3495, value = "lastModified param null")
    String lastModifiedParamNull();

    @Message(id = 3500, value = "Locale value is null")
    String localeValueNull();

    @Message(id = 3505, value = "Malformed media type: %s")
    String malformedMediaType(String header);

    @Message(id = 3510, value = "Malformed parameter: %s")
    String malformedParameter(String parameter);

    @Message(id = 3515, value = "Malformed parameters: %s.")
    String malformedParameters(String header);

    @Message(id = 3520, value = "Malformed quality value.")
    String malformedQualityValue();

    @Message(id = 3525, value = "map key is null")
    String mapKeyNull();

    @Message(id = 3530, value = "map value is null")
    String mapValueNull();

    @Message(id = 3535, value = "MarshalledEntity must have type information.")
    String marshalledEntityMustHaveTypeInfo();

    @Message(id = 3540, value = "MediaType q value cannot be greater than 1.0: %s")
    String mediaTypeQGreaterThan1(String mediaType);

    @Message(id = 3545, value = "MediaType q parameter must be a float: %s")
    String mediaTypeQMustBeFloat(MediaType mediaType);

    @Message(id = 3550, value = "MediaType q parameter must be a float: %s")
    String mediaTypeQWeightedLanguageMustBeFloat(String lang);

    @Message(id = 3555, value = "MediaType value is null")
    String mediaTypeValueNull();

    @Message(id = 3560, value = "method is not annotated with @Path")
    String methodNotAnnotatedWithPath();

    @Message(id = 3565, value = "method was null")
    String methodNull();

    @Message(id = 3570, value = "Missing type parameter.")
    String missingTypeParameter();

    @Message(id = 3575, value = "You must define a @Consumes type on your client method or interface, or supply a default")
    String mustDefineConsumes();

    @Message(id = 3580, value = "You must set either LinkHeaderParam.rel() or LinkHeaderParam.title() for on {0}.{1}", format = Format.MESSAGE_FORMAT)
    String mustSetLinkHeaderRelOrTitle(String className, String methodName);

    @Message(id = 3585, value = "You must set either the port or ssl port, not both")
    String mustSetEitherPortOrSSLPort();

    @Message(id = 3590, value = "You must set the port or ssl port")
    String mustSetPort();

    @Message(id = 3595, value = "You must use at least one, but no more than one http method annotation on: %s")
    String mustUseOneHttpMethod(String methodName);

    @Message(id = 3600, value = "name parameter is null")
    String nameParameterNull();

    @Message(id = 3605, value = "name param is null")
    String nameParamIsNull();

    @Message(id = 3610, value = "name param was null")
    String nameParamWasNull();

    @Message(id = 3615, value = "NewCookie value is null")
    String newCookieValueNull();

    @Message(id = 3620, value = "No content")
    String noContent();

    @Message(id = 3625, value = "No content.  Content-Length is 0")
    String noContentContentLength0();

    @Message(id = 3630, value = "%s is no longer a supported context param.  See documentation for more details")
    String noLongerASupportedContextParam(String paramName);

    @Message(id = 3635, value = "No match for accept header")
    String noMatchForAcceptHeader();

    @Message(id = 3640, value = "No output stream allowed")
    String noOutputStreamAllowed();

    @Message(id = 3645, value = "No public @Path annotated method for {0}.{1}", format = Format.MESSAGE_FORMAT)
    String noPublicPathAnnotatedMethod(String resource, String method);

    @Message(id = 3650, value = "No resource method found for %s, return 405 with Allow header")
    String noResourceMethodFoundForHttpMethod(String httpMethod);

    @Message(id = 3655, value = "No resource method found for options, return OK with Allow header")
    String noResourceMethodFoundForOptions();

    @Message(id = 3660, value = "No type information to extract entity with, use other getEntity() methods")
    String noTypeInformationForEntity();

    @Message(id = 3665, value = "Not allowed to reflect on method: %s")
    String notAllowedToReflectOnMethod(String methodName);

    @Message(id = 3670, value = "You did not supply enough values to fill path parameters")
    String notEnoughPathParameters();

    @Message(id = 3675, value = "%s is not a valid injectable type for @Suspend")
    String notValidInjectableType(String typeName);

    @Message(id = 3680, value = "Null subresource for path: %s.")
    String nullSubresource(URI uri);

    @Message(id = 3685, value = "null value")
    String nullValue();

    @Message(id = 3690, value = "Number of matched segments greater than actual")
    String numberOfMatchedSegments();

    @Message(id = 3695, value = "Odd number of characters.")
    String oddNumberOfCharacters();

    @Message(id = 3700, value = "Origin not allowed: %s")
    String originNotAllowed(String origin);

    @Message(id = 3705, value = "param was null")
    String paramNull();

    @Message(id = 3710, value = "A passed in value was null")
    String passedInValueNull();

    @Message(id = 3715, value = "path was null")
    String pathNull();

    @Message(id = 3720, value = "path param %s has not been provided by the parameter map")
    String pathParameterNotProvided(String param);

    @Message(id = 3723, value = "path param %s regex expression %s yields an empty string")
    String regexPathParameterResultEmpty(String param, String regexText);

    @Message(id = 3725, value = "pattern is null")
    String patternNull();

    @Message(id = 3730, value = "Accept-Language q value cannot be greater than 1.0 %s")
    String qValueCannotBeGreaterThan1(String lang);

    @Message(id = 3735, value = "Quoted string is not closed: %s")
    String quotedStringIsNotClosed(String header);

    @Message(id = 3740, value = "rel param was null")
    String relParamNull();

    @Message(id = 3745, value = "Removing a header is illegal for an HttpServletResponse")
    String removingHeaderIllegal();

    @Message(id = 3750, value = "Request media type is not application/x-www-form-urlencoded")
    String requestMediaTypeNotUrlencoded();

    @Message(id = 3755, value = "Request was already executed")
    String requestWasAlreadyExecuted();

    @Message(id = 3760, value = "resource was null")
    String resourceNull();

    @Message(id = 3765, value = "Response is closed.")
    String responseIsClosed();

    @Message(id = 3770, value = "Response is committed, can't handle exception")
    String responseIsCommitted();

    @Message(id = 3775, value = "schemeSpecificPart was null")
    String schemeSpecificPartNull();

    @Message(id = 3780, value = "A segment is null")
    String segmentNull();

    @Message(id = 3785, value = "segments parameter was null")
    String segmentsParameterNull();

    @Message(id = 3790, value = "Should be unreachable")
    String shouldBeUnreachable();

    @Message(id = 3795, value = "Source array with length {0} cannot have offset of {1} and process {2} bytes.", format = Format.MESSAGE_FORMAT)
    String sourceArrayCannotProcessBytes(int srcLen, int off, int len);

    @Message(id = 3800, value = "Source array with length {0} cannot have offset of {1} and still process four bytes.", format = Format.MESSAGE_FORMAT)
    String sourceArrayCannotProcessFourBytes(int srcLen, int off);

    @Message(id = 3805, value = "Source array was null.")
    String sourceArrayNull();

    @Message(id = 3810, value = "Stream wrapped by Signature, cannot reset the stream without destroying signature")
    String streamWrappedBySignature();

    @Message(id = 3815, value = "Subresource for target class has no qualifying annotations.: %s")
    String subresourceHasNoJaxRsAnnotations(String className);

    @Message(id = 3820, value = "tClass parameter is null")
    String tClassParameterNull();

    @Message(id = 3825, value = "Tailing garbage: %s")
    String tailingGarbage(String header);

    @Message(id = 3830, value = "NULL value for template parameter: %s")
    String templateParameterNull(String param);

    @Message(id = 3835, value = "templateValues param null")
    String templateValuesParamNull();

    @Message(id = 3840, value = "title param was null")
    String titleParamNull();

    @Message(id = 3845, value = "there are two method named %s")
    String twoMethodsSameName(String method);

    @Message(id = 3850, value = "type param was null")
    String typeParamNull();

    @Message(id = 3855, value = "Unable to create URI: %s")
    String unableToCreateURI(String buf);

    @Message(id = 3860, value = "Unable to decode query string")
    String unableToDecodeQueryString();

    @Message(id = 3865, value = "Unable to determine base class from Type")
    String unableToDetermineBaseClass();

    @Message(id = 3870, value = "Unable to extract parameter from http request: %s value is '%s'")
    String unableToExtractParameter(String paramSignature, String strVal);

    @Message(id = 3875, value = "Unable to find a constructor that takes a String param or a valueOf() or fromString() method for {0} on {1} for basetype: {2}", format = Format.MESSAGE_FORMAT)
    String unableToFindConstructor(String paramSignature, AccessibleObject target, String className);

    @Message(id = 3876, value = "Unable to find a constructor that takes a String param or a valueOf() or fromString() method for %s with a base type of %s")
    RuntimeException unableToFindStringConstructor(String paramSignature, String className);

    @Message(id = 3880, value = "Unable to find contextual data of type: %s")
    String unableToFindContextualData(String className);

    @Message(id = 3885, value = "Unable to find InjectorFactory implementation.")
    String unableToFindInjectorFactory();

    @Message(id = 3890, value = "Unable to find resource associated with path: %s.")
    String unableToFindJaxRsResource(String path);

    @Message(id = 3895, value = "Unable to find a public constructor for class %s")
    String unableToFindPublicConstructorForClass(String className);

    @Message(id = 3900, value = "Unable to find a public constructor for provider class %s")
    String unableToFindPublicConstructorForProvider(String className);

    @Message(id = 3905, value = "Unable to find type arguments of %s")
    String unableToFindTypeArguments(Class<?> clazz);

    @Message(id = 3910, value = "Unable to instantiate ClientExceptionMapper")
    String unableToInstantiateClientExceptionMapper();

    @Message(id = 3915, value = "Unable to instantiate context object %s")
    String unableToInstantiateContextObject(String key);

    @Message(id = 3920, value = "Unable to instantiate ContextResolver")
    String unableToInstantiateContextResolver();

    @Message(id = 3925, value = "Unable to instantiate ExceptionMapper")
    String unableToInstantiateExceptionMapper();

    @Message(id = 3930, value = "Unable to instantiate @Form class. No no-arg constructor.")
    String unableToInstantiateForm();

    @Message(id = 3935, value = "Unable to instantiate InjectorFactory implementation.")
    String unableToInstantiateInjectorFactory();

    @Message(id = 3940, value = "Unable to instantiate MessageBodyReader")
    String unableToInstantiateMessageBodyReader();

    @Message(id = 3945, value = "Unable to instantiate MessageBodyWriter")
    String unableToInstantiateMessageBodyWriter();

    @Message(id = 3950, value = "Unable to parse the date %s")
    String unableToParseDate(String dateValue);

    @Message(id = 3955, value = "Unable to parse Link header.  No end to link: %s")
    String unableToParseLinkHeaderNoEndToLink(String value);

    @Message(id = 3960, value = "Unable to parse Link header.  No end to parameter: %s")
    String unableToParseLinkHeaderNoEndToParameter(String value);

    @Message(id = 3965, value = "Unable to parse Link header. Too many links in declaration: %s")
    String unableToParseLinkHeaderTooManyLinks(String value);

    @Message(id = 3970, value = "Unable to resolve type variable")
    String unableToResolveTypeVariable();

    @Message(id = 3975, value = "Unable to unmarshall response for %s")
    String unableToUnmarshalResponse(String attributeExceptionsTo);

    @Message(id = 3977, value = "Unexpected Number subclass: %s")
    String unexpectedNumberSubclass(String classname);

    @Message(id = 3980, value = "Unknown interceptor precedence: %s")
    String unknownInterceptorPrecedence(String precedence);

    @Message(id = 3985, value = "Unknown media type for response entity")
    String unknownMediaTypeResponseEntity();

    @Message(id = 3990, value = "Unknown @PathParam: {0} for path: {1}", format = Format.MESSAGE_FORMAT)
    String unknownPathParam(String paramName, String path);

    @Message(id = 3995, value = "Unknown state.  You have a Listener messing up what resteasy expects")
    String unknownStateListener();

    @Message(id = 4000, value = "Unsupported collectionType: %s")
    String unsupportedCollectionType(Class<?> clazz);

    @Message(id = 4005, value = "Unsupported parameter: %s")
    String unsupportedParameter(String parameter);

    @Message(id = 4010, value = "URI was null")
    String uriNull();

    @Message(id = 4015, value = "uri param was null")
    String uriParamNull();

    @Message(id = 4020, value = "uriTemplate parameter is null")
    String uriTemplateParameterNull();

    @Message(id = 4025, value = "URI value is null")
    String uriValueNull();

    @Message(id = 4030, value = "User is not registered: %s")
    String userIsNotRegistered(String user);

    @Message(id = 4035, value = "A value was null")
    String valueNull();

    @Message(id = 4040, value = "value param is null")
    String valueParamIsNull();

    @Message(id = 4045, value = "value param was null")
    String valueParamWasNull();

    @Message(id = 4050, value = "values param is null")
    String valuesParamIsNull();

    @Message(id = 4055, value = "values param was null")
    String valuesParamWasNull();

    @Message(id = 4060, value = "values parameter is null")
    String valuesParameterNull();

    @Message(id = 4065, value = "Variant list must not be zero")
    String variantListMustNotBeZero();

    @Message(id = 4070, value = "Wrong password for: %s")
    String wrongPassword(String user);

    @Message(id = 4080, value = "WebTarget is not set for creating SseEventSource")
    String webTargetIsNotSetForEventSource();

    @Message(id = 4081, value = "EventSource is not ready to open")
    String eventSourceIsNotReadyForOpen();

    @Message(id = 4082, value = "No suitable message body writer for class : %s")
    String notFoundMBW(String className);

    @Message(id = 4083, value = "Sever sent event feature requries HttpServlet30Dispatcher")
    String asyncServletIsRequired();

    @Message(id = 4084, value = "Failed to read SseEvent")
    String readEventException();

    @Message(id = 4085, value = "%s is not set for OutboundSseEvent builder")
    String nullValueSetToCreateOutboundSseEvent(String field);

    @Message(id = 4086, value = "Failed to write data to InBoundSseEvent")
    String failedToWriteDataToInboudEvent();

    @Message(id = 4087, value = "No suitable message body reader for class : %s")
    String notFoundMBR(String className);

    @Message(id = 4088, value = "Failed to read data from InboundSseEvent")
    String failedToReadData();

    @Message(id = 4089, value = "Failed to create SseEventOutput")
    String failedToCreateSseEventOutput();

    @Message(id = 4090, value = "Unable to instantiate AsyncResponseProvider")
    String unableToInstantiateAsyncResponseProvider();

    @Message(id = 4091, value = "Unable to instantiate AsyncStreamProvider")
    String unableToInstantiateAsyncStreamProvider();

    @Message(id = 4092, value = "SseEventSink is closed")
    String sseEventSinkIsClosed();

    @Message(id = 4095, value = "Unable to instantiate AsyncClientResponseProvider")
    String unableToInstantiateAsyncClientResponseProvider();

    @Message(id = 4096, value = "Unable to instantiate AsyncClientStreamProvider")
    String unableToInstantiateAsyncClientStreamProvider();

    @Message(id = 4093, value = "SseBroadcaster is closed")
    String sseBroadcasterIsClosed();

    @Message(id = 4094, value = "Unable to instantiate ContextInjector")
    String unableToInstantiateContextInjector();

    @Message(id = 4097, value = "Registering a context resolver doesn't support lambdas")
    String registeringContextResolverAsLambda();

    @Message(id = 4098, value = "MultiValuedArrayParamConverter expected array, not: %s")
    String expectedArray(String className);

    @Message(id = 4099, value = "MultiValuedCollectionParamConverter unable to parse: %s")
    String unableToParse(String s);

    @Message(id = 5042, value = "Multiple resource methods match request %s. Matching methods: %s")
    String multipleMethodsMatchFailFast(String request, String[] methods);

    @Message(id = 3013, value = "Error creating array from %s")
    String errorCreatingArray(String s);

    @Message(id = 5043, value = "Value %s cannot be converted to type %s with property name %s")
    IllegalArgumentException cannotConvertParameter(Object value, Class<?> type, String name);

    @Message(id = 5044, value = "Property %s not found")
    NoSuchElementException propertyNotFound(String name);

    @Message(id = 5050, value = "The executor has been shutdown and is no longer available.")
    IllegalStateException executorShutdown();

    @Message(id = 5051, value = "Required context value not found.")
    IllegalArgumentException requiredContextParameterNotFound();

    @Message(id = 5060, value = "Failed to load services for type %s")
    UncheckedIOException failedToLoadService(@Cause IOException e, Class<?> type);

    @Message(id = 5061, value = "Failed to construct type %s")
    RuntimeException failedToConstructClass(@Cause Throwable cause, Class<?> type);

    @Message(id = 5070, value = "Invalid protocol %s. Only protocols %s are allowed.")
    IllegalArgumentException invalidProtocol(String protocol, String... values);

    @Message(id = 5071, value = "Invalid argument %s for property %s. Require type is %s.")
    IllegalArgumentException invalidArgumentType(String propertyName, Object value, Class<?> expected);

    @Message(id = 5072, value = "Parameter %s is a required parameter and cannot be set to null.")
    String nullParameter(String name);

    @Message(id = 5073, value = "Failed to scan for resources.")
    UncheckedIOException failedToScanResources(@Cause IOException cause);

    @Message(id = 5074, value = "No implementation of %s was found.")
    IllegalStateException noImplementationFound(String name);

    @Message(id = 5075, value = "Could no load default SSL context")
    IllegalStateException couldNotLoadSslContext(@Cause Throwable cause);

    @Message(id = 5076, value = "A ResteasyDeployment object required")
    IllegalArgumentException deploymentRequired();

    @Message(id = 5080, value = "The stream has already been exported.")
    Supplier<IllegalStateException> alreadyExported();

    @Message(id = 5081, value = "File limit of %s has been reached. The entity cannot be processed. Increase the " +
            "size with the configuration property %s.")
    IllegalStateException fileLimitReached(Threshold limit, String propertyName);

    @Message(id = 5082, value = "The generic type for %s could not be determined based on %s.")
    IllegalArgumentException couldNotDetermineGenericType(String typeName, String implName);

    @Message(id = 5085, value = "Failed to resolve the SSLContext for the client.")
    RuntimeException failedToResolveSSLContext(@Cause Throwable cause);

    @Message(id = 5095, value = "The HTTP session has been invalidated.")
    IOException invalidSession();

    @Message(id = 5096, value = "Unable to find constructor with arguments [%s] or a no-arg constructor for type %s")
    IllegalStateException unableToFindConstructor(String args, String className);

    @Message(id = 5097, value = "Deployment has either not been started or stopped. There are no executor services available.")
    IllegalStateException executorNotAvailable();

    @Message(id = 5110, value = "The annotation @%s is not supported for parameter extraction.")
    IllegalArgumentException unsupportedAnnotation(String annotationName);
}
