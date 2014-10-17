package org.jboss.resteasy.jaxrs_api.i18n;

import java.net.URL;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.logging.annotations.MessageBundle;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Oct 11, 2014
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 9500;
   
   @Message(id = BASE + 0, value = "Arguments must not be null")
   String argumentsMustNotBeNull();

   @Message(id = BASE + 5, value = "ClassCastException: attempting to cast {0} to {1}", format=Format.MESSAGE_FORMAT)
   String classCastException(URL resource, String type);
   
   @Message(id = BASE + 10, value = "Could not find from factory file %s")
   String couldNotFindFromFactoryFile(URL url);
   
   @Message(id = BASE + 15, value = "The entity must not be null")
   String entityMustNotBeNull();

   @Message(id = BASE + 20, value = "mediaType, language, encoding all null")
   String mediaTypeLanguageEncodingNull();
   
   @Message(id = BASE + 25, value = "Missing type parameter.")
   String missingTypeParameter();

   @Message(id = BASE + 30, value = "name==null")
   String nameIsNull();

   @Message(id = BASE + 35, value = "path parameter is null")
   String pathParameterIsNull();

   @Message(id = BASE + 40, value = "Provider for %s cannot be found")
   String providerCouldNotBeFound(String factoryId);
   
   @Message(id = BASE + 45, value = "Provider {0} could not be instantiated: {1}", format=Format.MESSAGE_FORMAT)
   String providerCouldNotBeInstantiated(String className, Exception e);
   
   @Message(id = BASE + 50, value = "The type is incompatible with the class of the entity")
   String typeIsIncompatible();
   
   @Message(id = BASE + 55, value = "value==null")
   String valueIsNull();
}
