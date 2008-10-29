package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.MediaTypeHelper;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.CookieParam;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
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
   protected HttpClient client;

   public ClientInvoker(Class<?> declaring, Method method, ResteasyProviderFactory providerFactory, HttpClient client)
   {
      this.declaring = declaring;
      this.method = method;
      this.providerFactory = providerFactory;
      params = new Marshaller[method.getParameterTypes().length];
      for (int i = 0; i < method.getParameterTypes().length; i++)
      {
         Class type = method.getParameterTypes()[i];
         Annotation[] annotations = method.getParameterAnnotations()[i];
         Type genericType = method.getGenericParameterTypes()[i];
         AccessibleObject target = method;

         Marshaller marshaller = createMarshaller(declaring, providerFactory, type, annotations, genericType, target, false);

         params[i] = marshaller;
      }
      accepts = MediaTypeHelper.getProduces(declaring, method);
      this.client = client;
   }

   public static Marshaller createMarshaller(Class<?> declaring, ResteasyProviderFactory providerFactory, Class type, Annotation[] annotations, Type genericType, AccessibleObject target, boolean ignoreBody)
   {
      Marshaller marshaller = null;

      QueryParam query;
      HeaderParam header;
      MatrixParam matrix;
      PathParam uriParam;
      CookieParam cookie;
      FormParam formParam;
      Form form;

      boolean isEncoded = FindAnnotation.findAnnotation(annotations, Encoded.class) != null;

      if ((query = FindAnnotation.findAnnotation(annotations, QueryParam.class)) != null)
      {
         marshaller = new QueryParamMarshaller(query.value(), providerFactory);
      }
      else if ((header = FindAnnotation.findAnnotation(annotations, HeaderParam.class)) != null)
      {
         marshaller = new HeaderParamMarshaller(header.value(), providerFactory);
      }
      else if ((cookie = FindAnnotation.findAnnotation(annotations, CookieParam.class)) != null)
      {
         marshaller = new CookieParamMarshaller(cookie.value());
      }
      else if ((uriParam = FindAnnotation.findAnnotation(annotations, PathParam.class)) != null)
      {
         marshaller = new PathParamMarshaller(uriParam.value(), isEncoded, providerFactory);
      }
      else if ((matrix = FindAnnotation.findAnnotation(annotations, MatrixParam.class)) != null)
      {
         marshaller = new MatrixParamMarshaller(matrix.value(), providerFactory);
      }
      else if ((formParam = FindAnnotation.findAnnotation(annotations, FormParam.class)) != null)
      {
         marshaller = new FormParamMarshaller(formParam.value(), providerFactory);
      }
      else if ((form = FindAnnotation.findAnnotation(annotations, Form.class)) != null)
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
            throw new RuntimeException("You must define a @ConsumeMime type on your client method or interface");
         }
         marshaller = new MessageBodyParameterMarshaller(mediaType, type, genericType, annotations, providerFactory);
      }
      return marshaller;
   }

   public void setBaseUri(URI uri)
   {
      builder = new UriBuilderImpl();
      builder.uri(uri);
      builder.path(declaring);
      builder.path(method);
   }

   protected void checkFailureStatus(HttpMethodBase baseMethod, int status)
   {
      if (status > 399 && status < 599)
      {
         throw new ClientResponseFailure("Error status " + status + " " + Response.Status.fromStatusCode(status) + " returned", createGenericClientResponse(baseMethod, status));
      }
   }

   public Object invoke(Object[] args)
   {
      boolean isProvidersSet = ResteasyProviderFactory.getContextData(Providers.class) != null;
      if (!isProvidersSet) ResteasyProviderFactory.pushContext(Providers.class, providerFactory);

      try
      {
         if (builder == null) throw new RuntimeException("You have not set a base URI for the client proxy");

         UriBuilderImpl uri = (UriBuilderImpl) builder.clone();

         if (args != null)
         {
            for (int i = 0; i < args.length; i++)
            {
               params[i].buildUri(args[i], uri);
            }
         }

         String url = null;
         HttpMethodBase baseMethod = null;
         try
         {
            url = uri.build().toURL().toString();
            baseMethod = createBaseMethod(url);
            if (ClientResponse.class.isAssignableFrom(method.getReturnType())) baseMethod.setFollowRedirects(false);
         }
         catch (MalformedURLException e)
         {
            throw new RuntimeException("Unable to build URL from uri", e);
         }

         if (accepts != null)
         {
            baseMethod.setRequestHeader(HttpHeaderNames.ACCEPT, accepts.toString());
         }

         if (args != null)
         {
            for (int i = 0; i < args.length; i++)
            {
               params[i].setHeaders(args[i], baseMethod);
            }
            for (int i = 0; i < args.length; i++)
            {
               params[i].buildRequest(args[i], baseMethod);
            }
         }

         int status = 0;
         try
         {
            status = client.executeMethod(baseMethod);
         }
         catch (IOException e)
         {
            throw new RuntimeException("Failed to execute GET request: " + url, e);
         }

         try
         {
            if (method.getReturnType().equals(Response.Status.class))
            {
               return Response.Status.fromStatusCode(status);
            }
            if (ClientResponse.class.isAssignableFrom(method.getReturnType()))
            {
               Type genericReturnType = null;
               Class returnType = null;
               if (method.getGenericReturnType() instanceof ParameterizedType)
               {
                  ParameterizedType zType = (ParameterizedType) method.getGenericReturnType();
                  genericReturnType = zType.getActualTypeArguments()[0];
                  returnType = Types.getRawType(genericReturnType);

               }
               if (returnType == null) return createGenericClientResponse(baseMethod, status);
               checkFailureStatus(baseMethod, status);
               return extractClientResponse(baseMethod, status, genericReturnType, returnType);
            }
            else if (method.getReturnType() != null && !method.getReturnType().equals(void.class))
            {
               checkFailureStatus(baseMethod, status);
               return extractClientResponse(baseMethod, status, method.getGenericReturnType(), method.getReturnType()).getEntity();
            }
            else
            {
               checkFailureStatus(baseMethod, status);
               return null;
            }

         }
         finally
         {
            // todo better semantics/api for handling keep alive and such
            baseMethod.releaseConnection();
         }
      }
      finally
      {
         if (!isProvidersSet) ResteasyProviderFactory.popContextData(Providers.class);

      }
   }

   public abstract HttpMethodBase createBaseMethod(String uri);

   protected ClientResponse<byte[]> createGenericClientResponse(final HttpMethodBase baseMethod, final int status)
   {
      final CaseInsensitiveMap<String> headers = new CaseInsensitiveMap<String>();


      for (Header header : baseMethod.getResponseHeaders())
      {
         headers.add(header.getName(), header.getValue());
      }

      return new ClientResponse<byte[]>()
      {
         public byte[] getEntity()
         {
            try
            {
               return baseMethod.getResponseBody();
            }
            catch (IOException e)
            {
               throw new RuntimeException(e);
            }
         }

         public MultivaluedMap<String, String> getHeaders()
         {
            return headers;
         }

         public int getStatus()
         {
            return status;
         }
      };

   }

   protected ClientResponse extractClientResponse(HttpMethodBase baseMethod, final int status, Type genericReturnType, Class returnType)
   {
      final CaseInsensitiveMap<String> headers = new CaseInsensitiveMap<String>();


      for (Header header : baseMethod.getResponseHeaders())
      {
         headers.add(header.getName(), header.getValue());
      }

      Header contentType = baseMethod.getResponseHeader(HttpHeaderNames.CONTENT_TYPE);
      if (contentType == null)
      {
         ClientResponse response = createGenericClientResponse(baseMethod, status);
         throw new ClientResponseFailure("No Content-Type header specified", response);
      }


      String mediaType = baseMethod.getResponseHeader(HttpHeaderNames.CONTENT_TYPE).getValue();
      if (mediaType == null)
      {
         Produces produce = method.getAnnotation(Produces.class);
         if (produce == null) produce = (Produces) declaring.getAnnotation(Produces.class);
         if (produce == null)
         {
            ClientResponse response = createGenericClientResponse(baseMethod, status);
            throw new ClientResponseFailure("@Produces on your proxy method, " + method.toString() + ", is required", response);
         }
         mediaType = produce.value()[0];
      }
      if (returnType == null)
      {
         returnType = byte[].class;
      }
      MediaType media = MediaType.valueOf(mediaType);
      MessageBodyReader reader = providerFactory.getMessageBodyReader(returnType, genericReturnType, method.getAnnotations(), media);
      if (reader == null)
      {
         ClientResponse response = createGenericClientResponse(baseMethod, status);
         throw new ClientResponseFailure("Unable to find a MessageBodyReader of content-type " + mediaType + " for response of " + method.toString(), response);
      }
      try
      {
         final Object response = reader.readFrom(returnType, genericReturnType, method.getAnnotations(), media, headers, baseMethod.getResponseBodyAsStream());
         return new ClientResponse()
         {
            public Object getEntity()
            {
               return response;
            }

            public MultivaluedMap getHeaders()
            {
               return headers;
            }

            public int getStatus()
            {
               return status;
            }
         };

      }
      catch (final IOException e)
      {
         return new ClientResponse()
         {
            public Object getEntity()
            {
               throw new RuntimeException("Unable to unmarshall response for " + method.toString(), e);
            }

            public MultivaluedMap getHeaders()
            {
               return headers;
            }

            public int getStatus()
            {
               return status;
            }
         };
      }
   }
}