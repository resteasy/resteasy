package org.resteasy;

import org.resteasy.spi.ConstructorInjector;
import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.HttpResponseCodes;

import javax.ws.rs.WebApplicationException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ConstructorInjectorImpl implements ConstructorInjector
{
   protected Constructor constructor;
   protected ValueInjector[] params;
   protected PathParamIndex index;

   public ConstructorInjectorImpl(Constructor constructor, PathParamIndex index, ResteasyProviderFactory factory)
   {
      this.constructor = constructor;
      this.index = index;
      params = new ValueInjector[constructor.getParameterTypes().length];
      for (int i = 0; i < constructor.getParameterTypes().length; i++)
      {
         Class type = constructor.getParameterTypes()[i];
         Type genericType = constructor.getGenericParameterTypes()[i];
         Annotation[] annotations = constructor.getParameterAnnotations()[i];
         params[i] = InjectorFactoryImpl.getParameterExtractor(index, type, genericType, annotations, constructor, factory);
      }
   }

   public Object[] injectableArguments(HttpRequest input, HttpResponse response)
   {
      Object[] args = null;
      if (params != null && params.length > 0)
      {
         args = new Object[params.length];
         int i = 0;
         for (ValueInjector extractor : params)
         {
            args[i++] = extractor.inject(input, response);
         }
      }
      return args;
   }

   public Object[] injectableArguments()
   {
      Object[] args = null;
      if (params != null && params.length > 0)
      {
         args = new Object[params.length];
         int i = 0;
         for (ValueInjector extractor : params)
         {
            args[i++] = extractor.inject();
         }
      }
      return args;
   }

   public Object construct(HttpRequest request, HttpResponse httpResponse) throws Failure
   {
      Object[] args = null;
      try
      {
         args = injectableArguments(request, httpResponse);
      }
      catch (Exception e)
      {
         throw new Failure("Failed processing arguments of " + constructor.toString(), e, HttpResponseCodes.SC_BAD_REQUEST);
      }
      try
      {
         return constructor.newInstance(args);
      }
      catch (InstantiationException e)
      {
         throw new RuntimeException("Failed to construct " + constructor.toString(), e);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException("Failed to construct " + constructor.toString(), e);
      }
      catch (InvocationTargetException e)
      {
         Throwable cause = e.getCause();
         if (cause instanceof WebApplicationException)
         {
            WebApplicationException wae = (WebApplicationException) cause;
            if (wae.getResponse() != null)
            {
               cause.printStackTrace();
               return wae.getResponse();
            }
         }
         throw new RuntimeException("Failed to construct " + constructor.toString(), e.getCause());
      }
      catch (IllegalArgumentException e)
      {
         String msg = "Bad arguments passed to " + constructor.toString() + "  (";
         boolean first = false;
         for (Object arg : args)
         {
            if (!first)
            {
               first = true;
            }
            else
            {
               msg += ",";
            }
            if (arg == null)
            {
               msg += " null";
               continue;
            }
            msg += " " + arg;
         }
         throw new RuntimeException(msg, e);
      }
   }

   public Object construct()
   {
      Object[] args = null;
      args = injectableArguments();
      try
      {
         return constructor.newInstance(args);
      }
      catch (InstantiationException e)
      {
         throw new RuntimeException("Failed to construct " + constructor.toString(), e);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException("Failed to construct " + constructor.toString(), e);
      }
      catch (InvocationTargetException e)
      {
         throw new RuntimeException("Failed to construct " + constructor.toString(), e.getCause());
      }
      catch (IllegalArgumentException e)
      {
         String msg = "Bad arguments passed to " + constructor.toString() + "  (";
         boolean first = false;
         for (Object arg : args)
         {
            if (!first)
            {
               first = true;
            }
            else
            {
               msg += ",";
            }
            if (arg == null)
            {
               msg += " null";
               continue;
            }
            msg += " " + arg;
         }
         throw new RuntimeException(msg, e);
      }
   }
}