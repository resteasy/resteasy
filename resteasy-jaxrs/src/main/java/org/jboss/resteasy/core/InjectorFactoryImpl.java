package org.jboss.resteasy.core;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.annotations.NewCookieParam;
import org.jboss.resteasy.annotations.NewFormParam;
import org.jboss.resteasy.annotations.NewHeaderParam;
import org.jboss.resteasy.annotations.NewMatrixParam;
import org.jboss.resteasy.annotations.NewPathParam;
import org.jboss.resteasy.annotations.NewQueryParam;
import org.jboss.resteasy.annotations.Query;
import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceConstructor;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;

import static org.jboss.resteasy.util.FindAnnotation.findAnnotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class InjectorFactoryImpl implements InjectorFactory
{
   @Override
   public ConstructorInjector createConstructor(Constructor constructor, ResteasyProviderFactory providerFactory)
   {
      return new ConstructorInjectorImpl(constructor, providerFactory);
   }

   @Override
   public ConstructorInjector createConstructor(ResourceConstructor constructor, ResteasyProviderFactory providerFactory)
   {
      return new ConstructorInjectorImpl(constructor, providerFactory);
   }

   @Override
   public PropertyInjector createPropertyInjector(Class resourceClass, ResteasyProviderFactory providerFactory)
   {
      return new PropertyInjectorImpl(resourceClass, providerFactory);
   }

   @Override
   public PropertyInjector createPropertyInjector(ResourceClass resourceClass, ResteasyProviderFactory providerFactory)
   {
      return new ResourcePropertyInjector(resourceClass, providerFactory);
   }

   @Override
   public MethodInjector createMethodInjector(ResourceLocator method, ResteasyProviderFactory factory)
   {
      return new MethodInjectorImpl(method, factory);
   }

   @Override
   public ValueInjector createParameterExtractor(Parameter parameter, ResteasyProviderFactory providerFactory)
   {
      switch (parameter.getParamType())
      {
         case QUERY_PARAM:
            return new QueryParamInjector(parameter.getType(), parameter.getGenericType(), parameter.getAccessibleObject(), parameter.getParamName(), parameter.getDefaultValue(), parameter.isEncoded(), parameter.getAnnotations(), providerFactory);
         case QUERY:
            return new QueryInjector(parameter.getType(), providerFactory);
         case HEADER_PARAM:
            return new HeaderParamInjector(parameter.getType(), parameter.getGenericType(), parameter.getAccessibleObject(), parameter.getParamName(), parameter.getDefaultValue(), parameter.getAnnotations(), providerFactory);
         case FORM_PARAM:
            return new FormParamInjector(parameter.getType(), parameter.getGenericType(), parameter.getAccessibleObject(), parameter.getParamName(), parameter.getDefaultValue(), parameter.isEncoded(), parameter.getAnnotations(), providerFactory);
         case COOKIE_PARAM:
            return new CookieParamInjector(parameter.getType(), parameter.getGenericType(), parameter.getAccessibleObject(), parameter.getParamName(), parameter.getDefaultValue(), parameter.getAnnotations(), providerFactory);
         case PATH_PARAM:
            return new PathParamInjector(parameter.getType(), parameter.getGenericType(), parameter.getAccessibleObject(), parameter.getParamName(), parameter.getDefaultValue(), parameter.isEncoded(), parameter.getAnnotations(), providerFactory);
         case FORM:
         {
            String prefix = parameter.getParamName();
            if (prefix.length() > 0)
            {
               if (parameter.getGenericType() instanceof ParameterizedType)
               {
                  ParameterizedType pType = (ParameterizedType) parameter.getGenericType();
                  if (Types.isA(List.class, pType))
                  {
                     return new ListFormInjector(parameter.getType(), Types.getArgumentType(pType, 0), prefix, providerFactory);
                  }
                  if (Types.isA(Map.class, pType))
                  {
                     return new MapFormInjector(parameter.getType(), Types.getArgumentType(pType, 0), Types.getArgumentType(pType, 1), prefix, providerFactory);
                  }
               }
               return new PrefixedFormInjector(parameter.getType(), prefix, providerFactory);
            }
            return new FormInjector(parameter.getType(), providerFactory);
         }
         case BEAN_PARAM:
            return new FormInjector(parameter.getType(), providerFactory);
         case MATRIX_PARAM:
            return new MatrixParamInjector(parameter.getType(), parameter.getGenericType(), parameter.getAccessibleObject(), parameter.getParamName(), parameter.getDefaultValue(), parameter.isEncoded(), parameter.getAnnotations(), providerFactory);
         case SUSPEND:
            return new SuspendInjector(parameter.getSuspendTimeout(), parameter.getType());
         case CONTEXT:
            return new ContextParameterInjector(null, parameter.getType(), providerFactory);
         case SUSPENDED:
            return new AsynchronousResponseInjector();
         case MESSAGE_BODY:
            return new MessageBodyParameterInjector(parameter.getResourceClass().getClazz(), parameter.getAccessibleObject(), parameter.getType(), parameter.getGenericType(), parameter.getAnnotations(), providerFactory);
         default:
            return null;
      }
   }


   @Override
   public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, String defaultName, Class type,
                                                 Type genericType, Annotation[] annotations, ResteasyProviderFactory providerFactory)
   {
      return createParameterExtractor(injectTargetClass, injectTarget, defaultName, type, genericType, annotations, true, providerFactory);
   }

   @Override
   public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, String defaultName, Class type, Type genericType, Annotation[] annotations, boolean useDefault, ResteasyProviderFactory providerFactory)
   {
      DefaultValue defaultValue = findAnnotation(annotations, DefaultValue.class);
      boolean encode = findAnnotation(annotations, Encoded.class) != null || injectTarget.isAnnotationPresent(Encoded.class) || type.isAnnotationPresent(Encoded.class);
      String defaultVal = null;
      if (defaultValue != null) defaultVal = defaultValue.value();

      QueryParam queryParam;
      HeaderParam header;
      MatrixParam matrix;
      PathParam uriParam;
      CookieParam cookie;
      FormParam formParam;
      Form form;
      Suspend suspend;
      Suspended suspended;
      Query query;


      if ((queryParam = findAnnotation(annotations, QueryParam.class)) != null)
      {
         return new QueryParamInjector(type, genericType, injectTarget, queryParam.value(), defaultVal, encode, annotations, providerFactory);
      }
      else if (findAnnotation(annotations, NewQueryParam.class) != null)
      {
         return new QueryParamInjector(type, genericType, injectTarget, defaultName, defaultVal, encode, annotations, providerFactory);
      }
      else if((query = findAnnotation(annotations, Query.class)) != null) {
         return new QueryInjector(type, providerFactory);
      }
      else if ((header = findAnnotation(annotations, HeaderParam.class)) != null)
      {
         return new HeaderParamInjector(type, genericType, injectTarget, header.value(), defaultVal, annotations, providerFactory);
      }
      else if (findAnnotation(annotations, NewHeaderParam.class) != null)
      {
         return new HeaderParamInjector(type, genericType, injectTarget, defaultName, defaultVal, annotations, providerFactory);
      }
      else if ((formParam = findAnnotation(annotations, FormParam.class)) != null)
      {
         return new FormParamInjector(type, genericType, injectTarget, formParam.value(), defaultVal, encode, annotations, providerFactory);
      }
      else if (findAnnotation(annotations, NewFormParam.class) != null)
      {
         return new FormParamInjector(type, genericType, injectTarget, defaultName, defaultVal, encode, annotations, providerFactory);
      }
      else if ((cookie = findAnnotation(annotations, CookieParam.class)) != null)
      {
         return new CookieParamInjector(type, genericType, injectTarget, cookie.value(), defaultVal, annotations, providerFactory);
      }
      else if (findAnnotation(annotations, NewCookieParam.class) != null)
      {
         return new CookieParamInjector(type, genericType, injectTarget, defaultName, defaultVal, annotations, providerFactory);
      }
      else if ((uriParam = findAnnotation(annotations, PathParam.class)) != null)
      {
         return new PathParamInjector(type, genericType, injectTarget, uriParam.value(), defaultVal, encode, annotations, providerFactory);
      }
      else if (findAnnotation(annotations, NewPathParam.class) != null)
      {
         return new PathParamInjector(type, genericType, injectTarget, defaultName, defaultVal, encode, annotations, providerFactory);
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
         return new MatrixParamInjector(type, genericType, injectTarget, matrix.value(), defaultVal, encode, annotations, providerFactory);
      }
      else if (findAnnotation(annotations, NewMatrixParam.class) != null)
      {
         return new MatrixParamInjector(type, genericType, injectTarget, defaultName, defaultVal, encode, annotations, providerFactory);
      }
      else if ((suspend = findAnnotation(annotations, Suspend.class)) != null)
      {
         return new SuspendInjector(suspend.value(), type);
      }
      else if (findAnnotation(annotations, Context.class) != null)
      {
         return new ContextParameterInjector(null, type, providerFactory);
      }
      else if ((suspended = findAnnotation(annotations, Suspended.class)) != null)
      {
         return new AsynchronousResponseInjector();
      }
      else if (javax.ws.rs.container.AsyncResponse.class.isAssignableFrom(type))
      {
         return new AsynchronousResponseInjector();
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
