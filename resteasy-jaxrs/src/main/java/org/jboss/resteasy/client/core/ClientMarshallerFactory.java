package org.jboss.resteasy.client.core;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.MediaTypeHelper;

import javax.ws.rs.CookieParam;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ClientMarshallerFactory
{

   public static Marshaller[] createMarshallers(Method method, ResteasyProviderFactory providerFactory)
   {
      Class<?> declaringClass = method.getDeclaringClass();
      Marshaller[] params = new Marshaller[method.getParameterTypes().length];
      for (int i = 0; i < method.getParameterTypes().length; i++)
      {
         Class<?> type = method.getParameterTypes()[i];
         Annotation[] annotations = method.getParameterAnnotations()[i];
         Type genericType = method.getGenericParameterTypes()[i];
         AccessibleObject target = method;
         params[i] = ClientMarshallerFactory.createMarshaller(declaringClass, providerFactory, type, annotations, genericType, target, false);
      }
      return params;
   }

   public static Marshaller createMarshaller(Class<?> declaring,
                                             ResteasyProviderFactory providerFactory, Class<?> type,
                                             Annotation[] annotations, Type genericType, AccessibleObject target,
                                             boolean ignoreBody)
   {
      Marshaller marshaller = null;

      QueryParam query;
      HeaderParam header;
      MatrixParam matrix;
      PathParam uriParam;
      CookieParam cookie;
      FormParam formParam;
      // Form form;

      boolean isEncoded = FindAnnotation.findAnnotation(annotations,
              Encoded.class) != null;

      if ((query = FindAnnotation.findAnnotation(annotations, QueryParam.class)) != null)
      {
         marshaller = new QueryParamMarshaller(query.value());
      }
      else if ((header = FindAnnotation.findAnnotation(annotations,
              HeaderParam.class)) != null)
      {
         marshaller = new HeaderParamMarshaller(header.value());
      }
      else if ((cookie = FindAnnotation.findAnnotation(annotations,
              CookieParam.class)) != null)
      {
         marshaller = new CookieParamMarshaller(cookie.value());
      }
      else if ((uriParam = FindAnnotation.findAnnotation(annotations,
              PathParam.class)) != null)
      {
         marshaller = new PathParamMarshaller(uriParam.value(), isEncoded,
                 providerFactory);
      }
      else if ((matrix = FindAnnotation.findAnnotation(annotations,
              MatrixParam.class)) != null)
      {
         marshaller = new MatrixParamMarshaller(matrix.value());
      }
      else if ((formParam = FindAnnotation.findAnnotation(annotations,
              FormParam.class)) != null)
      {
         marshaller = new FormParamMarshaller(formParam.value());
      }
      else if ((/* form = */FindAnnotation.findAnnotation(annotations,
              Form.class)) != null)
      {
         marshaller = new FormMarshaller(type, providerFactory);
      }
      else if (type.equals(Cookie.class))
      {
         marshaller = new CookieParamMarshaller(null);
      }
      else if (!ignoreBody)
      {
         MediaType mediaType = MediaTypeHelper.getConsumes(declaring, target);
         if (mediaType == null)
         {
            throw new RuntimeException(
                    "You must define a @ConsumeMime type on your client method or interface");
         }
         marshaller = new MessageBodyParameterMarshaller(mediaType, type,
                 genericType, annotations);
      }
      return marshaller;
   }
}
