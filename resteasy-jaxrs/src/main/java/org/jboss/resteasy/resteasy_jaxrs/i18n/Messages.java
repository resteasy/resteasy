package org.jboss.resteasy.resteasy_jaxrs.i18n;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.resteasy.util.WeightedLanguage;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Sep 20, 2014
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 1000;
   
   @Message(id = BASE + 00, value = "SelfExpandingBufferredInputStream is always marked at index 0.")
   String alwaysMarkedAtIndex0();

   @Message(id = BASE + 05, value = "Ambiguous inherited JAX-RS annotations applied to method: %s")
   String ambiguousInheritedAnnotations(Method method);
   
   @Message(id = BASE + 10, value = "The implementation of javax.ws.rs.core.Application must be specified.")
   String applicationMustBeSpecified();
   
   @Message(id = BASE + 15, value = "Bad arguments passed to %s")
   String badArguments(String methodName);
   
   @Message(id = BASE + 20, value = "Bad Base64 input character decimal {0} in array position {1}", format=Format.MESSAGE_FORMAT)
   String badBase64Character(int c, int pos);

   @Message(id = BASE + 25, value = "Base64 input not properly padded.")
   String base64InputNotProperlyPadded();
   
   @Message(id = BASE + 30, value = "Base64-encoded string must have at least four characters, but length specified was %s")
   String base64StringMustHaveFourCharacters(int len);
   
   @Message(id = BASE + 35, value = "You have not set a base URI for the client proxy")
   String baseURINotSetForClientProxy();

   @Message(id = BASE + 40, value = "CacheControl max-age header does not have a value: %s.")
   String cacheControlMaxAgeHeader(String value);

   @Message(id = BASE + 45, value = "CacheControl s-maxage header does not have a value: %s.")
   String cacheControlSMaxAgeHeader(String value);
   
   @Message(id = BASE + 50, value = "Cache-Control value is null")
   String cacheControlValueNull();
   
   @Message(id = BASE + 55, value = "Cannot consume content type")
   String cannotConsumeContentType();
   
   @Message(id = BASE + 60, value = "Cannot decode null source array.")
   String cannotDecodeNullSourceArray();
   
   @Message(id = BASE + 65, value = "Cannot have length offset: %s")
   String cannotHaveLengthOffset(int len);
   
   @Message(id = BASE + 70, value = "Cannot have negative offset: %s")
   String cannotHaveNegativeOffset(int off);

   @Message(id = BASE + 75, value = "Cannot have offset of {0} and length of {1} with array of length {2}", format=Format.MESSAGE_FORMAT)
   String cannotHaveOffset(int off, int len, int srcLen);
   
   @Message(id = BASE + 80, value = "You cannot have 2 locators for same path: %s")
   String cannotHaveTwoLocators(String path);
   
   @Message(id = BASE + 85, value = "You cannot inject into a form outside the scope of an HTTP request")
   String cannotInjectIntoForm();
   
   @Message(id = BASE + 90, value = "You cannot send both form parameters and an entity body")
   String cannotSendFormParametersAndEntity();

   @Message(id = BASE + 95, value = "Cannot serialize a null array.")
   String cannotSerializeNullArray();
   
   @Message(id = BASE + 100, value = "Cannot serialize a null object.")
   String cannotSerializeNullObject();
   
   @Message(id = BASE + 105, value = "You can only set one of LinkHeaderParam.rel() and LinkHeaderParam.title() for on {0}.{1}", format=Format.MESSAGE_FORMAT)
   String canOnlySetLinkHeaderRelOrTitle(String className, String methodName);

   @Message(id = BASE + 110, value = "Class is not a root resource.  It, or one of its interfaces must be annotated with @Path: %s implements: }")
   String classIsNotRootResource(String className);
   
   @Message(id = BASE + 115, value = "Class must be annotated with @Path to invoke path(Class")
   String classMustBeAnnotatedWithPath();
   
   @Message(id = BASE + 120, value = "ClientRequest doesn't implement Clonable.  Notify the RESTEasy staff right away.")
   String clientRequestDoesntSupportClonable();
   
   @Message(id = BASE + 125, value = "Unable to find a MessageBodyReader of content-type {0} and type {1}", format=Format.MESSAGE_FORMAT)
   String clientResponseFailureMediaType(MediaType mediaType, Type type);
   
   @Message(id = BASE + 130, value = "Error status {0} {1} returned", format=Format.MESSAGE_FORMAT)
   String clientResponseFailureStatus(int status, Status responseStatus);
   
   @Message(id = BASE + 135, value = "Constructor arg paramMapping is invalid")
   String constructorMappingInvalid();

   @Message(id = BASE + 140, value = "Control character in cookie value, consider BASE64 encoding your value")
   String controlCharacterInCookieValue();
   
   @Message(id = BASE + 145, value = "Cookie header value was null")
   String cookieHeaderValueNull();

   @Message(id = BASE + 150, value = "Could not create a default entity type factory of type %s")
   String couldNotCreateEntityFactory(String className);
   
   @Message(id = BASE + 155, value = "Could not create a URI for {0} in {1}.{2}", format=Format.MESSAGE_FORMAT)
   String couldNotCreateURI(String uri, String className, String methodName);

   @Message(id = BASE + 160, value = "Could not find class %s provided to JNDI Component Resource")
   String couldNotFindClassJndi(String className);

   @Message(id = BASE + 165, value = "URITemplateAnnotationResolver could not find a getter for param %s")
   String couldNotFindGetterForParam(String param);
   
   @Message(id = BASE + 170, value = "Could not find message body reader for type: {0} of content type: {1}", format=Format.MESSAGE_FORMAT)
   String couldNotFindMessageBodyReader(Type type, MediaType mediaType);
   
   @Message(id = BASE + 175, value = "Could not find a method for: %s")
   String couldNotFindMethod(Method method);
   
   @Message(id = BASE + 180, value = "Could not find resource for full path: %s")
   String couldNotFindResourceForFullPath(URI uri);
   
   @Message(id = BASE + 185, value = "Could not find resource for relative : {0} of full path: {1}", format=Format.MESSAGE_FORMAT)
   String couldNotFindResourceForRelativePath(String path, URI uri);
   
   @Message(id = BASE + 190, value = "could not find writer for content-type {0} type: {1}", format=Format.MESSAGE_FORMAT)
   String couldNotFindWriterForContentType(MediaType mediaType, String className);

   @Message(id = BASE + 195, value = "URITemplateAnnotationResolver could not get a value for %s")
   String couldNotGetAValue(String param);

   @Message(id = BASE + 200, value = "URITemplateAnnotationResolver could not introspect class %s")
   String couldNotIntrospectClass(String className);

   @Message(id = BASE + 205, value = "Could not process method %s")
   String couldNotProcessMethod(Method method);
   
   @Message(id = BASE + 210, value = "Could not read type {0} for media type {1}", format=Format.MESSAGE_FORMAT)
   String couldNotReadType(Type type, MediaType mediaType);

   @Message(id = BASE + 215, value = "Date instances are not supported by this class.")
   String dateInstancesNotSupported();
   
   @Message(id = BASE + 220, value = "date is null")
   String dateNull();
   
   @Message(id = BASE + 225, value = "Data to encode was null.")
   String dataToEncodeNull();

   @Message(id = BASE + 230, value = "dateValue is null")
   String dateValueNull();
   
   @Message(id = BASE + 235, value = "Destination array with length {0} cannot have offset of {1} and still store three bytes.", format=Format.MESSAGE_FORMAT)
   String destinationArrayCannotStoreThreeBytes(int len, int off);
   
   @Message(id = BASE + 240, value = "Destination array was null.")
   String destinationArrayNull();

   @Message(id = BASE + 245, value = "Empty field in: %s.")
   String emptyFieldInHeader(String header);
   
   @Message(id = BASE + 250, value = "The entity was already read, and it was of type %s")
   String entityAlreadyRead(Class<?> clazz);
      
   @Message(id = BASE + 255, value = "The object you supplied to registerInterceptor is not of an understood type")
   String entityNotOfUnderstoodType();

   @Message(id = BASE + 260, value = "value of EntityTag is null")
   String entityTagValueNull();

   @Message(id = BASE + 265, value = "Error in Base64 code reading stream.")
   String errorInBase64Stream();
   
   @Message(id = BASE + 270, value = "You have exceeded your maximum forwards ResteasyProviderFactory allows.  Last good uri: %s")
   String excededMaximumForwards(String uri);
   
   @Message(id = BASE + 275, value = "Failed processing arguments of %s")
   String failedProcessingArguments(String constructor);
   
   @Message(id = BASE + 280, value = "Failed to construct %s")
   String failedToConstruct(String constructor);

   @Message(id = BASE + 285, value = "Failed to create URI: %s")
   String failedToCreateUri(String buf);
   
   @Message(id = BASE + 290, value = "Failed to parse cookie string '%s'")
   String failedToParseCookie(String value);
   
   @Message(id = BASE + 295, value = "Failure parsing MediaType string: %s")
   String failureParsingMediaType(String type);

   @Message(id = BASE + 300, value = "File is too big for this convenience method (%s bytes).")
   String fileTooBig(long len);
   
   @Message(id = BASE + 305, value = "Garbage after quoted string: %s")
   String garbageAfterQuotedString(String header);
   
   @Message(id = BASE + 310, value = "A GET request cannot have a body.")
   String getRequestCannotHaveBody();

   @Message(id = BASE + 315, value = "%s has no String constructor")
   String hasNoStringConstructor(String className);
   
   @Message(id = BASE + 320, value = "Illegal hexadecimal character {0} at index {1}", format=Format.MESSAGE_FORMAT)
   String illegalHexadecimalCharacter(char ch, int index);
   
   @Message(id = BASE + 325, value = "It is illegal to inject a @CookieParam into a singleton")
   String illegalToInjectCookieParam();

   @Message(id = BASE + 330, value = "It is illegal to inject a @FormParam into a singleton")
   String illegalToInjectFormParam();
   
   @Message(id = BASE + 335, value = "It is illegal to inject a @HeaderParam into a singleton")
   String illegalToInjectHeaderParam();
   
   @Message(id = BASE + 340, value = "It is illegal to inject a @MatrixParam into a singleton")
   String illegalToInjectMatrixParam();
   
   @Message(id = BASE + 345, value = "Illegal to inject a message body into a singleton into %s")
   String illegalToInjectMessageBody(AccessibleObject target);
   
   @Message(id = BASE + 350, value = "Illegal to inject a non-interface type into a singleton")
   String illegalToInjectNonInterfaceType();
   
   @Message(id = BASE + 355, value = "It is illegal to inject a @PathParam into a singleton")
   String illegalToInjectPathParam();
   
   @Message(id = BASE + 360, value = "It is illegal to inject a @QueryParam into a singleton")
   String illegalToInjectQueryParam();

   @Message(id = BASE + 365, value = "Illegal uri template: %s")
   String illegalUriTemplate(String template);

   @Message(id = BASE + 370, value = "Improperly padded Base64 input.")
   String improperlyPaddedBase64Input();
   
   @Message(id = BASE + 375, value = "Incorrect type parameter. ExceptionMapper requires a subclass of java.lang.Throwable as its type parameter.")
   String incorrectTypeParameter();

   @Message(id = BASE + 380, value = "Input stream was empty, there is no entity")
   String inputStreamEmpty();
   
   @Message(id = BASE + 385, value = "Input string was null.")
   String inputStringNull();
   
   @Message(id = BASE + 390, value = "Interceptor class must be annotated with @ServerInterceptor and/or @ClientInterceptor")
   String interceptorClassMustBeAnnotated();

   @Message(id = BASE + 395, value = "Interceptor class %s must be annotated with @ServerInterceptor and/or @ClientInterceptor")
   String interceptorClassMustBeAnnotatedWithClass(Class<?> clazz);

   @Message(id = BASE + 400, value = "Invalid character in Base64 data.")
   String invalidCharacterInBase64Data();
   
   @Message(id = BASE + 405, value = "Invalid escape character in cookie value.")
   String invalidEscapeCharacterInCookieValue();

   @Message(id = BASE + 410, value = "invalid host")
   String invalidHost();
   
   @Message(id = BASE + 415, value = "Invalid port value")
   String invalidPort();
   
   @Message(id = BASE + 420, value = "%s is not initial request.  Its suspended and retried.  Aborting.")
   String isNotInitialRequest(String path);

   @Message(id = BASE + 425, value = "JNDI Component Resource variable is not set correctly: jndi;class;true|false comma delimited")
   String jndiComponentResourceNotSetCorrectly();
   
   @Message(id = BASE + 430, value = "The %s config in web.xml could not be parsed, accepted values are true,false or 1,0")
   String keyCouldNotBeParsed(String key);
   
   @Message(id = BASE + 435, value = "Locale value is null")
   String localeValueNull();
   
   @Message(id = BASE + 440, value = "Malformed media type: %s")
   String malformedMediaType(String header);
   
   @Message(id = BASE + 445, value = "Malformed parameter: %s")
   String malformedParameter(String parameter);
   
   @Message(id = BASE + 450, value = "Malformed parameters: %s.")
   String malformedParameters(String header);
   
   @Message(id = BASE + 455, value = "Malformed quality value.")
   String malformedQualityValue();
   
   @Message(id = BASE + 460, value = "MarshalledEntity must have type information.")
   String marshalledEntityMustHaveTypeInfo();

   @Message(id = BASE + 465, value = "MediaType q value cannot be greater than 1.0: %s")
   String mediaTypeQGreaterThan1(String mediaType);

   @Message(id = BASE + 470, value = "MediaType q parameter must be a float: %s")
   String mediaTypeQMustBeFloat(MediaType mediaType);

   @Message(id = BASE + 475, value = "MediaType q parameter must be a float: %s")
   String mediaTypeQWeightedLanguageMustBeFloat(WeightedLanguage lang);
   
   @Message(id = BASE + 480, value = "MediaType value is null")
   String mediaTypeValueNull();

   @Message(id = BASE + 485, value = "method is not annotated with @Path")
   String methodNotAnnotatedWithPath();
   
   @Message(id = BASE + 490, value = "method was null")
   String methodNull();

   @Message(id = BASE + 495, value = "Missing type parameter.")
   String missingTypeParameter();
   
   @Message(id = BASE + 500, value = "You must define a @Consumes type on your client method or interface, or supply a default")
   String mustDefineConsumes();
   
   @Message(id = BASE + 505, value = "You must set either LinkHeaderParam.rel() or LinkHeaderParam.title() for on {0}.{1}", format=Format.MESSAGE_FORMAT)
   String mustSetLinkHeaderRelOrTitle(String className, String methodName);
   
   @Message(id = BASE + 510, value = "You must use at least one, but no more than one http method annotation on: %s")
   String mustUseOneHttpMethod(String methodName);

   @Message(id = BASE + 515, value = "name parameter is null")
   String nameParameterNull();
   
   @Message(id = BASE + 520, value = "NewCookie value is null")
   String newCookieValueNull();

   @Message(id = BASE + 525, value = "%s is no longer a supported context param.  See documentation for more details")
   String noLongerASupportedContextParam(String paramName);
   
   @Message(id = BASE + 530, value = "No match for accept header")
   String noMatchForAcceptHeader();

   @Message(id = BASE + 535, value = "No output stream allowed")
   String noOutputStreamAllowed();
   
   @Message(id = BASE + 540, value = "No path match in subresource for: %s.")
   String noPathMatchInSubresource(URI uri);

   @Message(id = BASE + 545, value = "No resource method found for %s, return 405 with Allow header")
   String noResourceMethodFoundForHttpMethod(String httpMethod);
   
   @Message(id = BASE + 550, value = "No resource method found for options, return OK with Allow header")
   String noResourceMethodFoundForOptions();
   
   @Message(id = BASE + 555, value = "No type information to extract entity with, use other getEntity() methods")
   String noTypeInformationForEntity();
   
   @Message(id = BASE + 560, value = "Not allowed to reflect on method: %s")
   String notAllowedToReflectOnMethod(String methodName);

   @Message(id = BASE + 565, value = "You did not supply enough values to fill path parameters")
   String notEnoughPathParameters();

   @Message(id = BASE + 570, value = "NOT SUPPORTED")
   String notSupported();
   
   @Message(id = BASE + 575, value = "%s is not a valid injectable type for @Suspend")
   String notValidInjectableType(String typeName);
   
   @Message(id = BASE + 580, value = "Null subresource for path: %s.")
   String nullResource(URI uri);
   
   @Message(id = BASE + 585, value = "Number of matched segments greater than actual")
   String numberOfMatchedSegments();

   @Message(id = BASE + 590, value = "Odd number of characters.")
   String oddNumberOfCharacters();
   
   @Message(id = BASE + 595, value = "A passed in value was null")
   String passedInValueNull();
   
   @Message(id = BASE + 600, value = "path was null")
   String pathNull();

   @Message(id = BASE + 605, value = "path param %s has not been provided by the parameter map")
   String pathParameterNotProvided(String param);

   @Message(id = BASE + 610, value = "pattern is null")
   String patternNull();

   @Message(id = BASE + 615, value = "Accept-Language q value cannot be greater than 1.0 %s")
   String qValueCannotBeGreaterThan1(String lang);

   @Message(id = BASE + 620, value = "Quoted string is not closed: %s")
   String quotedStringIsNotClosed(String header);
   
   @Message(id = BASE + 625, value = "Removing a header is illegal for an HttpServletResponse")
   String removingHeaderIllegal();
   
   @Message(id = BASE + 630, value = "Request media type is not application/x-www-form-urlencoded")
   String requestMediaTypeNotUrlencoded();
   
   @Message(id = BASE + 635, value = "Request was committed couldn't handle exception")
   String requestWasCommitted();

   @Message(id = BASE + 640, value = "resource was null")
   String resourceNull();
   
   @Message(id = BASE + 645, value = "schemeSpecificPart was null")
   String schemeSpecificPartNull();

   @Message(id = BASE + 650, value = "A segment is null")
   String segmentNull();
   
   @Message(id = BASE + 655, value = "segments parameter was null")
   String segmentsParameterNull();
   
   @Message(id = BASE + 660, value = "Should be unreachable")
   String shouldBeUnreachable();

   @Message(id = BASE + 665, value = "Source array with length {0} cannot have offset of {1} and process {2} bytes.", format=Format.MESSAGE_FORMAT)
   String sourceArrayCannotProcessBytes(int srcLen, int off, int len);
   
   @Message(id = BASE + 670, value = "Source array with length {0} cannot have offset of {1} and still process four bytes.", format=Format.MESSAGE_FORMAT)
   String sourceArrayCannotProcessFourBytes(int srcLen, int off);
   
   @Message(id = BASE + 675, value = "Source array was null.")
   String sourceArrayNull();

   @Message(id = BASE + 680, value = "Stream wrapped by Signature, cannot reset the stream without destroying signature")
   String streamWrappedBySignature();
   
   @Message(id = BASE + 685, value = "Subresource for target class has no jax-rs annotations.: %s")
   String subresourceHasNoJaxRsAnnotations(String className);
   
   @Message(id = BASE + 690, value = "Tailing garbage: %s")
   String tailingGarbage(String header);

   @Message(id = BASE + 695, value = "NULL value for template parameter: %s")
   String templateParameterNull(String param);
   
   @Message(id = BASE + 700, value = "there are two method named %s")
   String twoMethodsSameName(String method);
   
   @Message(id = BASE + 705, value = "Unable to decode query string")
   String unableToDecodeQueryString();

   @Message(id = BASE + 710, value = "Unable to determine base class from Type")
   String unableToDetermineBaseClass();

   @Message(id = BASE + 715, value = "Unable to determine value of type parameter %s")
   String unableToDetermineTypeParameter(TypeVariable<?> typeVariable);
   
   @Message(id = BASE + 720, value = "Unable to extract parameter from http request: {0} value is '{1}' for {2}", format=Format.MESSAGE_FORMAT)
   String unableToExtractParameter(String paramSignature, String strVal, AccessibleObject target);
   
   @Message(id = BASE + 725, value = "Unable to find a constructor that takes a String param or a valueOf() or fromString() method for {0} on {1} for basetype: {2}", format=Format.MESSAGE_FORMAT)
   String unableToFindConstructor(String paramSignature, AccessibleObject target, String className);
   
   @Message(id = BASE + 730, value = "Unable to find contextual data of type: %s")
   String unableToFindContextualData(String className);

   @Message(id = BASE + 735, value = "Unable to find InjectorFactory implementation")
   String unableToFindInjectorFactory();
   
   @Message(id = BASE + 740, value = "Unable to find JAX-RS resource associated with path: %s.")
   String unableToFindJaxRsResource(String path);

   @Message(id = BASE + 745, value = "Unable to find a public constructor for class %s")
   String unableToFindPublicConstructorForClass(String className);
   
   @Message(id = BASE + 750, value = "Unable to find a public constructor for interceptor class %s")
   String unableToFindPublicConstructorForInterceptor(String className);
   
   @Message(id = BASE + 755, value = "Unable to find a public constructor for provider class %s")
   String unableToFindPublicConstructorForProvider(String className);

   @Message(id = BASE + 760, value = "Unable to find type arguments of %s")
   String unableToFindTypeArguments(Class<?> clazz);
   
   @Message(id = BASE + 765, value = "Unable to instantiate @Form class. No no-arg constructor.")
   String unableToInstantiateForm();

   @Message(id = BASE + 770, value = "Unable to instantiate ClientExceptionMapper")
   String unableToInstantiateClientExceptionMapper();
   
   @Message(id = BASE + 775, value = "Unable to instantiate context object %s")
   String unableToInstantiateContextObject(String key);

   @Message(id = BASE + 780, value = "Unable to instantiate ContextResolver")
   String unableToInstantiateContextResolver();

   @Message(id = BASE + 785, value = "Unable to instantiate ExceptionMapper")
   String unableToInstantiateExceptionMapper();

   @Message(id = BASE + 790, value = "Unable to instantiate InjectorFactory implementation.")
   String unableToInstantiateInjectorFactory();

   @Message(id = BASE + 795, value = "Unable to instantiate MessageBodyReader")
   String unableToInstantiateMessageBodyReader();
   
   @Message(id = BASE + 800, value = "Unable to instantiate MessageBodyWriter")
   String unableToInstantiateMessageBodyWriter();

   @Message(id = BASE + 805, value = "Unable to parse the date %s")
   String unableToParseDate(String dateValue);
   
   @Message(id = BASE + 810, value = "Unable to parse Link header.  No end to link: %s")
   String unableToParseLinkHeaderNoEndToLink(String value);

   @Message(id = BASE + 815, value = "Unable to parse Link header.  No end to parameter: %s")
   String unableToParseLinkHeaderNoEndToParameter(String value);
   
   @Message(id = BASE + 820, value = "Unable to parse Link header. Too many links in declaration: %s")
   String unableToParseLinkHeaderTooManyLinks(String value);

   @Message(id = BASE + 825, value = "Unable to resolve type variable")
   String unableToResolveTypeVariable();
   
   @Message(id = BASE + 830, value = "Unable to scan WEB-INF for JAX-RS annotations, you must manually register your classes/resources")
   String unableToScanWebInf();
   
   @Message(id = BASE + 835, value = "Unable to unmarshall response for %s")
   String unableToUnmarshalResponse(String attributeExceptionsTo);

   @Message(id = BASE + 840, value = "Application.getClasses() returned unknown class type: %s")
   String unknownClassTypeGetClasses(String className);

   @Message(id = BASE + 845, value = "Application.getSingletons() returned unknown class type: %s")
   String unknownClassTypeGetSingletons(String className);
   
   @Message(id = BASE + 850, value = "Unknown interceptor precedence: %s")
   String unknownInterceptorPrecedence(String precedence);
   
   @Message(id = BASE + 855, value = "Unknown @PathParam: {0} for path: {1}", format=Format.MESSAGE_FORMAT)
   String unknownPathParam(String paramName, String path);

   @Message(id = BASE + 860, value = "Unknown state.  You have a Listener messing up what resteasy expects")
   String unknownStateListener();
   
   @Message(id = BASE + 865, value = "Unsupported collectionType: %s")
   String unsupportedCollectionType(Class<?> clazz);
   
   @Message(id = BASE + 870, value = "Unsupported parameter: %s")
   String unsupportedParameter(String parameter);

   @Message(id = BASE + 875, value = "URI was null")
   String uriNull();
   
   @Message(id = BASE + 880, value = "URI value is null")
   String uriValueNull();

   @Message(id = BASE + 885, value = "User is not registered: %s")
   String userIsNotRegistered(String user);
   
   @Message(id = BASE + 890, value = "A value was null")
   String valueNull();
   
   @Message(id = BASE + 895, value = "values parameter is null")
   String valuesParameterNull();
   
   @Message(id = BASE + 900, value = "Variant list must not be zero")
   String variantListMustNotBeZero();
   
   @Message(id = BASE + 905, value = "Wrong password for: %s")
   String wrongPassword(String user);
}
