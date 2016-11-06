package org.jboss.resteasy.client.core.marshallers;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.client.ClientURI;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.MediaTypeHelper;

import javax.ws.rs.BeanParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ClientMarshallerFactory
{

	public static Marshaller[] createMarshallers(Class declaringClass, Method method, ResteasyProviderFactory providerFactory)
	{
		return createMarshallers(declaringClass, method, providerFactory, null);
	}
	
	public static Marshaller[] createMarshallers(Class declaringClass, Method method, ResteasyProviderFactory providerFactory, MediaType defaultConsumes)
	{
      Marshaller[] params = new Marshaller[method.getParameterTypes().length];
      for (int i = 0; i < method.getParameterTypes().length; i++)
      {
         Class<?> type = method.getParameterTypes()[i];
         Annotation[] annotations = method.getParameterAnnotations()[i];
         Type genericType = method.getGenericParameterTypes()[i];
         AccessibleObject target = method;
         params[i] = ClientMarshallerFactory.createMarshaller(declaringClass, providerFactory, type, annotations, genericType, target, defaultConsumes, false);
      }
      return params;
   }

	   public static Marshaller createMarshaller(Class<?> declaring,
               ResteasyProviderFactory providerFactory, Class<?> type,
               Annotation[] annotations, Type genericType, AccessibleObject target,
               boolean ignoreBody)
	   {
		   return createMarshaller(declaring, providerFactory, type, annotations, genericType, target, null, ignoreBody);
	   }
	   
	   public static Marshaller createMarshaller(Class<?> declaring,
               ResteasyProviderFactory providerFactory, Class<?> type,
               Annotation[] annotations, Type genericType, AccessibleObject target, MediaType defaultConsumes,
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
      else if ((/* form = */FindAnnotation.findAnnotation(annotations,
              BeanParam.class)) != null)
      {
         marshaller = new FormMarshaller(type, providerFactory);
      }
      else if ((FindAnnotation.findAnnotation(annotations,
              Context.class)) != null)
      {
         marshaller = new NOOPMarshaller();
      }
      else if (type.equals(Cookie.class))
      {
         marshaller = new CookieParamMarshaller(null);
      }
      // this is for HATEAOS clients
      else if (FindAnnotation.findAnnotation(annotations, ClientURI.class) != null
            || FindAnnotation.findAnnotation(annotations, org.jboss.resteasy.annotations.ClientURI.class) != null)
      {
         marshaller = new URIParamMarshaller();
      }
      else if (!ignoreBody)
      {
         MediaType mediaType = MediaTypeHelper.getConsumes(declaring, target);
         if(mediaType == null)
        	 mediaType = defaultConsumes;
         if (mediaType == null)
         {
            throw new RuntimeException(Messages.MESSAGES.mustDefineConsumes());
         }
         marshaller = new MessageBodyParameterMarshaller(mediaType, type,
                 genericType, annotations);
      }
      return marshaller;
   }
}
