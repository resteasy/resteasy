package org.jboss.resteasy.i18n;

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
   
   @Message(id = 1000, value = "SelfExpandingBufferredInputStream is always marked at index 0.")
   String alwaysMarkedAtIndex0();

   @Message(id = 1005, value = "Ambiguous inherited JAX-RS annotations applied to method: %s")
   String ambiguousInheritedAnnotations(Method method);
   
   @Message(id = 1010, value = "The implementation of javax.ws.rs.core.Application must be specified.")
   String applicationMustBeSpecified();
   
   @Message(id = 1015, value = "Bad arguments passed to %s")
   String badArguments(String methodName);
   
   @Message(id = 1020, value = "Bad Base64 input character decimal {0} in array position {1}", format=Format.MESSAGE_FORMAT)
   String badBase64Character(int c, int pos);

   @Message(id = 1025, value = "Base64 input not properly padded.")
   String base64InputNotProperlyPadded();
   
   @Message(id = 1030, value = "Base64-encoded string must have at least four characters, but length specified was %s")
   String base64StringMustHaveFourCharacters(int len);
   
   @Message(id = 1035, value = "You have not set a base URI for the client proxy")
   String baseURINotSetForClientProxy();

   @Message(id = 1040, value = "CacheControl max-age header does not have a value: %s.")
   String cacheControlMaxAgeHeader(String value);

   @Message(id = 1045, value = "CacheControl s-maxage header does not have a value: %s.")
   String cacheControlSMaxAgeHeader(String value);
   
   @Message(id = 1050, value = "Cache-Control value is null")
   String cacheControlValueNull();
   
   @Message(id = 1055, value = "Cannot consume content type")
   String cannotConsumeContentType();
   
   @Message(id = 1060, value = "Cannot decode null source array.")
   String cannotDecodeNullSourceArray();
   
   @Message(id = 1065, value = "Cannot have length offset: %s")
   String cannotHaveLengthOffset(int len);
   
   @Message(id = 1070, value = "Cannot have negative offset: %s")
   String cannotHaveNegativeOffset(int off);

   @Message(id = 1075, value = "Cannot have offset of {0} and length of {1} with array of length {2}", format=Format.MESSAGE_FORMAT)
   String cannotHaveOffset(int off, int len, int srcLen);
   
   @Message(id = 1080, value = "You cannot have 2 locators for same path: %s")
   String cannotHaveTwoLocators(String path);
   
   @Message(id = 1085, value = "You cannot inject into a form outside the scope of an HTTP request")
   String cannotInjectIntoForm();
   
   @Message(id = 1090, value = "You cannot send both form parameters and an entity body")
   String cannotSendFormParametersAndEntity();

   @Message(id = 1095, value = "Cannot serialize a null array.")
   String cannotSerializeNullArray();
   
   @Message(id = 1100, value = "Cannot serialize a null object.")
   String cannotSerializeNullObject();
   
   @Message(id = 1105, value = "You can only set one of LinkHeaderParam.rel() and LinkHeaderParam.title() for on {0}.{1}", format=Format.MESSAGE_FORMAT)
   String canOnlySetLinkHeaderRelOrTitle(String className, String methodName);

   @Message(id = 1110, value = "Class is not a root resource.  It, or one of its interfaces must be annotated with @Path: %s implements: }")
   String classIsNotRootResource(String className);
   
   @Message(id = 1115, value = "Class must be annotated with @Path to invoke path(Class")
   String classMustBeAnnotatedWithPath();
   
   @Message(id = 1120, value = "ClientRequest doesn't implement Clonable.  Notify the RESTEasy staff right away.")
   String clientRequestDoesntSupportClonable();
   
   @Message(id = 1125, value = "Unable to find a MessageBodyReader of content-type {0} and type {1}", format=Format.MESSAGE_FORMAT)
   String clientResponseFailureMediaType(MediaType mediaType, Type type);
   
   @Message(id = 1130, value = "Error status {0} {1} returned", format=Format.MESSAGE_FORMAT)
   String clientResponseFailureStatus(int status, Status responseStatus);
   
   @Message(id = 1135, value = "Constructor arg paramMapping is invalid")
   String constructorMappingInvalid();

   @Message(id = 1140, value = "Control character in cookie value, consider BASE64 encoding your value")
   String controlCharacterInCookieValue();
   
   @Message(id = 1145, value = "Cookie header value was null")
   String cookieHeaderValueNull();

   @Message(id = 1150, value = "Could not create a default entity type factory of type %s")
   String couldNotCreateEntityFactory(String className);
   
   @Message(id = 1155, value = "Could not create a URI for {0} in {1}.{2}", format=Format.MESSAGE_FORMAT)
   String couldNotCreateURI(String uri, String className, String methodName);

   @Message(id = 1160, value = "Could not find class %s provided to JNDI Component Resource")
   String couldNotFindClassJndi(String className);

   @Message(id = 1165, value = "URITemplateAnnotationResolver could not find a getter for param %s")
   String couldNotFindGetterForParam(String param);
   
   @Message(id = 1170, value = "Could not find message body reader for type: {0} of content type: {1}", format=Format.MESSAGE_FORMAT)
   String couldNotFindMessageBodyReader(Type type, MediaType mediaType);
   
   @Message(id = 1175, value = "Could not find a method for: %s")
   String couldNotFindMethod(Method method);
   
   @Message(id = 1180, value = "Could not find resource for full path: %s")
   String couldNotFindResourceForFullPath(URI uri);
   
   @Message(id = 1185, value = "Could not find resource for relative : {0} of full path: {1}", format=Format.MESSAGE_FORMAT)
   String couldNotFindResourceForRelativePath(String path, URI uri);
   
   @Message(id = 1190, value = "could not find writer for content-type {0} type: {1}", format=Format.MESSAGE_FORMAT)
   String couldNotFindWriterForContentType(MediaType mediaType, String className);

   @Message(id = 1195, value = "URITemplateAnnotationResolver could not get a value for %s")
   String couldNotGetAValue(String param);

   @Message(id = 1200, value = "URITemplateAnnotationResolver could not introspect class %s")
   String couldNotIntrospectClass(String className);

   @Message(id = 1205, value = "Could not process method %s")
   String couldNotProcessMethod(Method method);
   
   @Message(id = 1210, value = "Could not read type {0} for media type {1}", format=Format.MESSAGE_FORMAT)
   String couldNotReadType(Type type, MediaType mediaType);

   @Message(id = 1215, value = "Date instances are not supported by this class.")
   String dateInstancesNotSupported();
   
   @Message(id = 1220, value = "date is null")
   String dateNull();
   
   @Message(id = 1225, value = "Data to encode was null.")
   String dataToEncodeNull();

   @Message(id = 1230, value = "dateValue is null")
   String dateValueNull();
   
   @Message(id = 1235, value = "Destination array with length {0} cannot have offset of {1} and still store three bytes.", format=Format.MESSAGE_FORMAT)
   String destinationArrayCannotStoreThreeBytes(int len, int off);
   
   @Message(id = 1240, value = "Destination array was null.")
   String destinationArrayNull();

   @Message(id = 1245, value = "Empty field in: %s.")
   String emptyFieldInHeader(String header);
   
   @Message(id = 1250, value = "The entity was already read, and it was of type %s")
   String entityAlreadyRead(Class<?> clazz);
      
   @Message(id = 1255, value = "The object you supplied to registerInterceptor is not of an understood type")
   String entityNotOfUnderstoodType();

   @Message(id = 1260, value = "value of EntityTag is null")
   String entityTagValueNull();

   @Message(id = 1265, value = "Error in Base64 code reading stream.")
   String errorInBase64Stream();
   
   @Message(id = 1270, value = "You have exceeded your maximum forwards ResteasyProviderFactory allows.  Last good uri: %s")
   String excededMaximumForwards(String uri);
   
   @Message(id = 1275, value = "Failed processing arguments of %s")
   String failedProcessingArguments(String constructor);
   
   @Message(id = 1280, value = "Failed to construct %s")
   String failedToConstruct(String constructor);

   @Message(id = 1285, value = "Failed to create URI: %s")
   String failedToCreateUri(String buf);
   
   @Message(id = 1290, value = "Failed to parse cookie string '%s'")
   String failedToParseCookie(String value);
   
   @Message(id = 1295, value = "Failure parsing MediaType string: %s")
   String failureParsingMediaType(String type);

   @Message(id = 1300, value = "File is too big for this convenience method (%s bytes).")
   String fileTooBig(long len);
   
   @Message(id = 1305, value = "Garbage after quoted string: %s")
   String garbageAfterQuotedString(String header);
   
   @Message(id = 1310, value = "A GET request cannot have a body.")
   String getRequestCannotHaveBody();

   @Message(id = 1315, value = "%s has no String constructor")
   String hasNoStringConstructor(String className);
   
   @Message(id = 1320, value = "Illegal hexadecimal character {0} at index {1}", format=Format.MESSAGE_FORMAT)
   String illegalHexadecimalCharacter(char ch, int index);
   
   @Message(id = 1325, value = "It is illegal to inject a @CookieParam into a singleton")
   String illegalToInjectCookieParam();

   @Message(id = 1330, value = "It is illegal to inject a @FormParam into a singleton")
   String illegalToInjectFormParam();
   
   @Message(id = 1335, value = "It is illegal to inject a @HeaderParam into a singleton")
   String illegalToInjectHeaderParam();
   
   @Message(id = 1340, value = "It is illegal to inject a @MatrixParam into a singleton")
   String illegalToInjectMatrixParam();
   
   @Message(id = 1345, value = "Illegal to inject a message body into a singleton into %s")
   String illegalToInjectMessageBody(AccessibleObject target);
   
   @Message(id = 1350, value = "Illegal to inject a non-interface type into a singleton")
   String illegalToInjectNonInterfaceType();
   
   @Message(id = 1355, value = "It is illegal to inject a @PathParam into a singleton")
   String illegalToInjectPathParam();
   
   @Message(id = 1360, value = "It is illegal to inject a @QueryParam into a singleton")
   String illegalToInjectQueryParam();

   @Message(id = 1365, value = "Illegal uri template: %s")
   String illegalUriTemplate(String template);

   @Message(id = 1370, value = "Improperly padded Base64 input.")
   String improperlyPaddedBase64Input();
   
   @Message(id = 1375, value = "Incorrect type parameter. ExceptionMapper requires a subclass of java.lang.Throwable as its type parameter.")
   String incorrectTypeParameter();

   @Message(id = 1380, value = "Input stream was empty, there is no entity")
   String inputStreamEmpty();
   
   @Message(id = 1385, value = "Input string was null.")
   String inputStringNull();
   
   @Message(id = 1390, value = "Interceptor class must be annotated with @ServerInterceptor and/or @ClientInterceptor")
   String interceptorClassMustBeAnnotated();

   @Message(id = 1395, value = "Interceptor class %s must be annotated with @ServerInterceptor and/or @ClientInterceptor")
   String interceptorClassMustBeAnnotatedWithClass(Class<?> clazz);

   @Message(id = 1400, value = "Invalid character in Base64 data.")
   String invalidCharacterInBase64Data();
   
   @Message(id = 1405, value = "Invalid escape character in cookie value.")
   String invalidEscapeCharacterInCookieValue();

   @Message(id = 1410, value = "invalid host")
   String invalidHost();
   
   @Message(id = 1415, value = "Invalid port value")
   String invalidPort();
   
   @Message(id = 1420, value = "%s is not initial request.  Its suspended and retried.  Aborting.")
   String isNotInitialRequest(String path);

   @Message(id = 1425, value = "JNDI Component Resource variable is not set correctly: jndi;class;true|false comma delimited")
   String jndiComponentResourceNotSetCorrectly();
   
   @Message(id = 1430, value = "The %s config in web.xml could not be parsed, accepted values are true,false or 1,0")
   String keyCouldNotBeParsed(String key);
   
   @Message(id = 1435, value = "Locale value is null")
   String localeValueNull();
   
   @Message(id = 1440, value = "Malformed media type: %s")
   String malformedMediaType(String header);
   
   @Message(id = 1445, value = "Malformed parameter: %s")
   String malformedParameter(String parameter);
   
   @Message(id = 1450, value = "Malformed parameters: %s.")
   String malformedParameters(String header);
   
   @Message(id = 1455, value = "Malformed quality value.")
   String malformedQualityValue();
   
   @Message(id = 1460, value = "MarshalledEntity must have type information.")
   String marshalledEntityMustHaveTypeInfo();

   @Message(id = 1465, value = "MediaType q value cannot be greater than 1.0: %s")
   String mediaTypeQGreaterThan1(String mediaType);

   @Message(id = 1470, value = "MediaType q parameter must be a float: %s")
   String mediaTypeQMustBeFloat(MediaType mediaType);

   @Message(id = 1475, value = "MediaType q parameter must be a float: %s")
   String mediaTypeQWeightedLanguageMustBeFloat(WeightedLanguage lang);
   
   @Message(id = 1480, value = "MediaType value is null")
   String mediaTypeValueNull();

   @Message(id = 1485, value = "method is not annotated with @Path")
   String methodNotAnnotatedWithPath();
   
   @Message(id = 1490, value = "method was null")
   String methodNull();

   @Message(id = 1495, value = "Missing type parameter.")
   String missingTypeParameter();
   
   @Message(id = 1500, value = "You must define a @Consumes type on your client method or interface, or supply a default")
   String mustDefineConsumes();
   
   @Message(id = 1505, value = "You must set either LinkHeaderParam.rel() or LinkHeaderParam.title() for on {0}.{1}", format=Format.MESSAGE_FORMAT)
   String mustSetLinkHeaderRelOrTitle(String className, String methodName);
   
   @Message(id = 1510, value = "You must use at least one, but no more than one http method annotation on: %s")
   String mustUseOneHttpMethod(String methodName);

   @Message(id = 1515, value = "name parameter is null")
   String nameParameterNull();
   
   @Message(id = 1520, value = "NewCookie value is null")
   String newCookieValueNull();

   @Message(id = 1525, value = "%s is no longer a supported context param.  See documentation for more details")
   String noLongerASupportedContextParam(String paramName);
   
   @Message(id = 1530, value = "No match for accept header")
   String noMatchForAcceptHeader();

   @Message(id = 1535, value = "No output stream allowed")
   String noOutputStreamAllowed();
   
   @Message(id = 1540, value = "No path match in subresource for: %s.")
   String noPathMatchInSubresource(URI uri);

   @Message(id = 1545, value = "No resource method found for %s, return 405 with Allow header")
   String noResourceMethodFoundForHttpMethod(String httpMethod);
   
   @Message(id = 1550, value = "No resource method found for options, return OK with Allow header")
   String noResourceMethodFoundForOptions();
   
   @Message(id = 1555, value = "No type information to extract entity with, use other getEntity() methods")
   String noTypeInformationForEntity();
   
   @Message(id = 1560, value = "Not allowed to reflect on method: %s")
   String notAllowedToReflectOnMethod(String methodName);

   @Message(id = 1565, value = "You did not supply enough values to fill path parameters")
   String notEnoughPathParameters();

   @Message(id = 1570, value = "NOT SUPPORTED")
   String notSupported();
   
   @Message(id = 1575, value = "%s is not a valid injectable type for @Suspend")
   String notValidInjectableType(String typeName);
   
   @Message(id = 1580, value = "Null subresource for path: %s.")
   String nullResource(URI uri);
   
   @Message(id = 1585, value = "Number of matched segments greater than actual")
   String numberOfMatchedSegments();

   @Message(id = 1590, value = "Odd number of characters.")
   String oddNumberOfCharacters();
   
   @Message(id = 1595, value = "A passed in value was null")
   String passedInValueNull();
   
   @Message(id = 1600, value = "path was null")
   String pathNull();

   @Message(id = 1605, value = "path param %s has not been provided by the parameter map")
   String pathParameterNotProvided(String param);

   @Message(id = 1610, value = "pattern is null")
   String patternNull();

   @Message(id = 1615, value = "Accept-Language q value cannot be greater than 1.0 %s")
   String qValueCannotBeGreaterThan1(String lang);

   @Message(id = 1620, value = "Quoted string is not closed: %s")
   String quotedStringIsNotClosed(String header);
   
   @Message(id = 1625, value = "Removing a header is illegal for an HttpServletResponse")
   String removingHeaderIllegal();
   
   @Message(id = 1630, value = "Request media type is not application/x-www-form-urlencoded")
   String requestMediaTypeNotUrlencoded();
   
   @Message(id = 1635, value = "Request was committed couldn't handle exception")
   String requestWasCommitted();

   @Message(id = 1640, value = "resource was null")
   String resourceNull();
   
   @Message(id = 1645, value = "schemeSpecificPart was null")
   String schemeSpecificPartNull();

   @Message(id = 1650, value = "A segment is null")
   String segmentNull();
   
   @Message(id = 1655, value = "segments parameter was null")
   String segmentsParameterNull();
   
   @Message(id = 1660, value = "Should be unreachable")
   String shouldBeUnreachable();

   @Message(id = 1665, value = "Source array with length {0} cannot have offset of {1} and process {2} bytes.", format=Format.MESSAGE_FORMAT)
   String sourceArrayCannotProcessBytes(int srcLen, int off, int len);
   
   @Message(id = 1670, value = "Source array with length {0} cannot have offset of {1} and still process four bytes.", format=Format.MESSAGE_FORMAT)
   String sourceArrayCannotProcessFourBytes(int srcLen, int off);
   
   @Message(id = 1675, value = "Source array was null.")
   String sourceArrayNull();

   @Message(id = 1680, value = "Stream wrapped by Signature, cannot reset the stream without destroying signature")
   String streamWrappedBySignature();
   
   @Message(id = 1685, value = "Subresource for target class has no jax-rs annotations.: %s")
   String subresourceHasNoJaxRsAnnotations(String className);
   
   @Message(id = 1690, value = "Tailing garbage: %s")
   String tailingGarbage(String header);

   @Message(id = 1695, value = "NULL value for template parameter: %s")
   String templateParameterNull(String param);
   
   @Message(id = 1700, value = "there are two method named %s")
   String twoMethodsSameName(String method);
   
   @Message(id = 1705, value = "Unable to decode query string")
   String unableToDecodeQueryString();

   @Message(id = 1710, value = "Unable to determine base class from Type")
   String unableToDetermineBaseClass();

   @Message(id = 1715, value = "Unable to determine value of type parameter %s")
   String unableToDetermineTypeParameter(TypeVariable<?> typeVariable);
   
   @Message(id = 1720, value = "Unable to extract parameter from http request: {0} value is '{1}' for {2}", format=Format.MESSAGE_FORMAT)
   String unableToExtractParameter(String paramSignature, String strVal, AccessibleObject target);
   
   @Message(id = 1725, value = "Unable to find a constructor that takes a String param or a valueOf() or fromString() method for {0} on {1} for basetype: {2}", format=Format.MESSAGE_FORMAT)
   String unableToFindConstructor(String paramSignature, AccessibleObject target, String className);
   
   @Message(id = 1730, value = "Unable to find contextual data of type: %s")
   String unableToFindContextualData(String className);

   @Message(id = 1735, value = "Unable to find InjectorFactory implementation")
   String unableToFindInjectorFactory();
   
   @Message(id = 1740, value = "Unable to find JAX-RS resource associated with path: %s.")
   String unableToFindJaxRsResource(String path);

   @Message(id = 1745, value = "Unable to find a public constructor for class %s")
   String unableToFindPublicConstructorForClass(String className);
   
   @Message(id = 1750, value = "Unable to find a public constructor for interceptor class %s")
   String unableToFindPublicConstructorForInterceptor(String className);
   
   @Message(id = 1755, value = "Unable to find a public constructor for provider class %s")
   String unableToFindPublicConstructorForProvider(String className);

   @Message(id = 1760, value = "Unable to find type arguments of %s")
   String unableToFindTypeArguments(Class<?> clazz);
   
   @Message(id = 1765, value = "Unable to instantiate @Form class. No no-arg constructor.")
   String unableToInstantiateForm();

   @Message(id = 1770, value = "Unable to instantiate ClientExceptionMapper")
   String unableToInstantiateClientExceptionMapper();
   
   @Message(id = 1775, value = "Unable to instantiate context object %s")
   String unableToInstantiateContextObject(String key);

   @Message(id = 1780, value = "Unable to instantiate ContextResolver")
   String unableToInstantiateContextResolver();

   @Message(id = 1785, value = "Unable to instantiate ExceptionMapper")
   String unableToInstantiateExceptionMapper();

   @Message(id = 1790, value = "Unable to instantiate InjectorFactory implementation.")
   String unableToInstantiateInjectorFactory();

   @Message(id = 1795, value = "Unable to instantiate MessageBodyReader")
   String unableToInstantiateMessageBodyReader();
   
   @Message(id = 1800, value = "Unable to instantiate MessageBodyWriter")
   String unableToInstantiateMessageBodyWriter();

   @Message(id = 1805, value = "Unable to parse the date %s")
   String unableToParseDate(String dateValue);
   
   @Message(id = 1810, value = "Unable to parse Link header.  No end to link: %s")
   String unableToParseLinkHeaderNoEndToLink(String value);

   @Message(id = 1815, value = "Unable to parse Link header.  No end to parameter: %s")
   String unableToParseLinkHeaderNoEndToParameter(String value);
   
   @Message(id = 1820, value = "Unable to parse Link header. Too many links in declaration: %s")
   String unableToParseLinkHeaderTooManyLinks(String value);

   @Message(id = 1825, value = "Unable to resolve type variable")
   String unableToResolveTypeVariable();
   
   @Message(id = 1830, value = "Unable to scan WEB-INF for JAX-RS annotations, you must manually register your classes/resources")
   String unableToScanWebInf();
   
   @Message(id = 1835, value = "Unable to unmarshall response for %s")
   String unableToUnmarshalResponse(String attributeExceptionsTo);

   @Message(id = 1840, value = "Application.getClasses() returned unknown class type: %s")
   String unknownClassTypeGetClasses(String className);

   @Message(id = 1845, value = "Application.getSingletons() returned unknown class type: %s")
   String unknownClassTypeGetSingletons(String className);
   
   @Message(id = 1850, value = "Unknown interceptor precedence: %s")
   String unknownInterceptorPrecedence(String precedence);
   
   @Message(id = 1855, value = "Unknown @PathParam: {0} for path: {1}", format=Format.MESSAGE_FORMAT)
   String unknownPathParam(String paramName, String path);

   @Message(id = 1860, value = "Unknown state.  You have a Listener messing up what resteasy expects")
   String unknownStateListener();
   
   @Message(id = 1865, value = "Unsupported collectionType: %s")
   String unsupportedCollectionType(Class<?> clazz);
   
   @Message(id = 1870, value = "Unsupported parameter: %s")
   String unsupportedParameter(String parameter);

   @Message(id = 1875, value = "URI was null")
   String uriNull();
   
   @Message(id = 1880, value = "URI value is null")
   String uriValueNull();

   @Message(id = 1885, value = "User is not registered: %s")
   String userIsNotRegistered(String user);
   
   @Message(id = 1890, value = "A value was null")
   String valueNull();
   
   @Message(id = 1895, value = "values parameter is null")
   String valuesParameterNull();
   
   @Message(id = 1900, value = "Variant list must not be zero")
   String variantListMustNotBeZero();
   
   @Message(id = 1905, value = "Wrong password for: %s")
   String wrongPassword(String user);
}
