package org.jboss.resteasy.core;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.FindAnnotation;

import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
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
@SuppressWarnings("unchecked")
public class InjectorFactoryImpl implements InjectorFactory
{
   private ResteasyProviderFactory providerFactory;


   public InjectorFactoryImpl(ResteasyProviderFactory factory)
   {
      this.providerFactory = factory;
   }

   public ConstructorInjector createConstructor(Constructor constructor)
   {
      return new ConstructorInjectorImpl(constructor, providerFactory);
   }

   public PropertyInjector createPropertyInjector(Class resourceClass)
   {
      return new PropertyInjectorImpl(resourceClass, providerFactory);
   }

   public MethodInjector createMethodInjector(Class root, Method method)
   {
      return new MethodInjectorImpl(root, method, providerFactory);
   }

   public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations)
   {
      DefaultValue defaultValue = FindAnnotation.findAnnotation(annotations, DefaultValue.class);
      boolean encode = FindAnnotation.findAnnotation(annotations, Encoded.class) != null || injectTarget.isAnnotationPresent(Encoded.class) || type.isAnnotationPresent(Encoded.class);
      String defaultVal = null;
      if (defaultValue != null) defaultVal = defaultValue.value();

      QueryParam query;
      HeaderParam header;
      MatrixParam matrix;
      PathParam uriParam;
      CookieParam cookie;
      FormParam formParam;
      Suspend suspend;


      if ((query = FindAnnotation.findAnnotation(annotations, QueryParam.class)) != null)
      {
         return new QueryParamInjector(type, genericType, injectTarget, query.value(), defaultVal, encode, annotations, providerFactory);
      }
      else if ((header = FindAnnotation.findAnnotation(annotations, HeaderParam.class)) != null)
      {
         return new HeaderParamInjector(type, genericType, injectTarget, header.value(), defaultVal, annotations, providerFactory);
      }
      else if ((formParam = FindAnnotation.findAnnotation(annotations, FormParam.class)) != null)
      {
         return new FormParamInjector(type, genericType, injectTarget, formParam.value(), defaultVal, annotations, providerFactory);
      }
      else if ((cookie = FindAnnotation.findAnnotation(annotations, CookieParam.class)) != null)
      {
         return new CookieParamInjector(type, genericType, injectTarget, cookie.value(), defaultVal, annotations, providerFactory);
      }
      else if ((uriParam = FindAnnotation.findAnnotation(annotations, PathParam.class)) != null)
      {
         return new PathParamInjector(type, genericType, injectTarget, uriParam.value(), defaultVal, encode, annotations, providerFactory);
      }
      else if (FindAnnotation.findAnnotation(annotations, Form.class) != null)
      {
         return new FormInjector(type, providerFactory);
      }
      else if ((matrix = FindAnnotation.findAnnotation(annotations, MatrixParam.class)) != null)
      {
         return new MatrixParamInjector(type, genericType, injectTarget, matrix.value(), defaultVal, annotations, providerFactory);
      }
      else if ((suspend = FindAnnotation.findAnnotation(annotations, Suspend.class)) != null)
      {
         return new SuspendInjector(suspend, type);
      }
      else if (FindAnnotation.findAnnotation(annotations, Context.class) != null)
      {
         return new ContextParameterInjector(type, providerFactory);
      }
      else
      {
         return new MessageBodyParameterInjector(injectTargetClass, injectTarget, type, genericType, annotations, providerFactory);
      }
   }

   public static ValueInjector getParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations, ResteasyProviderFactory providerFactory)
   {
      return providerFactory.getInjectorFactory().createParameterExtractor(injectTargetClass, injectTarget, type, genericType, annotations);
   }
}
