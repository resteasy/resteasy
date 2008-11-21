package org.jboss.resteasy.spi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface InjectorFactory
{
   ConstructorInjector createConstructor(Constructor constructor);

   PropertyInjector createPropertyInjector(Class resourceClass);

   MethodInjector createMethodInjector(Class root, Method method);
}
