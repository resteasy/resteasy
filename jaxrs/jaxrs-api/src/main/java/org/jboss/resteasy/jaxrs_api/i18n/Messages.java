package org.jboss.resteasy.jaxrs_api.i18n;

import java.lang.reflect.Type;
import java.net.URL;

import javax.ws.rs.core.Response.Status.Family;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.logging.annotations.MessageBundle;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 22, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 1000;
   
   @Message(id = BASE + 0, value = "Arguments must not be null")
   String argumentsMustNotBeNull();

   @Message(id = BASE + 5, value = "ClassCastException: attempting to cast {0} to {1}", format=Format.MESSAGE_FORMAT)
   String classCastException(URL resource, URL type);
   
   @Message(id = BASE + 10, value = "%s does not specify the type parameter T of GenericType<T>")
   String currentTypeNoTypeParameter(Type currentType);
   
   @Message(id = BASE + 15, value = "The entity must not be null")
   String entityMustNotBeNull();
   
   @Message(id = BASE + 20, value = "Failed to load service %s from $java.home/lib/jaxrs.properties")
   String failedToLoadServiceFromJaxrsProperties(String factoryId);
   
   @Message(id = BASE + 25, value = "Failed to load service {0} from {1}", format=Format.MESSAGE_FORMAT)
   String failedToLoadServiceFromServiceId(String factoryId, String serviceId);
   
   @Message(id = BASE + 30, value = "Failed to load service %s from a system property")
   String failedToLoadServiceFromSystemProperty(String factoryId);
   
   @Message(id = BASE + 35, value = "Invalid response status code. Expected [{0}], was [{1}].", format=Format.MESSAGE_FORMAT)
   String invalidResponseStatusCode(int expected, int actual);
   
   @Message(id = BASE + 40, value = "The provider 'link' parameter value is 'null'.")
   String linkParameterValueNull();
   
   @Message(id = BASE + 45, value = "mediaType, language, encoding all null")
   String mediaTypeLanguageEncodingNull();

   @Message(id = BASE + 50, value = "name==null")
   String nameIsNull();
   
   @Message(id = BASE + 55, value = "No allowed method specified.")
   String noAllowedMethodSpecified();   

   @Message(id = BASE + 60, value = "Primary challenge parameter must not be null.")
   String primaryChallengeParameterNull();
   
   @Message(id = BASE + 65, value = "Provider for %s cannot be found")
   String providerCouldNotBeFound(String factoryId);
   
   @Message(id = BASE + 70, value = "Provider {0} could not be instantiated: {1}", format=Format.MESSAGE_FORMAT)
   String providerCouldNotBeInstantiated(String className, Exception e);
   
   @Message(id = BASE + 75, value = "Response does not contain required 'Allow' HTTP header.")
   String responseDoesContainAllowHeader();
   
   @Message(id = BASE + 80, value = "Status code of the supplied response [{0}] is not from the required status code family \"{1}\".", format=Format.MESSAGE_FORMAT)
   String statusNotFromRequiredFamily(int actual, Family expected);

   @Message(id = BASE + 85, value = "Supplied array of values must not be null.")
   String suppliedArrayMustNotBeNull();
   
   @Message(id = BASE + 90, value = "Supplied list of values must not be null.")
   String suppliedListMustNotBeNull();
   
   @Message(id = BASE + 95, value = "The type is incompatible with the class of the entity.")
   String typeIsIncompatible();
   
   @Message(id = BASE + 100, value = "Type must not be null")
   String typeMustNotBeNull();

   @Message(id = BASE + 105, value = "Type parameter %s not a class or parameterized type whose raw type is a class")
   String typeParameterNotAClass(String type);
   
   @Message(id = BASE + 110, value = "Unable to get context classloader instance.")
   String unableToGetContextClassloader();
   
   @Message(id = BASE + 115, value = "Unable to load provider class {0} using custom classloader {1} trying again with current classloader.", format=Format.MESSAGE_FORMAT)
   String unableToLoadProviderClass(String className, String classLoader);
   
   @Message(id = BASE + 120, value = "Underlying store must not be 'null'.")
   String underlyingStoreMustNotBeNull();
   
   @Message(id = BASE + 125, value = "value==null")
   String valueIsNull();
}
