package org.jboss.resteasy.client.jaxrs.i18n;

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;

import javax.ws.rs.core.MediaType;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.logging.annotations.Message.Format;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 26, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 4500;
   
   @Message(id = BASE + 0, value = "You can only set one of LinkHeaderParam.rel() and LinkHeaderParam.title() for on {0}.{1}", format=Format.MESSAGE_FORMAT)
   String canOnlySetOneLinkHeaderParam(String className, String methodName);
   
   @Message(id = BASE + 05, value = "Cannot set a form parameter if entity body already set")
   String cannotSetFormParameter();
   
   @Message(id = BASE + 10, value = "Client is closed.")
   String clientIsClosed();
   
   @Message(id = BASE + 15, value = "Client Proxy for : %s")
   String clientProxyFor(String className);
   
   @Message(id = BASE + 20, value = "Could not create a URL for {0} in {1}.{2}", format=Format.MESSAGE_FORMAT)
   String couldNotCreateURL(String uri, String className, String methodName);   
   
   @Message(id = BASE + 25, value = "Marking file '%s' to be deleted, as it could not be deleted while processing request:")
   String couldNotDeleteFile(String path);
   
   @Message(id = BASE + 30, value = "Could not find a method for: %s")
   String couldNotFindMethod(Method method);
   
   @Message(id = BASE + 35, value = "Could not process method %s")
   String couldNotProcessMethod(Method method);
   
   @Message(id = BASE + 40, value = "%s does not specify the type parameter T of GenericType<T>")
   String doesNotSpecifyTypeParameter(TypeVariable<?> var);
   
   @Message(id = BASE + 45, value = "The entity was already read, and it was of type %s")
   String entityAlreadyRead(Class<?> clazz);
   
   @Message(id = BASE + 50, value = "failed on registering class: %s")
   String failedOnRegisteringClass(String className);
   
   @Message(id = BASE + 55, value = "Failed to buffer aborted response")
   String failedToBufferAbortedResponse(); 
   
   @Message(id = BASE + 60, value = "Failed to buffer aborted response. Could not find writer for content-type {0} type: {1}", format=Format.MESSAGE_FORMAT)
   String failedToBufferAbortedResponseNoWriter(MediaType mediaType, String className); 
         
   @Message(id = BASE + 65, value = "A GET request cannot have a body.")
   String getRequestCannotHaveBody();
   
   @Message(id = BASE + 70, value = "Hostname verification failure")
   String hostnameVerificationFailure();
   
   @Message(id = BASE + 75, value = "Input stream was empty, there is no entity")
   String inputStreamWasEmpty();

   @Message(id = BASE + 80, value = "link was null")
   String linkWasNull();
   
   @Message(id = BASE + 85, value = "method was null")
   String methodWasNull();
   
   @Message(id = BASE + 90, value = "You must define a @Consumes type on your client method or interface, or supply a default")
   String mustDefineConsumesType();
   
   @Message(id = BASE + 95, value = "You must set either LinkHeaderParam.rel() or LinkHeaderParam.title() for on {0}.{1}", format=Format.MESSAGE_FORMAT)
   String mustSetLinkHeaderParam(String className, String methodName);
   
   @Message(id = BASE + 100, value = "You must use at least one, but no more than one http method annotation on: %s")
   String mustUseExactlyOneHttpMethod(String methodName);
   
   @Message(id = BASE + 105, value = "name was null")
   String nameWasNull();
   
   @Message(id = BASE + 110, value = "No type information to extract entity with.  You use other getEntity() methods")
   String noTypeInformation();

   @Message(id = BASE + 115, value = "parameters was null")
   String parametersWasNull();
   
   @Message(id = BASE + 120, value = "path was null")
   String pathWasNull();
   
   @Message(id = BASE + 123, value = "Please consider updating the version of Apache HttpClient being used.  Version 4.3.6+ is recommended.")
   String pleaseConsiderUnpdating();
   
   @Message(id = BASE + 125, value = "proxyInterface was null")
   String proxyInterfaceWasNull();
   
   @Message(id = BASE + 130, value = "resource was null")
   String resourceWasNull();
   
   @Message(id = BASE + 135, value = "Resteasy Client Proxy for : %s")
   String resteasyClientProxyFor(String className);
   
   @Message(id = BASE + 140, value = "Stream is closed")
   String streamIsClosed();
   
   @Message(id = BASE + 145, value = "templateValues entry was null")
   String templateValuesEntryWasNull();
   
   @Message(id = BASE + 150, value = "templateValues was null")
   String templateValuesWasNull();

   @Message(id = BASE + 152, value = "Unable to create new instance of %s")
   String unableToInstantiate(Class<?> clazz);
   
   @Message(id = BASE + 155, value = "Unable to invoke request")
   String unableToInvokeRequest();
   
   @Message(id = BASE + 160, value = "uriBuilder was null")
   String uriBuilderWasNull();

   @Message(id = BASE + 165, value = "uri was null")
   String uriWasNull();

   @Message(id = BASE + 170, value = "value was null")
   String valueWasNull();
   
   @Message(id = BASE + 175, value = "This verification path not implemented")
   String verificationPathNotImplemented();
   
   @Message(id = BASE + 180, value = "Could not close http response")
   String couldNotCloseHttpResponse();
}
