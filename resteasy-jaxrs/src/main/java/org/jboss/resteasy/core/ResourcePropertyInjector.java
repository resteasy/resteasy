package org.jboss.resteasy.core;

import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.FieldParameter;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.SetterParameter;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.MethodHashing;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourcePropertyInjector implements PropertyInjector
{
   private static class FieldInjector
   {
      public FieldParameter param;
      public ValueInjector injector;

      private FieldInjector(FieldParameter param, ValueInjector injector)
      {
         this.param = param;
         this.injector = injector;
      }
   }

   private static class SetterInjector
   {
      public SetterParameter param;
      public ValueInjector injector;

      private SetterInjector(SetterParameter param, ValueInjector injector)
      {
         this.param = param;
         this.injector = injector;
      }
   }

   protected List<FieldInjector> fields = new ArrayList<FieldInjector>();
   protected List<SetterInjector> setters = new ArrayList<SetterInjector>();
   protected ResourceClass resourceClass;
   protected ResteasyProviderFactory factory;


   public ResourcePropertyInjector(ResourceClass resourceClass, ResteasyProviderFactory factory)
   {
      this.resourceClass = resourceClass;
      this.factory = factory;

      for (FieldParameter param : resourceClass.getFields())
      {
         ValueInjector injector = factory.getInjectorFactory().createParameterExtractor(param, factory);
         if (injector == null) continue;
         fields.add(new FieldInjector(param, injector));
      }
      for (SetterParameter param : resourceClass.getSetters())
      {
         ValueInjector injector = factory.getInjectorFactory().createParameterExtractor(param, factory);
         if (injector == null) continue;
         setters.add(new SetterInjector(param, injector));
      }
   }

   @Override
   public CompletionStage<Void> inject(HttpRequest request, HttpResponse response, Object target, boolean unwrapAsync) throws Failure
   {
      CompletionStage<Void> ret = CompletableFuture.completedFuture(null);
      for (FieldInjector injector : fields)
      {
         ret = ret.thenCompose(v -> 
            injector.injector.inject(request, response, unwrapAsync)
            .thenAccept(value -> {
               try
               {
                  injector.param.getField().set(target, value);
               }
               catch (IllegalAccessException e)
               {
                  throw new InternalServerErrorException(e);
               }
         }));
      }
      for (SetterInjector injector : setters)
      {
         ret = ret.thenCompose(v -> 
            injector.injector.inject(request, response, unwrapAsync)
            .thenAccept(value -> {
                     try
                     {
                        injector.param.getSetter().invoke(target, value);
                     }
                     catch (IllegalAccessException e)
                     {
                        throw new InternalServerErrorException(e);
                     }
                     catch (InvocationTargetException e)
                     {
                        throw new ApplicationException(e.getCause());
                     }
                  }));
      }
      return ret;
   }

   @Override
   public CompletionStage<Void> inject(Object target, boolean unwrapAsync)
   {
      CompletionStage<Void> ret = CompletableFuture.completedFuture(null);
      for (FieldInjector injector : fields)
      {
         ret = ret.thenCompose(v -> 
            injector.injector.inject(unwrapAsync)
            .thenAccept(value -> {
               try
               {
                  injector.param.getField().set(target, value);
               }
               catch (IllegalAccessException e)
               {
                  throw new InternalServerErrorException(e);
               }
         }));
      }
      for (SetterInjector injector : setters)
      {
         ret = ret.thenCompose(v -> 
            injector.injector.inject(unwrapAsync)
            .thenAccept(value -> {
                     try
                     {
                        injector.param.getSetter().invoke(target, value);
                     }
                     catch (IllegalAccessException e)
                     {
                        throw new InternalServerErrorException(e);
                     }
                     catch (InvocationTargetException e)
                     {
                        throw new ApplicationException(e.getCause());
                     }
                  }));
      }
      return ret;
   }
}
