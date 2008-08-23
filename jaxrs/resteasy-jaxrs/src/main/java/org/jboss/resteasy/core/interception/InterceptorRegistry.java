package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.core.ResourceMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class InterceptorRegistry
{
   private List<Class> perResourceMethodInterceptorClasses = new ArrayList<Class>();
   private List<ResourceMethodInterceptor> resourceMethodIntercptors = new ArrayList<ResourceMethodInterceptor>();

   public List<Class> getPerResourceMethodInterceptorClasses()
   {
      return perResourceMethodInterceptorClasses;
   }

   public List<ResourceMethodInterceptor> getResourceMethodIntercptors()
   {
      return resourceMethodIntercptors;
   }

   public void registerResourceMethodInterceptor(Class clazz)
   {
      perResourceMethodInterceptorClasses.add(clazz);
   }

   public void registerResourceMethodInterceptors(Class[] classes)
   {
      for (Class clazz : classes) registerResourceMethodInterceptor(clazz);
   }

   public void registerResourceMethodInterceptor(ResourceMethodInterceptor interceptor)
   {
      resourceMethodIntercptors.add(interceptor);
   }

   public ResourceMethodInterceptor[] bind(ResourceMethod method)
   {
      List<ResourceMethodInterceptor> list = new ArrayList<ResourceMethodInterceptor>();
      for (Class<ResourceMethodInterceptor> clazz : perResourceMethodInterceptorClasses)
      {
         ResourceMethodInterceptor interceptor = null;
         try
         {
            interceptor = clazz.newInstance();
         }
         catch (InstantiationException e)
         {
            throw new RuntimeException(e);
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException(e);
         }
         if (interceptor.accepted(method))
         {
            list.add(interceptor);
         }
      }
      for (ResourceMethodInterceptor interceptor : resourceMethodIntercptors)
      {
         if (interceptor.accepted(method))
         {
            list.add(interceptor);
         }
      }
      return list.toArray(new ResourceMethodInterceptor[0]);
   }
}
