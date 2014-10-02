package org.jboss.resteasy.core;

import org.jboss.resteasy.i18n.Messages;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.validation.ValidatorAdapter;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MethodInjectorImpl implements MethodInjector
{
   protected Method method;
   protected Method invokedMethod;
   protected Class rootClass;
   protected ValueInjector[] params;
   protected ResteasyProviderFactory factory;
   protected ValidatorAdapter validatorAdapter;

   public MethodInjectorImpl(Class root, Method method, ResteasyProviderFactory factory)
   {
      this.method = method;
      this.rootClass = root;

      // invokedMethod is for when the target object might be a proxy and
      // resteasy is getting the bean class to introspect.
      // An example is a proxied Spring bean that is a resource
      this.invokedMethod = findInterfaceBasedMethod(root, method);
      this.factory = factory;
      params = new ValueInjector[method.getParameterTypes().length];
      /*
          We get the genericParameterTypes for the case of:

          interface Foo<T> {
             @PUT
             void put(List<T> l);
          }

          public class FooImpl implements Foo<Customer> {
              public void put(List<Customer> l) {...}
          }
       */
      Method actualMethod = null;
      // java.lang.reflect.Proxy removes generic type information
      // so use the method passed into this class.
      // see RESTEASY-685 for an example of this.
      if (Proxy.isProxyClass(root))
      {
         actualMethod = method;
      }
      else
      {
         actualMethod = Types.getImplementingMethod(root, method);
      }

      Type[] genericParameterTypes = actualMethod.getGenericParameterTypes();
      for (int i = 0; i < actualMethod.getParameterTypes().length; i++)
      {
         Class<?> type;
         Type genericType;

         // the parameter type might be a type variable defined in a superclass
         if (actualMethod.getGenericParameterTypes()[i] instanceof TypeVariable<?>)
         {
            // try to find out the value of the type variable
            genericType = Types.getActualValueOfTypeVariable(root, (TypeVariable<?>) genericParameterTypes[i]);
            type = Types.getRawType(genericType);
         }
         else
         {
            type = actualMethod.getParameterTypes()[i];
            genericType = genericParameterTypes[i];
         }

         Annotation[] annotations = method.getParameterAnnotations()[i];
         params[i] = factory.getInjectorFactory().createParameterExtractor(root, method, type, genericType, annotations);
      }

      ContextResolver<ValidatorAdapter> contextResolver = factory.getContextResolver(ValidatorAdapter.class, MediaType.WILDCARD_TYPE);
      if (contextResolver == null) return;
      validatorAdapter = contextResolver.getContext(null);
   }

   public static Method findInterfaceBasedMethod(Class root, Method method)
   {
      if (method.getDeclaringClass().isInterface() || root.isInterface()) return method;

      for (Class intf : root.getInterfaces())
      {
         try
         {
            return intf.getMethod(method.getName(), method.getParameterTypes());
         }
         catch (NoSuchMethodException ignored)
         {}
      }

      if (root.getSuperclass() == null || root.getSuperclass().equals(Object.class)) return method;
      return findInterfaceBasedMethod(root.getSuperclass(), method);

   }

   public ValueInjector[] getParams()
   {
      return params;
   }

   public Object[] injectArguments(HttpRequest input, HttpResponse response)
   {
      try
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
      catch (WebApplicationException we)
      {
         throw we;
      }
      catch (Failure f)
      {
         throw f;
      }
      catch (Exception e)
      {
         BadRequestException badRequest = new BadRequestException(Messages.MESSAGES.failedProcessingArguments(method.toString()), e);
         badRequest.setLoggable(true);
         throw badRequest;
      }
   }

   public Object invoke(HttpRequest request, HttpResponse httpResponse, Object resource) throws Failure, ApplicationException, WebApplicationException
   {
      Object[] args = injectArguments(request, httpResponse);

      if (validatorAdapter != null)
         validatorAdapter.applyValidation(resource, invokedMethod, args);

      try
      {

         return invokedMethod.invoke(resource, args);
      }
      catch (IllegalAccessException e)
      {
         throw new InternalServerErrorException(Messages.MESSAGES.notAllowedToReflectOnMethod(method.toString()), e);
      }
      catch (InvocationTargetException e)
      {
         Throwable cause = e.getCause();
         if (cause instanceof WebApplicationException)
         {
            WebApplicationException wae = (WebApplicationException) cause;
            throw wae;
         }
         throw new ApplicationException(cause);
      }
      catch (IllegalArgumentException e)
      {
         String msg = Messages.MESSAGES.badArguments(method.toString()) + "  (";
         if (args != null)
         {
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
               msg += " " + arg.getClass().getName() + " " + arg;
            }
         }
         msg += " )";
         throw new InternalServerErrorException(msg, e);
      }
   }

}
