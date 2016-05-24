package org.jboss.resteasy.cdi.i18n;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.logging.annotations.Message.Format;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 25, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 10500;

   @Message(id = BASE + 0, value = "ProcessInjectionTarget.getAnnotatedType() returned null. As a result, JAX-RS property injection will not work.")
   String annotatedTypeNull();

   @Message(id = BASE + 5, value = "Bean {0} does not have the scope defined. Binding to {1}.", format=Format.MESSAGE_FORMAT)
   String beanDoesNotHaveScopeDefined(Class<?> clazz, Annotation scope);

   @Message(id = BASE + 10, value = "Bean {0} has a scope defined.", format=Format.MESSAGE_FORMAT)
   String beanHasScopeDefined(Class<?> clazz);
   
   @Message(id = BASE + 15, value = "Bean {0} is a SLSB or Singleton. Leaving scope unmodified.", format=Format.MESSAGE_FORMAT)
   String beanIsSLSBOrSingleton(Class<?> clazz);
   
   @Message(id = BASE + 20, value = "Beans found for {0} : {1}", format=Format.MESSAGE_FORMAT)
   String beansFound(Type type, Set<Bean<?>> beans);
   
   @Message(id = BASE + 25, value = "Discovered CDI bean which is javax.ws.rs.core.Application subclass {0}.", format=Format.MESSAGE_FORMAT)
   String discoveredCDIBeanApplication(String classname);
   
   @Message(id = BASE + 30, value = "Discovered CDI bean which is a JAX-RS provider {0}.", format=Format.MESSAGE_FORMAT)
   String discoveredCDIBeanJaxRsProvider(String classname);

   @Message(id = BASE + 35, value = "Discovered CDI bean which is a JAX-RS resource {0}.", format=Format.MESSAGE_FORMAT)
   String discoveredCDIBeanJaxRsResource(String classname);
   
   @Message(id = BASE + 40, value = "Doing a lookup for BeanManager in {0}", format=Format.MESSAGE_FORMAT)
   String doingALookupForBeanManager(String name);
   
   @Message(id = BASE + 45, value = "Error occurred trying to look up via ServletContext.")
   String errorOccurredLookingUpServletContext();
   
   @Message(id = BASE + 50, value = "Error occurred trying to look up via CDI util.")
   String errorOccurredLookingUpViaCDIUtil();
   
   @Message(id = BASE + 55, value = "Found BeanManager at java:app/BeanManager")
   String foundBeanManagerAtJavaApp();

   @Message(id = BASE + 60, value = "Found BeanManager at java:comp/BeanManager")
   String foundBeanManagerAtJavaComp();

   @Message(id = BASE + 65, value = "Found BeanManager in ServletContext")
   String foundBeanManagerInServletContext();

   @Message(id = BASE + 70, value = "Found BeanManager via CDI Util")
   String foundBeanManagerViaCDI();
   
   @Message(id = BASE + 75, value = "No CDI beans found for {0}. Using default ConstructorInjector.", format=Format.MESSAGE_FORMAT)
   String noCDIBeansFound(Class<?> clazz);

   @Message(id = BASE + 80, value = "No lookup interface found for {0}", format=Format.MESSAGE_FORMAT)
   String noLookupInterface(Class<?> clazz);
   
   @Message(id = BASE + 85, value = "JaxrsInjectionTarget skipping validation outside of Resteasy context")
   String skippingValidationOutsideResteasyContext();
   
   @Message(id = BASE + 90, value = "{0} will be used for {1} lookup", format=Format.MESSAGE_FORMAT)
   String typeWillBeUsedForLookup(Type type, Class<?> clazz);

   @Message(id = BASE + 95, value = "Unable to find CDI class ")
   String unableToFindCDIClass();
   
   @Message(id = BASE + 100, value = "Unable to find ServletContext class.")
   String unableToFindServletContextClass();
   
   @Message(id = BASE + 105, value = "Unable to lookup BeanManager.")
   String unableToLookupBeanManager();
   
   @Message(id = BASE + 110, value = "Unable to obtain BeanManager from {0}", format=Format.MESSAGE_FORMAT)
   String unableToObtainBeanManager(String name);

   @Message(id = BASE + 115, value = "Unable to obtain ResteasyCdiExtension instance.")
   String unableToObtainResteasyCdiExtension();
   
   @Message(id = BASE + 120, value = "Unable to perform JNDI lookups. You are probably running on GAE.")
   String unableToPerformJNDILookups();
   
   @Message(id = BASE + 125, value = "Using CdiConstructorInjector for class {0}.", format=Format.MESSAGE_FORMAT)
   String usingCdiConstructorInjector(Class<?> clazz);
   
   @Message(id = BASE + 130, value = "Using {0} for lookup of Session Bean {1}.", format=Format.MESSAGE_FORMAT)
   String usingInterfaceForLookup(Type type, Class<?> clazz);
}
