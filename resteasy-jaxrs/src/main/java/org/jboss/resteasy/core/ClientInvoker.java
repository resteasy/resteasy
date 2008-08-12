package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.MediaTypeHelper;

import javax.ws.rs.CookieParam;
import javax.ws.rs.Encoded;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
abstract public class ClientInvoker
{
   protected ResteasyProviderFactory providerFactory;
   protected Method method;
   protected Marshaller[] params;
   protected UriBuilderImpl builder;
   protected Class declaring;
   protected MediaType accepts;

   public ClientInvoker(Class<?> declaring, Method method, ResteasyProviderFactory providerFactory)
   {
      this.declaring = declaring;
      this.method = method;
      this.providerFactory = providerFactory;
      params = new Marshaller[method.getParameterTypes().length];
      for (int i = 0; i < method.getParameterTypes().length; i++)
      {
         Class type = method.getParameterTypes()[i];
         Annotation[] annotations = method.getParameterAnnotations()[i];

         QueryParam query;
         HeaderParam header;
         MatrixParam matrix;
         PathParam uriParam;
         CookieParam cookie;

         boolean isEncoded = FindAnnotation.findAnnotation(annotations, Encoded.class) != null;

         if ((query = FindAnnotation.findAnnotation(annotations, QueryParam.class)) != null)
         {
            params[i] = new QueryParamMarshaller(query.value());
         }
         else if ((header = FindAnnotation.findAnnotation(annotations, HeaderParam.class)) != null)
         {
            params[i] = new HeaderParamMarshaller(header.value());
         }
         else if ((cookie = FindAnnotation.findAnnotation(annotations, CookieParam.class)) != null)
         {
            params[i] = new CookieParamMarshaller(cookie.value());
         }
         else if ((uriParam = FindAnnotation.findAnnotation(annotations, PathParam.class)) != null)
         {
            params[i] = new PathParamMarshaller(uriParam.value(), isEncoded);
         }
         else if ((matrix = FindAnnotation.findAnnotation(annotations, MatrixParam.class)) != null)
         {
            params[i] = new MatrixParamMarshaller(matrix.value());
         }
         else if (type.equals(Cookie.class))
         {
            params[i] = new CookieParamMarshaller(null);
         }
         else
         {
            MediaType mediaType = MediaTypeHelper.getConsumes(declaring, method);
            if (mediaType == null) mediaType = determineMediaType();
            params[i] = new MessageBodyParameterMarshaller(mediaType, type, method.getGenericParameterTypes()[i], method.getParameterAnnotations()[i], providerFactory);
         }
      }
      accepts = MediaTypeHelper.getProduces(declaring, method);
   }

   public void setBaseUri(URI uri)
   {
      builder = new UriBuilderImpl();
      builder.uri(uri);
      builder.path(declaring);
      builder.path(method);
   }

   public MediaType determineMediaType()
   {
      throw new RuntimeException("You must define a @ConsumeMime type.  In the future we will");
   }

   public abstract Object invoke(Object[] args);

}