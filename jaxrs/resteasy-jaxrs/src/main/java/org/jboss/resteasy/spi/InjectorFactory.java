package org.jboss.resteasy.spi;

import org.jboss.resteasy.core.ValueInjector;

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
   MethodInjector createMethodInjector(Class root, Method method, ResteasyProviderFactory factory);
   ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations, ResteasyProviderFactory factory);
   ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations, boolean useDefault, ResteasyProviderFactory factory);
}
