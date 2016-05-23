package org.jboss.resteasy.links.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.logging.annotations.Message.Format;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 28, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 12000;

   @Message(id = BASE + 0, value = "Cannot guess collection type for service discovery")
   String cannotGuessCollectionType();
   
   @Message(id = BASE + 5, value = "Cannot guess resource type for service discovery")
   String cannotGuessResourceType();

   @Message(id = BASE + 10, value = "Cannot guess type for Response")
   String cannotGuessType();
   
   @Message(id = BASE + 15, value = "Could not instantiate ELProvider class %s")
   String couldNotInstantiateELProviderClass(String className);
   
   @Message(id = BASE + 20, value = "Discovery failed for method {0}.{1}: {2}", format=Format.MESSAGE_FORMAT)
   String discoveryFailedForMethod(String className, String methodName, String s);
   
   @Message(id = BASE + 25, value = "Failed to evaluate EL expression: %s")
   String failedToEvaluateELExpression(String expression);
   
   @Message(id = BASE + 30, value = "Failed to find bean property %s")
   String failedToFindBeanProperty(String property);

   @Message(id = BASE + 35, value = "Failed to inject links in %s")
   String failedToInjectLinks(Object entity);
   
   @Message(id = BASE + 40, value = "Failed to instantiate ELProvider: %s")
   String failedToInstantiateELProvider(String className);

   @Message(id = BASE + 45, value = "Failed to read field %s")
   String failedToReadField(String field);
   
   @Message(id = BASE + 50, value = "Failed to read property %s")
   String failedToReadProperty(String property);

   @Message(id = BASE + 55, value = "Failed to read property from method %s")
   String failedToReadPropertyFromMethod(String property);
   
   @Message(id = BASE + 60, value = "Not enough URI parameters: expecting {0} but only found {1}", format=Format.MESSAGE_FORMAT)
   String notEnoughtUriParameters(int expected, int actual);

   @Message(id = BASE + 65, value = "Failed to access/reuse user-created service discovery in %s")
   String failedToReuseServiceDiscovery(Object entity);
}
