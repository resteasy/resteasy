package org.jboss.resteasy.core;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.BeanParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.jboss.resteasy.util.FindAnnotation.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class InjectorFactoryImpl implements InjectorFactory
{
   private ConcurrentHashMap<Class<?>, Class<?>> contextProxyCache = new ConcurrentHashMap<Class<?>, Class<?>>();


   public ConstructorInjector createConstructor(Constructor constructor, ResteasyProviderFactory providerFactory)
   {
      return new ConstructorInjectorImpl(constructor, providerFactory);
   }

   public PropertyInjector createPropertyInjector(Class resourceClass, ResteasyProviderFactory providerFactory)
   {
      return new PropertyInjectorImpl(resourceClass, providerFactory);
   }

   public MethodInjector createMethodInjector(Class root, Method method, ResteasyProviderFactory providerFactory)
   {
      return new MethodInjectorImpl(root, method, providerFactory);
   }

   public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type,
                                                 Type genericType, Annotation[] annotations, ResteasyProviderFactory providerFactory)
   {
      return createParameterExtractor(injectTargetClass, injectTarget, type, genericType, annotations, true, providerFactory);
   }

   public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations, boolean useDefault, ResteasyProviderFactory providerFactory)
   {
      DefaultValue defaultValue = findAnnotation(annotations, DefaultValue.class);
      boolean encode = findAnnotation(annotations, Encoded.class) != null || injectTarget.isAnnotationPresent(Encoded.class) || type.isAnnotationPresent(Encoded.class);
      String defaultVal = null;
      if (defaultValue != null) defaultVal = defaultValue.value();

      QueryParam query;
      HeaderParam header;
      MatrixParam matrix;
      PathParam uriParam;
      CookieParam cookie;
      FormParam formParam;
      Form form;
      Suspend suspend;
      Suspended suspended;


      if ((query = findAnnotation(annotations, QueryParam.class)) != null)
      {
         return new QueryParamInjector(type, genericType, injectTarget, query.value(), defaultVal, encode, annotations, providerFactory);
      }
      else if ((header = findAnnotation(annotations, HeaderParam.class)) != null)
      {
         return new HeaderParamInjector(type, genericType, injectTarget, header.value(), defaultVal, annotations, providerFactory);
      }
      else if ((formParam = findAnnotation(annotations, FormParam.class)) != null)
      {
         return new FormParamInjector(type, genericType, injectTarget, formParam.value(), defaultVal, annotations, providerFactory);
      }
      else if ((cookie = findAnnotation(annotations, CookieParam.class)) != null)
      {
         return new CookieParamInjector(type, genericType, injectTarget, cookie.value(), defaultVal, annotations, providerFactory);
      }
      else if ((uriParam = findAnnotation(annotations, PathParam.class)) != null)
      {
         return new PathParamInjector(type, genericType, injectTarget, uriParam.value(), defaultVal, encode, annotations, providerFactory);
      }
      else if ((form = findAnnotation(annotations, Form.class)) != null)
      {
         String prefix = form.prefix();
         if (prefix.length() > 0)
         {
            if (genericType instanceof ParameterizedType)
            {
               ParameterizedType pType = (ParameterizedType) genericType;
               if (Types.isA(List.class, pType))
               {
                  return new ListFormInjector(type, Types.getArgumentType(pType, 0), prefix, providerFactory);
               }
               if (Types.isA(Map.class, pType))
               {
                  return new MapFormInjector(type, Types.getArgumentType(pType, 0), Types.getArgumentType(pType, 1), prefix, providerFactory);
               }
            }
            return new PrefixedFormInjector(type, prefix, providerFactory);
         }
         return new FormInjector(type, providerFactory);
      }
      else if (findAnnotation(annotations, BeanParam.class) != null)
      {
         return new FormInjector(type, providerFactory);
      }
      else if ((matrix = findAnnotation(annotations, MatrixParam.class)) != null)
      {
         return new MatrixParamInjector(type, genericType, injectTarget, matrix.value(), defaultVal, annotations, providerFactory);
      }
      else if ((suspend = findAnnotation(annotations, Suspend.class)) != null)
      {
         return new SuspendInjector(suspend, type);
      }
      else if (findAnnotation(annotations, Context.class) != null)
      {
         Class proxy = null;
         if (type.isInterface())
         {
            proxy = contextProxyCache.get(type);
            if (proxy == null)
            {
               proxy = Proxy.getProxyClass(type.getClassLoader(), type);
               contextProxyCache.putIfAbsent(type, proxy);
            }
         }
         return new ContextParameterInjector(proxy, type, providerFactory);
      }
      else if ((suspended = findAnnotation(annotations, Suspended.class)) != null)
      {
         return new AsynchronousResponseInjector(suspended);
      }
      else if (javax.ws.rs.container.AsyncResponse.class.isAssignableFrom(type))
      {
         return new AsynchronousResponseInjector(suspended);
      }
      else if (useDefault)
      {
         return new MessageBodyParameterInjector(injectTargetClass, injectTarget, type, genericType, annotations, providerFactory);
      }
      else
      {
         return null;
      }
   }

}
