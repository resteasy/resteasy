package org.resteasy;

import org.resteasy.spi.ConstructorInjector;
import org.resteasy.spi.InjectorFactory;
import org.resteasy.spi.MethodInjector;
import org.resteasy.spi.PropertyInjector;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.FindAnnotation;

import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class InjectorFactoryImpl implements InjectorFactory
{
   private PathParamIndex index;
   private ResteasyProviderFactory factory;


   public InjectorFactoryImpl(PathParamIndex index, ResteasyProviderFactory factory)
   {
      this.index = index;
      this.factory = factory;
   }

   public ConstructorInjector createConstructor(Constructor constructor)
   {
      return new ConstructorInjectorImpl(constructor, index, factory);
   }

   public PropertyInjector createPropertyInjector(Class resourceClass)
   {
      return new PropertyInjectorImpl(resourceClass, index, factory);
   }

   public MethodInjector createMethodInjector(Method method)
   {
      return new MethodInjectorImpl(method, index, factory);
   }

   public static ValueInjector getParameterExtractor(PathParamIndex index, Class type, Type genericType, Annotation[] annotations, AccessibleObject target, ResteasyProviderFactory providerFactory)
   {

      DefaultValue defaultValue = FindAnnotation.findAnnotation(annotations, DefaultValue.class);
      boolean encode = FindAnnotation.findAnnotation(annotations, Encoded.class) != null || target.isAnnotationPresent(Encoded.class) || type.isAnnotationPresent(Encoded.class);
      String defaultVal = null;
      if (defaultValue != null) defaultVal = defaultValue.value();

      QueryParam query;
      HeaderParam header;
      MatrixParam matrix;
      PathParam uriParam;
      CookieParam cookie;

      if ((query = FindAnnotation.findAnnotation(annotations, QueryParam.class)) != null)
      {
         return new QueryParamInjector(type, genericType, target, query.value(), defaultVal, encode, query.encode());
      }
      else if ((header = FindAnnotation.findAnnotation(annotations, HeaderParam.class)) != null)
      {
         return new HeaderParamInjector(type, genericType, target, header.value(), defaultVal);
      }
      else if ((cookie = FindAnnotation.findAnnotation(annotations, CookieParam.class)) != null)
      {
         return new CookieParamInjector(type, genericType, target, cookie.value(), defaultVal);
      }
      else if ((uriParam = FindAnnotation.findAnnotation(annotations, PathParam.class)) != null)
      {
         return new PathParamInjector(index, type, genericType, target, uriParam.value(), defaultVal, encode);
      }
      else if ((matrix = FindAnnotation.findAnnotation(annotations, MatrixParam.class)) != null)
      {
         return new MatrixParamInjector(type, genericType, target, matrix.value(), defaultVal);
      }
      else if (FindAnnotation.findAnnotation(annotations, Context.class) != null)
      {
         return new ContextParameterInjector(type, providerFactory);
      }
      else
      {
         return new MessageBodyParameterInjector(type, genericType, annotations, providerFactory);
      }

   }
}
