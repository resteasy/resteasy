package org.jboss.resteasy.client.jaxrs.internal.proxy;

import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ProxyConfig;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.WebTarget;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

public class SubResourceInvoker implements MethodInvoker
{
   final ProxyConfig config;
   final Class<?> iface;
   final Method method;
   final ResteasyWebTarget parent;
   Annotation[] jaxParams;
   boolean hasJaxParams;

   public SubResourceInvoker(ResteasyWebTarget parent, Method method, ProxyConfig config)
   {
      this.config = config;
      this.method = method;
      this.iface = method.getReturnType();
      jaxParams = new Annotation[method.getParameterTypes().length];
      for (int i = 0; i < jaxParams.length; i++)
      {
         Annotation[] paramAnnotations = method.getParameterAnnotations()[i];
         for (Annotation annotation : paramAnnotations)
         {
            if (annotation instanceof PathParam || annotation instanceof MatrixParam)
            {
               jaxParams[i] = annotation;
               hasJaxParams = true;
               break;
            }
         }
      }
      if (method.isAnnotationPresent(Path.class))
      {
         parent = parent.path(method.getAnnotation(Path.class).value());
      }
      this.parent = parent;

   }

   @Override
   public Object invoke(Object[] args)
   {
      WebTarget target = parent;
      if (hasJaxParams)
      {
         HashMap<String, Object> pathParams = new HashMap<String, Object>();
         for (int i = 0; i < jaxParams.length; i++)
         {
            if (jaxParams[i] instanceof PathParam) 
            {
               pathParams.put(((PathParam) jaxParams[i]).value(), args[i]);
            }
            else if (jaxParams[i] instanceof MatrixParam)
            {
               target = target.matrixParam(((MatrixParam) jaxParams[i]).value(), args[i]);
            }
         }
         if (!pathParams.isEmpty()) 
         {
            target = target.resolveTemplates(pathParams);
         }
      }
      return ProxyBuilder.proxy(iface, target, config);
   }
}
