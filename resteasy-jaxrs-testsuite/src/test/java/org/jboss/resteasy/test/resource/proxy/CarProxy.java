package org.jboss.resteasy.test.resource.proxy;

import javassist.util.proxy.MethodHandler;

import java.lang.reflect.Method;

/**
 * Trivial proxy that forwards method calls to underlying Car instance.
 * In Seam, similar proxies are used for nontrivial purposes.
 *
 * @author Jozef Hartinger
 */
public class CarProxy implements MethodHandler
{
   private Car car;

   public CarProxy(Car car)
   {
      this.car = car;
   }

   public Object invoke(Object object, Method method, Method proceed, Object[] args) throws Throwable
   {
      return method.invoke(car, args);
   }
}
