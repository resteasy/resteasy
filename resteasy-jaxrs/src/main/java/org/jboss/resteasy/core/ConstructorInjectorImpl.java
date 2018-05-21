package org.jboss.resteasy.core;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.ConstructorParameter;
import org.jboss.resteasy.spi.metadata.MethodParameter;
import org.jboss.resteasy.spi.metadata.ResourceConstructor;

import javax.ws.rs.WebApplicationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ConstructorInjectorImpl implements ConstructorInjector
{
   protected Constructor constructor;
   protected ValueInjector[] params;

   public ConstructorInjectorImpl(ResourceConstructor constructor, ResteasyProviderFactory factory)
   {
      this.constructor = constructor.getConstructor();
      params = new ValueInjector[constructor.getParams().length];
      int i = 0;
      for (ConstructorParameter parameter : constructor.getParams())
      {
         params[i++] = factory.getInjectorFactory().createParameterExtractor(parameter, factory);
      }

   }


   public ConstructorInjectorImpl(Constructor constructor, ResteasyProviderFactory factory)
   {
      this.constructor = constructor;
      params = new ValueInjector[constructor.getParameterTypes().length];
      Parameter[] reflectionParameters = constructor.getParameters();
      for (int i = 0; i < constructor.getParameterTypes().length; i++)
      {
         Class type = constructor.getParameterTypes()[i];
         Type genericType = constructor.getGenericParameterTypes()[i];
         Annotation[] annotations = constructor.getParameterAnnotations()[i];
         String name = reflectionParameters[i].getName();
         params[i] = factory.getInjectorFactory().createParameterExtractor(constructor.getDeclaringClass(), constructor, name, type, genericType, annotations, factory);
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

   public Object construct(HttpRequest request, HttpResponse httpResponse) throws Failure, ApplicationException, WebApplicationException
   {
      Object[] args = null;
      try
      {
         args = injectableArguments(request, httpResponse);
      }
      catch (Exception e)
      {
         throw new InternalServerErrorException(Messages.MESSAGES.failedProcessingArguments(constructor.toString()), e);
      }
      try
      {
         return constructor.newInstance(args);
      }
      catch (InstantiationException e)
      {
         throw new InternalServerErrorException(Messages.MESSAGES.failedToConstruct(constructor.toString()), e);
      }
      catch (IllegalAccessException e)
      {
         throw new InternalServerErrorException(Messages.MESSAGES.failedToConstruct(constructor.toString()), e);
      }
      catch (InvocationTargetException e)
      {
         Throwable cause = e.getCause();
         if (cause instanceof WebApplicationException)
         {
            throw (WebApplicationException) cause;
         }
         throw new ApplicationException(Messages.MESSAGES.failedToConstruct(constructor.toString()), e.getCause());
      }
      catch (IllegalArgumentException e)
      {
         String msg = Messages.MESSAGES.badArguments(constructor.toString() + "  (");
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
         throw new InternalServerErrorException(msg, e);
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
         throw new RuntimeException(Messages.MESSAGES.failedToConstruct(constructor.toString()), e);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(Messages.MESSAGES.failedToConstruct(constructor.toString()), e);
      }
      catch (InvocationTargetException e)
      {
         throw new RuntimeException(Messages.MESSAGES.failedToConstruct(constructor.toString()), e.getCause());
      }
      catch (IllegalArgumentException e)
      {
         String msg = Messages.MESSAGES.badArguments(constructor.toString() + "  (");
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