package org.jboss.resteasy.spi;

import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceConstructor;
import org.jboss.resteasy.spi.metadata.ResourceLocator;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface InjectorFactory
{
   ConstructorInjector createConstructor(Constructor constructor, ResteasyProviderFactory factory);
   PropertyInjector createPropertyInjector(Class resourceClass, ResteasyProviderFactory factory);
   ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, String defaultName, Class type, Type genericType, Annotation[] annotations, ResteasyProviderFactory factory);
   ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, String defaultName, Class type, Type genericType, Annotation[] annotations, boolean useDefault, ResteasyProviderFactory factory);

   ValueInjector createParameterExtractor(Parameter parameter, ResteasyProviderFactory providerFactory);

   MethodInjector createMethodInjector(ResourceLocator method, ResteasyProviderFactory factory);

   PropertyInjector createPropertyInjector(ResourceClass resourceClass, ResteasyProviderFactory providerFactory);

   ConstructorInjector createConstructor(ResourceConstructor constructor, ResteasyProviderFactory providerFactory);
}
