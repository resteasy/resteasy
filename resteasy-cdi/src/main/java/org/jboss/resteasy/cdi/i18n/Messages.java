/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi.i18n;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.Set;

import jakarta.enterprise.inject.spi.Bean;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LoggingSupport;

@MessageBundle(projectCode = "RESTEASY")
public interface Messages {
    Messages MESSAGES = LoggingSupport.getBundle(MethodHandles.lookup(), Messages.class);

    @Message(id = 10500, value = "ProcessInjectionTarget.getAnnotatedType() returned null. As a result, property injection will not work.")
    String annotatedTypeNull();

    @Message(id = 10505, value = "Bean {0} does not have the scope defined. Binding to {1}.", format = Format.MESSAGE_FORMAT)
    String beanDoesNotHaveScopeDefined(Class<?> clazz, Annotation scope);

    @Message(id = 10510, value = "Bean {0} has a scope defined.", format = Format.MESSAGE_FORMAT)
    String beanHasScopeDefined(Class<?> clazz);

    @Message(id = 10515, value = "Bean {0} is a SLSB or Singleton. Leaving scope unmodified.", format = Format.MESSAGE_FORMAT)
    String beanIsSLSBOrSingleton(Class<?> clazz);

    @Message(id = 10520, value = "Beans found for {0} : {1}", format = Format.MESSAGE_FORMAT)
    String beansFound(Type type, Set<Bean<?>> beans);

    @Message(id = 10525, value = "Discovered CDI bean which is jakarta.ws.rs.core.Application subclass {0}.", format = Format.MESSAGE_FORMAT)
    String discoveredCDIBeanApplication(String classname);

    @Message(id = 10530, value = "Discovered CDI bean which is a provider {0}.", format = Format.MESSAGE_FORMAT)
    String discoveredCDIBeanJaxRsProvider(String classname);

    @Message(id = 10535, value = "Discovered CDI bean which is a resource {0}.", format = Format.MESSAGE_FORMAT)
    String discoveredCDIBeanJaxRsResource(String classname);

    @Message(id = 10540, value = "Doing a lookup for BeanManager in {0}", format = Format.MESSAGE_FORMAT)
    String doingALookupForBeanManager(String name);

    @Message(id = 10545, value = "Error occurred trying to look up via ServletContext.")
    String errorOccurredLookingUpServletContext();

    @Message(id = 10550, value = "Error occurred trying to look up via CDI util.")
    String errorOccurredLookingUpViaCDIUtil();

    @Message(id = 10555, value = "Found BeanManager at java:app/BeanManager")
    String foundBeanManagerAtJavaApp();

    @Message(id = 10560, value = "Found BeanManager at java:comp/BeanManager")
    String foundBeanManagerAtJavaComp();

    @Message(id = 10565, value = "Found BeanManager in ServletContext")
    String foundBeanManagerInServletContext();

    @Message(id = 10570, value = "Found BeanManager via CDI Util")
    String foundBeanManagerViaCDI();

    @Message(id = 10575, value = "No CDI beans found for {0}. Using default ConstructorInjector.", format = Format.MESSAGE_FORMAT)
    String noCDIBeansFound(Class<?> clazz);

    @Message(id = 10580, value = "No lookup interface found for {0}", format = Format.MESSAGE_FORMAT)
    String noLookupInterface(Class<?> clazz);

    @Message(id = 10585, value = "JaxrsInjectionTarget skipping validation outside of Resteasy context")
    String skippingValidationOutsideResteasyContext();

    @Message(id = 10590, value = "{0} will be used for {1} lookup", format = Format.MESSAGE_FORMAT)
    String typeWillBeUsedForLookup(Type type, Class<?> clazz);

    @Message(id = 10595, value = "Unable to find CDI class ")
    String unableToFindCDIClass();

    @Message(id = 10600, value = "Unable to find ServletContext class.")
    String unableToFindServletContextClass();

    @Message(id = 10605, value = "Unable to lookup BeanManager.")
    String unableToLookupBeanManager();

    @Message(id = 10610, value = "Unable to obtain BeanManager from {0}", format = Format.MESSAGE_FORMAT)
    String unableToObtainBeanManager(String name);

    @Message(id = 10615, value = "Unable to obtain ResteasyCdiExtension instance.")
    String unableToObtainResteasyCdiExtension();

    @Message(id = 10620, value = "Unable to perform JNDI lookups. You are probably running on GAE.")
    String unableToPerformJNDILookups();

    @Message(id = 10625, value = "Using CdiConstructorInjector for class {0}.", format = Format.MESSAGE_FORMAT)
    String usingCdiConstructorInjector(Class<?> clazz);

    @Message(id = 10630, value = "Using {0} for lookup of Session Bean {1}.", format = Format.MESSAGE_FORMAT)
    String usingInterfaceForLookup(Type type, Class<?> clazz);

    @Message(id = 10635, value = "Unable to resolve CDI bean for resource class %s.")
    IllegalStateException unableToResolveBean(String classname);
}
