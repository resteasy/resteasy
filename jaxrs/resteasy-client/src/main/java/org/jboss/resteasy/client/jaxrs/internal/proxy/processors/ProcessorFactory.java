package org.jboss.resteasy.client.jaxrs.internal.proxy.processors;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.client.ClientURI;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation.CookieParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation.FormParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation.HeaderParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation.MessageBodyParameterProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation.URIParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget.MatrixParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget.PathParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget.QueryParamProcessor;
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

public class ProcessorFactory
{

	public static Object[] createProcessors(Class declaringClass, Method method, ClientConfiguration configuration)
	{
		return createProcessors(declaringClass, method, configuration, null);
	}
	
	public static Object[] createProcessors(Class declaringClass, Method method, ClientConfiguration configuration, MediaType defaultConsumes)
	{
      Object[] params = new Object[method.getParameterTypes().length];
      for (int i = 0; i < method.getParameterTypes().length; i++)
      {
         Class<?> type = method.getParameterTypes()[i];
         Annotation[] annotations = method.getParameterAnnotations()[i];
         Type genericType = method.getGenericParameterTypes()[i];
         AccessibleObject target = method;
         params[i] = ProcessorFactory.createProcessor(declaringClass, configuration, type, annotations, genericType, target, defaultConsumes, false);
      }
      return params;
   }

	   public static Object createProcessor(Class<?> declaring,
                                               ClientConfiguration configuration, Class<?> type,
                                               Annotation[] annotations, Type genericType, AccessibleObject target,
                                               boolean ignoreBody)
	   {
		   return createProcessor(declaring, configuration, type, annotations, genericType, target, null, ignoreBody);
	   }
	   
	   public static Object createProcessor(Class<?> declaring,
                                               ClientConfiguration configuration, Class<?> type,
                                               Annotation[] annotations, Type genericType, AccessibleObject target, MediaType defaultConsumes,
                                               boolean ignoreBody)
   {
      Object processor = null;

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
         processor = new QueryParamProcessor(query.value());
      }
      else if ((header = FindAnnotation.findAnnotation(annotations,
              HeaderParam.class)) != null)
      {
         processor = new HeaderParamProcessor(header.value());
      }
      else if ((cookie = FindAnnotation.findAnnotation(annotations,
              CookieParam.class)) != null)
      {
         processor = new CookieParamProcessor(cookie.value());
      }
      else if ((uriParam = FindAnnotation.findAnnotation(annotations,
              PathParam.class)) != null)
      {
         processor = new PathParamProcessor(uriParam.value());
      }
      else if ((matrix = FindAnnotation.findAnnotation(annotations,
              MatrixParam.class)) != null)
      {
         processor = new MatrixParamProcessor(matrix.value());
      }
      else if ((formParam = FindAnnotation.findAnnotation(annotations,
              FormParam.class)) != null)
      {
         processor = new FormParamProcessor(formParam.value());
      }
      else if ((/* form = */FindAnnotation.findAnnotation(annotations,
              Form.class)) != null)
      {
         processor = new FormProcessor(type, configuration);
      }
      else if ((/* form = */FindAnnotation.findAnnotation(annotations,
              BeanParam.class)) != null)
      {
         processor = new FormProcessor(type, configuration);
      }
      else if ((FindAnnotation.findAnnotation(annotations,
              Context.class)) != null)
      {
         processor = null;
      }
      else if (type.equals(Cookie.class))
      {
         processor = new CookieParamProcessor(null);
      }
      // this is for HATEAOS clients
      else if (FindAnnotation.findAnnotation(annotations, ClientURI.class) != null)
      {
         processor = new URIParamProcessor();
      }
      else if (!ignoreBody)
      {
         MediaType mediaType = MediaTypeHelper.getConsumes(declaring, target);
         if(mediaType == null)
        	 mediaType = defaultConsumes;
         if (mediaType == null)
         {
            throw new RuntimeException(
                    "You must define a @Consumes type on your client method or interface, or supply a default");
         }
         processor = new MessageBodyParameterProcessor(mediaType, type,
                 genericType, annotations);
      }
      return processor;
   }
}
