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
import org.jboss.resteasy.spi.metadata.ResourceConstructor;

import javax.ws.rs.WebApplicationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

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

   @Override
   public CompletionStage<Object[]> injectableArguments(HttpRequest input, HttpResponse response, boolean unwrapAsync)
   {
      if (params != null && params.length > 0)
      {
         Object[] args = new Object[params.length];
         int i = 0;
         CompletionStage<Void> stage = CompletableFuture.completedFuture(null);
         for (ValueInjector extractor : params)
         {
            int ifinal = i++;
            stage = stage.thenCompose(v -> extractor.inject(input, response, unwrapAsync).thenAccept(value -> args[ifinal] = value));
         }
         return stage.thenApply(v -> args);
      }
      else
         return CompletableFuture.completedFuture(null);
   }

   @Override
   public CompletionStage<Object[]> injectableArguments(boolean unwrapAsync)
   {
      if (params != null && params.length > 0)
      {
         Object[] args = new Object[params.length];
         int i = 0;
         CompletionStage<Void> stage = CompletableFuture.completedFuture(null);
         for (ValueInjector extractor : params)
         {
            int ifinal = i++;
            stage = stage.thenCompose(v -> extractor.inject(unwrapAsync).thenAccept(value -> args[ifinal] = value));
         }
         return stage.thenApply(v -> args);
      }
      else
         return CompletableFuture.completedFuture(null);
   }

   public CompletionStage<Object> construct(HttpRequest request, HttpResponse httpResponse, boolean unwrapAsync) throws Failure, ApplicationException, WebApplicationException
   {
      return injectableArguments(request, httpResponse, unwrapAsync)
      .exceptionally(e -> {
         throw new InternalServerErrorException(Messages.MESSAGES.failedProcessingArguments(constructor.toString()), e);
      }).thenApply(args -> {
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
      });
   }

   @Override
   public CompletionStage<Object> construct(boolean unwrapAsync)
   {
      return injectableArguments(unwrapAsync)
            .thenApply(args -> {
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
            });
   }
}