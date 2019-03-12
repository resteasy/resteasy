package org.jboss.resteasy.resteasy_jaxrs.i18n;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;

import javax.validation.ElementKind;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.resteasy.annotations.Stream;
import org.jboss.resteasy.api.validation.ConstraintType;
import org.jboss.resteasy.logging.Logger.LoggerType;
import org.jboss.resteasy.util.WeightedLanguage;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 13, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 3000;
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

   @Message(id = BASE + 00, value = "SelfExpandingBufferredInputStream is always marked at index 0.")
   String alwaysMarkedAtIndex0();

   @Message(id = BASE + 05, value = "Ambiguous inherited JAX-RS annotations applied to method: %s")
   String ambiguousInheritedAnnotations(Method method);

   @Message(id = BASE + 10, value =  "annotations param was null")
   String annotationsParamNull();

   @Message(id = BASE + 15, value = "application param was null")
   String applicationParamNull();

   @Message(id = BASE + 17, value = "ClassCastException: attempting to cast {0} to {1}", format=Format.MESSAGE_FORMAT)
   String attemptingToCast(URL from, URL to);

   @Message(id = BASE + 20, value = "Bad arguments passed to %s")
   String badArguments(String methodName);

   @Message(id = BASE + 25, value = "Bad Base64 input character decimal {0} in array position {1}", format=Format.MESSAGE_FORMAT)
   String badBase64Character(int c, int pos);

   @Message(id = BASE + 30, value = "Base64 input not properly padded.")
   String base64InputNotProperlyPadded();

   @Message(id = BASE + 35, value = "Base64-encoded string must have at least four characters, but length specified was %s")
   String base64StringMustHaveFourCharacters(int len);

   @Message(id = BASE + 40, value = "You have not set a base URI for the client proxy")
   String baseURINotSetForClientProxy();

   @Message(id = BASE + 45, value = "CacheControl max-age header does not have a value: %s.")
   String cacheControlMaxAgeHeader(String value);

   @Message(id = BASE + 50, value = "CacheControl s-maxage header does not have a value: %s.")
   String cacheControlSMaxAgeHeader(String value);

   @Message(id = BASE + 55, value = "Cache-Control value is null")
   String cacheControlValueNull();

   @Message(id = BASE + 60, value = "Callback was null")
   String callbackWasNull();

   @Message(id = BASE + 65, value = "Cannot consume content type")
   String cannotConsumeContentType();

   @Message(id = BASE + 70, value = "Cannot decode null source array.")
   String cannotDecodeNullSourceArray();

   @Message(id = BASE + 75, value = "Cannot have length offset: %s")
   String cannotHaveLengthOffset(int len);

   @Message(id = BASE + 80, value = "Cannot have negative offset: %s")
   String cannotHaveNegativeOffset(int off);

   @Message(id = BASE + 85, value = "Cannot have offset of {0} and length of {1} with array of length {2}", format=Format.MESSAGE_FORMAT)
   String cannotHaveOffset(int off, int len, int srcLen);

   @Message(id = BASE + 90, value = "You cannot inject AsynchronousResponse outside the scope of an HTTP request")
   String cannotInjectAsynchronousResponse();

   @Message(id = BASE + 95, value = "You cannot inject into a form outside the scope of an HTTP request")
   String cannotInjectIntoForm();

   @Message(id = BASE + 100, value = "You cannot send both form parameters and an entity body")
   String cannotSendFormParametersAndEntity();

   @Message(id = BASE + 105, value = "Cannot serialize a null array.")
   String cannotSerializeNullArray();

   @Message(id = BASE + 110, value = "Cannot serialize a null object.")
   String cannotSerializeNullObject();

   @Message(id = BASE + 115, value = "You can only set one of LinkHeaderParam.rel() and LinkHeaderParam.title() for on {0}.{1}", format=Format.MESSAGE_FORMAT)
   String canOnlySetLinkHeaderRelOrTitle(String className, String methodName);

   @Message(id = BASE + 120, value = "Can't set method after match")
   String cantSetMethod();

   @Message(id = BASE + 125, value = "Can't set URI after match")
   String cantSetURI();

   @Message(id = BASE + 130, value = "Class is not a root resource.  It, or one of its interfaces must be annotated with @Path: %s implements: ")
   String classIsNotRootResource(String className);

   @Message(id = BASE + 135, value = "Class must be annotated with @Path to invoke path(Class)")
   String classMustBeAnnotatedWithPath();

   @Message(id = BASE + 140, value = "ClientRequest doesn't implement Clonable.  Notify the RESTEasy staff right away.")
   String clientRequestDoesntSupportClonable();

   @Message(id = BASE + 145, value = "Unable to find a MessageBodyReader of content-type {0} and type {1}", format=Format.MESSAGE_FORMAT)
   String clientResponseFailureMediaType(MediaType mediaType, Type type);

   @Message(id = BASE + 150, value = "Error status {0} {1} returned", format=Format.MESSAGE_FORMAT)
   String clientResponseFailureStatus(int status, Status responseStatus);

   @Message(id = BASE + 155, value = "Constructor arg paramMapping is invalid")
   String constructorMappingInvalid();

   @Message(id = BASE + 160, value = "Control character in cookie value, consider BASE64 encoding your value")
   String controlCharacterInCookieValue();

   @Message(id = BASE + 165, value = "Cookie header value was null")
   String cookieHeaderValueNull();

   @Message(id = BASE + 170, value = "Could not create a default entity type factory of type {0}", format=Format.MESSAGE_FORMAT)
   String couldNotCreateEntityFactory(String className);

   @Message(id = BASE + 175, value = "Could not create a default entity type factory of type {0}. {1}", format=Format.MESSAGE_FORMAT)
   String couldNotCreateEntityFactoryMessage(String className, String message);

   @Message(id = BASE + 180, value = "Could not create a URI for {0} in {1}.{2}", format=Format.MESSAGE_FORMAT)
   String couldNotCreateURI(String uri, String className, String methodName);

   @Message(id = BASE + 185, value = "Could not find class %s provided to JNDI Component Resource")
   String couldNotFindClassJndi(String className);

   @Message(id = BASE + 190, value = "Could not find constructor for class: %s")
   String couldNotFindConstructor(String className);

   @Message(id = BASE + 195, value = "URITemplateAnnotationResolver could not find a getter for param %s")
   String couldNotFindGetterForParam(String param);

   @Message(id = BASE + 200, value = "Could not find message body reader for type: {0} of content type: {1}", format=Format.MESSAGE_FORMAT)
   String couldNotFindMessageBodyReader(Type type, MediaType mediaType);

   @Message(id = BASE + 205, value = "Could not find a method for: %s")
   String couldNotFindMethod(Method method);

   @Message(id = BASE + 210, value = "Could not find resource for full path: %s")
   String couldNotFindResourceForFullPath(URI uri);

   @Message(id = BASE + 215, value = "could not find writer for content-type {0} type: {1}", format=Format.MESSAGE_FORMAT)
   String couldNotFindWriterForContentType(MediaType mediaType, String className);

   @Message(id = BASE + 220, value = "URITemplateAnnotationResolver could not get a value for %s")
   String couldNotGetAValue(String param);

   @Message(id = BASE + 225, value = "URITemplateAnnotationResolver could not introspect class %s")
   String couldNotIntrospectClass(String className);

   @Message(id = BASE + 230, value = "Could not match up an implementation for LoggerType: %s")
   String couldNotMatchUpLoggerTypeImplementation(LoggerType loggerType);

   @Message(id = BASE + 235, value = "Could not process method %s")
   String couldNotProcessMethod(Method method);

   @Message(id = BASE + 240, value = "Could not read type {0} for media type {1}", format=Format.MESSAGE_FORMAT)
   String couldNotReadType(Type type, MediaType mediaType);

   @Message(id = BASE + 245, value = "Date instances are not supported by this class.")
   String dateInstancesNotSupported();

   @Message(id = BASE + 250, value = "date is null")
   String dateNull();

   @Message(id = BASE + 255, value = "Data to encode was null.")
   String dataToEncodeNull();

   @Message(id = BASE + 260, value = "dateValue is null")
   String dateValueNull();

   @Message(id = BASE + 265, value = "Destination array with length {0} cannot have offset of {1} and still store three bytes.", format=Format.MESSAGE_FORMAT)
   String destinationArrayCannotStoreThreeBytes(int len, int off);

   @Message(id = BASE + 270, value = "Destination array was null.")
   String destinationArrayNull();

   @Message(id = BASE + 275, value = "Empty field in: %s.")
   String emptyFieldInHeader(String header);

   @Message(id = BASE + 280, value = "empty host name")
   String emptyHostName();

   @Message(id = BASE + 285, value = "The entity was already read, and it was of type %s")
   String entityAlreadyRead(Class<?> clazz);

   @Message(id = BASE + 290, value = "Entity is not backed by an input stream")
   String entityNotBackedByInputStream();

   @Message(id = BASE + 291, value = "Input stream was empty, there is no entity")
   String inputStreamWasEmpty();

   @Message(id = BASE + 292, value = "Stream is closed")
   String streamIsClosed();

   @Message(id = BASE + 295, value = "The object you supplied to registerInterceptor is not of an understood type")
   String entityNotOfUnderstoodType();

   @Message(id = BASE + 300, value = "value of EntityTag is null")
   String entityTagValueNull();

   @Message(id = BASE + 305, value = "Error in Base64 code reading stream.")
   String errorInBase64Stream();

   @Message(id = BASE + 310, value = "eTag param null")
   String eTagParamNull();

   @Message(id = BASE + 315, value = "You have exceeded your maximum forwards ResteasyProviderFactory allows.  Last good uri: %s")
   String excededMaximumForwards(String uri);

   @Message(id = BASE + 320, value = "Failed processing arguments of %s")
   String failedProcessingArguments(String constructor);

   @Message(id = BASE + 316, value = "Expected '\', '\n', or '\r', got %s")
   String expectedExcapedCharacter(int n);

   @Message(id = BASE + 317, value = "Expected Stream.MODE.GENERAL or Stream.MODE.RAW, got %s")
   String expectedStreamModeGeneralOrRaw(Stream.MODE mode);

   @Message(id = BASE + 318, value = "Expected @Stream or @Produces(\"text/event-stream\")")
   String expectedStreamOrSseMediaType();

   @Message(id = BASE + 319, value = "Expected String or MediaType, got %s")
   String expectedStringOrMediaType(Object o);

   @Message(id = BASE + 325, value = "Failed to construct %s")
   String failedToConstruct(String constructor);

   @Message(id = BASE + 330, value = "Failed to create URI: %s")
   String failedToCreateUri(String buf);

   @Message(id = BASE + 335, value = "Failed to parse cookie string '%s'")
   String failedToParseCookie(String value);

   @Message(id = BASE + 340, value = "Failure parsing MediaType string: %s")
   String failureParsingMediaType(String type);

   @Message(id = BASE + 345, value = "File is too big for this convenience method (%s bytes).")
   String fileTooBig(long len);

   @Message(id = BASE + 350, value = "Garbage after quoted string: %s")
   String garbageAfterQuotedString(String header);

   @Message(id = BASE + 355, value = "A GET request cannot have a body.")
   String getRequestCannotHaveBody();

   @Message(id = BASE + 357, value = "GZIP input exceeds max size: %s")
   String gzipExceedsMaxSize(int size);

   @Message(id = BASE + 360, value = "%s has no String constructor")
   String hasNoStringConstructor(String className);

   @Message(id = BASE + 365, value = "Illegal hexadecimal character {0} at index {1}", format=Format.MESSAGE_FORMAT)
   String illegalHexadecimalCharacter(char ch, int index);

   @Message(id = BASE + 370, value = "Illegal response media type: %s")
   String illegalResponseMediaType(String mediaType);

   @Message(id = BASE + 375, value = "It is illegal to inject a @CookieParam into a singleton")
   String illegalToInjectCookieParam();

   @Message(id = BASE + 380, value = "It is illegal to inject a @FormParam into a singleton")
   String illegalToInjectFormParam();

   @Message(id = BASE + 385, value = "It is illegal to inject a @HeaderParam into a singleton")
   String illegalToInjectHeaderParam();

   @Message(id = BASE + 390, value = "It is illegal to inject a @MatrixParam into a singleton")
   String illegalToInjectMatrixParam();

   @Message(id = BASE + 395, value = "Illegal to inject a message body into a singleton into %s")
   String illegalToInjectMessageBody(AccessibleObject target);

   @Message(id = BASE + 400, value = "Illegal to inject a non-interface type into a singleton")
   String illegalToInjectNonInterfaceType();

   @Message(id = BASE + 405, value = "It is illegal to inject a @PathParam into a singleton")
   String illegalToInjectPathParam();

   @Message(id = BASE + 410, value = "It is illegal to inject a @QueryParam into a singleton")
   String illegalToInjectQueryParam();

   @Message(id = BASE + 415, value = "Illegal uri template: %s")
   String illegalUriTemplate(String template);

   @Message(id = BASE + 420, value = "Improperly padded Base64 input.")
   String improperlyPaddedBase64Input();

   @Message(id = BASE + 425, value = "Incorrect type parameter. ClientExceptionMapper requires a subclass of java.lang.Throwable as its type parameter.")
   String incorrectTypeParameterClientExceptionMapper();

   @Message(id = BASE + 430, value = "Incorrect type parameter. ExceptionMapper requires a subclass of java.lang.Throwable as its type parameter.")
   String incorrectTypeParameterExceptionMapper();

   @Message(id = BASE + 435, value = "Input stream was empty, there is no entity")
   String inputStreamEmpty();

   @Message(id = BASE + 440, value = "Input string was null.")
   String inputStringNull();

   @Message(id = BASE + 445, value = "Interceptor class must be annotated with @ServerInterceptor and/or @ClientInterceptor")
   String interceptorClassMustBeAnnotated();

   @Message(id = BASE + 450, value = "Interceptor class %s must be annotated with @ServerInterceptor and/or @ClientInterceptor")
   String interceptorClassMustBeAnnotatedWithClass(Class<?> clazz);

   @Message(id = BASE + 455, value = "interceptor null from class: %s")
   String interceptorNullFromClass(String className);

   @Message(id = BASE + 460, value = "Invalid character in Base64 data.")
   String invalidCharacterInBase64Data();

   @Message(id = BASE + 465, value = "Invalid escape character in cookie value.")
   String invalidEscapeCharacterInCookieValue();

   @Message(id = BASE + 470, value = "invalid host")
   String invalidHost();

   @Message(id = BASE + 475, value = "Invalid port value")
   String invalidPort();

   @Message(id = BASE + 480, value = "%s is not initial request.  Its suspended and retried.  Aborting.")
   String isNotInitialRequest(String path);

   @Message(id = BASE + 485, value = "JNDI Component Resource variable is not set correctly: jndi;class;true|false comma delimited")
   String jndiComponentResourceNotSetCorrectly();

   @Message(id = BASE + 490, value = "The %s config in web.xml could not be parsed, accepted values are true,false or 1,0")
   String keyCouldNotBeParsed(String key);

   @Message(id = BASE + 495, value = "lastModified param null")
   String lastModifiedParamNull();

   @Message(id = BASE + 500, value = "Locale value is null")
   String localeValueNull();

   @Message(id = BASE + 505, value = "Malformed media type: %s")
   String malformedMediaType(String header);

   @Message(id = BASE + 510, value = "Malformed parameter: %s")
   String malformedParameter(String parameter);

   @Message(id = BASE + 515, value = "Malformed parameters: %s.")
   String malformedParameters(String header);

   @Message(id = BASE + 520, value = "Malformed quality value.")
   String malformedQualityValue();

   @Message(id = BASE + 525, value = "map key is null")
   String mapKeyNull();

   @Message(id = BASE + 530, value = "map value is null")
   String mapValueNull();

   @Message(id = BASE + 535, value = "MarshalledEntity must have type information.")
   String marshalledEntityMustHaveTypeInfo();

   @Message(id = BASE + 540, value = "MediaType q value cannot be greater than 1.0: %s")
   String mediaTypeQGreaterThan1(String mediaType);

   @Message(id = BASE + 545, value = "MediaType q parameter must be a float: %s")
   String mediaTypeQMustBeFloat(MediaType mediaType);

   @Message(id = BASE + 550, value = "MediaType q parameter must be a float: %s")
   String mediaTypeQWeightedLanguageMustBeFloat(WeightedLanguage lang);

   @Message(id = BASE + 555, value = "MediaType value is null")
   String mediaTypeValueNull();

   @Message(id = BASE + 560, value = "method is not annotated with @Path")
   String methodNotAnnotatedWithPath();

   @Message(id = BASE + 565, value = "method was null")
   String methodNull();

   @Message(id = BASE + 570, value = "Missing type parameter.")
   String missingTypeParameter();

   @Message(id = BASE + 575, value = "You must define a @Consumes type on your client method or interface, or supply a default")
   String mustDefineConsumes();

   @Message(id = BASE + 580, value = "You must set either LinkHeaderParam.rel() or LinkHeaderParam.title() for on {0}.{1}", format=Format.MESSAGE_FORMAT)
   String mustSetLinkHeaderRelOrTitle(String className, String methodName);

   @Message(id = BASE + 585, value = "You must set either the port or ssl port, not both")
   String mustSetEitherPortOrSSLPort();

   @Message(id = BASE + 590, value = "You must set the port or ssl port")
   String mustSetPort();

   @Message(id = BASE + 595, value = "You must use at least one, but no more than one http method annotation on: %s")
   String mustUseOneHttpMethod(String methodName);

   @Message(id = BASE + 600, value = "name parameter is null")
   String nameParameterNull();

   @Message(id = BASE + 605, value = "name param is null")
   String nameParamIsNull();

   @Message(id = BASE + 610, value = "name param was null")
   String nameParamWasNull();

   @Message(id = BASE + 615, value = "NewCookie value is null")
   String newCookieValueNull();

   @Message(id = BASE + 620, value = "No content")
   String noContent();

   @Message(id = BASE + 625, value = "No content.  Content-Length is 0")
   String noContentContentLength0();

   @Message(id = BASE + 630, value = "%s is no longer a supported context param.  See documentation for more details")
   String noLongerASupportedContextParam(String paramName);

   @Message(id = BASE + 635, value = "No match for accept header")
   String noMatchForAcceptHeader();

   @Message(id = BASE + 640, value = "No output stream allowed")
   String noOutputStreamAllowed();

   @Message(id = BASE + 645, value = "No public @Path annotated method for {0}.{1}", format=Format.MESSAGE_FORMAT)
   String noPublicPathAnnotatedMethod(String resource, String method);

   @Message(id = BASE + 650, value = "No resource method found for %s, return 405 with Allow header")
   String noResourceMethodFoundForHttpMethod(String httpMethod);

   @Message(id = BASE + 655, value = "No resource method found for options, return OK with Allow header")
   String noResourceMethodFoundForOptions();

   @Message(id = BASE + 660, value = "No type information to extract entity with, use other getEntity() methods")
   String noTypeInformationForEntity();

   @Message(id = BASE + 665, value = "Not allowed to reflect on method: %s")
   String notAllowedToReflectOnMethod(String methodName);

   @Message(id = BASE + 670, value = "You did not supply enough values to fill path parameters")
   String notEnoughPathParameters();

   @Message(id = BASE + 675, value = "%s is not a valid injectable type for @Suspend")
   String notValidInjectableType(String typeName);

   @Message(id = BASE + 680, value = "Null subresource for path: %s.")
   String nullSubresource(URI uri);

   @Message(id = BASE + 685, value = "null value")
   String nullValue();

   @Message(id = BASE + 690, value = "Number of matched segments greater than actual")
   String numberOfMatchedSegments();

   @Message(id = BASE + 695, value = "Odd number of characters.")
   String oddNumberOfCharacters();

   @Message(id = BASE + 700, value = "Origin not allowed: %s")
   String originNotAllowed(String origin);

   @Message(id = BASE + 705, value = "param was null")
   String paramNull();

   @Message(id = BASE + 710, value = "A passed in value was null")
   String passedInValueNull();

   @Message(id = BASE + 715, value = "path was null")
   String pathNull();

   @Message(id = BASE + 720, value = "path param %s has not been provided by the parameter map")
   String pathParameterNotProvided(String param);

   @Message(id = BASE + 725, value = "pattern is null")
   String patternNull();

   @Message(id = BASE + 730, value = "Accept-Language q value cannot be greater than 1.0 %s")
   String qValueCannotBeGreaterThan1(String lang);

   @Message(id = BASE + 735, value = "Quoted string is not closed: %s")
   String quotedStringIsNotClosed(String header);

   @Message(id = BASE + 740, value = "rel param was null")
   String relParamNull();

   @Message(id = BASE + 745, value = "Removing a header is illegal for an HttpServletResponse")
   String removingHeaderIllegal();

   @Message(id = BASE + 750, value = "Request media type is not application/x-www-form-urlencoded")
   String requestMediaTypeNotUrlencoded();

   @Message(id = BASE + 755, value = "Request was already executed")
   String requestWasAlreadyExecuted();

   @Message(id = BASE + 760, value = "resource was null")
   String resourceNull();

   @Message(id = BASE + 765, value = "Response is closed.")
   String responseIsClosed();

   @Message(id = BASE + 770, value = "Response is committed, can't handle exception")
   String responseIsCommitted();

   @Message(id = BASE + 775, value = "schemeSpecificPart was null")
   String schemeSpecificPartNull();

   @Message(id = BASE + 780, value = "A segment is null")
   String segmentNull();

   @Message(id = BASE + 785, value = "segments parameter was null")
   String segmentsParameterNull();

   @Message(id = BASE + 790, value = "Should be unreachable")
   String shouldBeUnreachable();

   @Message(id = BASE + 795, value = "Source array with length {0} cannot have offset of {1} and process {2} bytes.", format=Format.MESSAGE_FORMAT)
   String sourceArrayCannotProcessBytes(int srcLen, int off, int len);

   @Message(id = BASE + 800, value = "Source array with length {0} cannot have offset of {1} and still process four bytes.", format=Format.MESSAGE_FORMAT)
   String sourceArrayCannotProcessFourBytes(int srcLen, int off);

   @Message(id = BASE + 805, value = "Source array was null.")
   String sourceArrayNull();

   @Message(id = BASE + 810, value = "Stream wrapped by Signature, cannot reset the stream without destroying signature")
   String streamWrappedBySignature();

   @Message(id = BASE + 815, value = "Subresource for target class has no jax-rs annotations.: %s")
   String subresourceHasNoJaxRsAnnotations(String className);

   @Message(id = BASE + 820, value = "tClass parameter is null")
   String tClassParameterNull();

   @Message(id = BASE + 825, value = "Tailing garbage: %s")
   String tailingGarbage(String header);

   @Message(id = BASE + 830, value = "NULL value for template parameter: %s")
   String templateParameterNull(String param);

   @Message(id = BASE + 835, value = "templateValues param null")
   String templateValuesParamNull();

   @Message(id = BASE + 840, value = "title param was null")
   String titleParamNull();

   @Message(id = BASE + 845, value = "there are two method named %s")
   String twoMethodsSameName(String method);

   @Message(id = BASE + 850, value = "type param was null")
   String typeParamNull();

   @Message(id = BASE + 855, value = "Unable to create URI: %s")
   String unableToCreateURI(String buf);

   @Message(id = BASE + 860, value = "Unable to decode query string")
   String unableToDecodeQueryString();

   @Message(id = BASE + 865, value = "Unable to determine base class from Type")
   String unableToDetermineBaseClass();

   @Message(id = BASE + 870, value = "Unable to extract parameter from http request: {0} value is '{1}' for {2}", format=Format.MESSAGE_FORMAT)
   String unableToExtractParameter(String paramSignature, String strVal, AccessibleObject target);

   @Message(id = BASE + 875, value = "Unable to find a constructor that takes a String param or a valueOf() or fromString() method for {0} on {1} for basetype: {2}", format=Format.MESSAGE_FORMAT)
   String unableToFindConstructor(String paramSignature, AccessibleObject target, String className);

   @Message(id = BASE + 880, value = "Unable to find contextual data of type: %s")
   String unableToFindContextualData(String className);

   @Message(id = BASE + 885, value = "Unable to find InjectorFactory implementation.")
   String unableToFindInjectorFactory();

   @Message(id = BASE + 890, value = "Unable to find JAX-RS resource associated with path: %s.")
   String unableToFindJaxRsResource(String path);

   @Message(id = BASE + 895, value = "Unable to find a public constructor for class %s")
   String unableToFindPublicConstructorForClass(String className);

   @Message(id = BASE + 900, value = "Unable to find a public constructor for provider class %s")
   String unableToFindPublicConstructorForProvider(String className);

   @Message(id = BASE + 905, value = "Unable to find type arguments of %s")
   String unableToFindTypeArguments(Class<?> clazz);

   @Message(id = BASE + 910, value = "Unable to instantiate ClientExceptionMapper")
   String unableToInstantiateClientExceptionMapper();

   @Message(id = BASE + 915, value = "Unable to instantiate context object %s")
   String unableToInstantiateContextObject(String key);

   @Message(id = BASE + 920, value = "Unable to instantiate ContextResolver")
   String unableToInstantiateContextResolver();

   @Message(id = BASE + 925, value = "Unable to instantiate ExceptionMapper")
   String unableToInstantiateExceptionMapper();

   @Message(id = BASE + 930, value = "Unable to instantiate @Form class. No no-arg constructor.")
   String unableToInstantiateForm();

   @Message(id = BASE + 935, value = "Unable to instantiate InjectorFactory implementation.")
   String unableToInstantiateInjectorFactory();

   @Message(id = BASE + 940, value = "Unable to instantiate MessageBodyReader")
   String unableToInstantiateMessageBodyReader();

   @Message(id = BASE + 945, value = "Unable to instantiate MessageBodyWriter")
   String unableToInstantiateMessageBodyWriter();

   @Message(id = BASE + 950, value = "Unable to parse the date %s")
   String unableToParseDate(String dateValue);

   @Message(id = BASE + 955, value = "Unable to parse Link header.  No end to link: %s")
   String unableToParseLinkHeaderNoEndToLink(String value);

   @Message(id = BASE + 960, value = "Unable to parse Link header.  No end to parameter: %s")
   String unableToParseLinkHeaderNoEndToParameter(String value);

   @Message(id = BASE + 965, value = "Unable to parse Link header. Too many links in declaration: %s")
   String unableToParseLinkHeaderTooManyLinks(String value);

   @Message(id = BASE + 970, value = "Unable to resolve type variable")
   String unableToResolveTypeVariable();

   @Message(id = BASE + 975, value = "Unable to unmarshall response for %s")
   String unableToUnmarshalResponse(String attributeExceptionsTo);

   @Message(id = BASE + 977, value = "Unexpected Number subclass: %s")
   String unexpectedNumberSubclass(String classname);

   @Message(id = BASE + 980, value = "Unknown interceptor precedence: %s")
   String unknownInterceptorPrecedence(String precedence);

   @Message(id = BASE + 985, value = "Unknown media type for response entity")
   String unknownMediaTypeResponseEntity();

   @Message(id = BASE + 990, value = "Unknown @PathParam: {0} for path: {1}", format=Format.MESSAGE_FORMAT)
   String unknownPathParam(String paramName, String path);

   @Message(id = BASE + 995, value = "Unknown state.  You have a Listener messing up what resteasy expects")
   String unknownStateListener();

   @Message(id = BASE + 1000, value = "Unsupported collectionType: %s")
   String unsupportedCollectionType(Class<?> clazz);

   @Message(id = BASE + 1005, value = "Unsupported parameter: %s")
   String unsupportedParameter(String parameter);

   @Message(id = BASE + 1010, value = "URI was null")
   String uriNull();

   @Message(id = BASE + 1015, value = "uri param was null")
   String uriParamNull();

   @Message(id = BASE + 1020, value = "uriTemplate parameter is null")
   String uriTemplateParameterNull();

   @Message(id = BASE + 1025, value = "URI value is null")
   String uriValueNull();

   @Message(id = BASE + 1030, value = "User is not registered: %s")
   String userIsNotRegistered(String user);

   @Message(id = BASE + 1035, value = "A value was null")
   String valueNull();

   @Message(id = BASE + 1040, value = "value param is null")
   String valueParamIsNull();

   @Message(id = BASE + 1045, value = "value param was null")
   String valueParamWasNull();

   @Message(id = BASE + 1050, value = "values param is null")
   String valuesParamIsNull();

   @Message(id = BASE + 1055, value = "values param was null")
   String valuesParamWasNull();

   @Message(id = BASE + 1060, value = "values parameter is null")
   String valuesParameterNull();

   @Message(id = BASE + 1065, value = "Variant list must not be zero")
   String variantListMustNotBeZero();

   @Message(id = BASE + 1070, value = "Wrong password for: %s")
   String wrongPassword(String user);

   @Message(id = BASE + 1080, value = "WebTarget is not set for creating SseEventSource")
   String webTargetIsNotSetForEventSource();
   @Message(id = BASE + 1081, value = "EventSource is not ready to open")
   String eventSourceIsNotReadyForOpen();
   @Message(id = BASE + 1082, value = "No suitable message body writer for class : %s")
   String notFoundMBW(String className);
   @Message(id = BASE + 1083, value = "Sever sent event feature requries HttpServlet30Dispatcher")
   String asyncServletIsRequired();
   @Message(id = BASE + 1084, value = "Failed to read SseEvent")
   String readEventException();
   @Message(id = BASE + 1085, value = "%s is not set for OutboundSseEvent builder")
   String nullValueSetToCreateOutboundSseEvent(String field);
   @Message(id = BASE + 1086, value = "Failed to write data to InBoundSseEvent")
   String failedToWriteDataToInboudEvent();
   @Message(id = BASE + 1087, value = "No suitable message body reader for class : %s")
   String notFoundMBR(String className);
   @Message(id = BASE + 1088, value = "Failed to read data from InboundSseEvent")
   String failedToReadData();
   @Message(id = BASE + 1089, value = "Failed to create SseEventOutput")
   String failedToCreateSseEventOutput();

   @Message(id = BASE + 1090, value = "Unable to instantiate AsyncResponseProvider")
   String unableToInstantiateAsyncResponseProvider();
   @Message(id = BASE + 1091, value = "Unable to instantiate AsyncStreamProvider")
   String unableToInstantiateAsyncStreamProvider();
   @Message(id = BASE + 1092, value = "SseEventSink is closed")
   String sseEventSinkIsClosed();

   @Message(id = BASE + 1093, value = "SseBroadcaster is closed")
   String sseBroadcasterIsClosed();

   @Message(id = BASE + 1094, value = "Unable to instantiate ContextInjector")
   String unableToInstantiateContextInjector();

   @Message(id = BASE + 1095, value = "Unable to instantiate AsyncClientResponseProvider")
   String unableToInstantiateAsyncClientResponseProvider();
   @Message(id = BASE + 1096, value = "Unable to instantiate AsyncClientStreamProvider")
   String unableToInstantiateAsyncClientStreamProvider();

   @Message(id = BASE + 1097, value = "Registering a context resolver doesn't support lambdas")
   String registeringContextResolverAsLambda();
}
