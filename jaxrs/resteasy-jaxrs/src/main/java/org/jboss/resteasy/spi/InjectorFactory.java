package org.jboss.resteasy.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.jboss.resteasy.core.ValueInjector;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface InjectorFactory
{
   ConstructorInjector createConstructor(Constructor constructor);

   PropertyInjector createPropertyInjector(Class resourceClass);

   MethodInjector createMethodInjector(Class root, Method method);

   ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations);

   ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations, boolean useDefault);
}
