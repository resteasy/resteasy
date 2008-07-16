package org.jboss.resteasy.plugins.client.httpclient;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.List;

import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.jboss.resteasy.core.ClientInvoker;
import org.jboss.resteasy.core.Marshaller;
import org.jboss.resteasy.core.MessageBodyParameterMarshaller;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ClientHttpOutput;
import org.jboss.resteasy.spi.ClientResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.Types;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
abstract public class HttpClientInvoker extends ClientInvoker
{
   private HttpClient client;

   public HttpClientInvoker(HttpClient client, Class<?> declaring, Method method, ResteasyProviderFactory providerFactory)
   {
      super(declaring, method, providerFactory);
      this.client = client;
   }

   public abstract HttpMethodBase createBaseMethod(String uri);

   public Object invoke(Object[] args)
   {
      if (builder == null) throw new RuntimeException("You have not set a base URI for the client proxy");
      ClientHttpOutput output = new HttpOutputMessage(null);

      UriBuilderImpl uri = (UriBuilderImpl) builder.clone();

      int i = 0;
      BodyRequestEntity body = null;
      for (Marshaller param : params)
      {
         if (param instanceof MessageBodyParameterMarshaller)
         {
            MessageBodyParameterMarshaller bodyMarshaller = (MessageBodyParameterMarshaller) param;
            body = new BodyRequestEntity(args[i], method.getGenericParameterTypes()[i], method.getParameterAnnotations()[i], bodyMarshaller, output.getOutputHeaders());
         }
         else param.marshall(args[i], uri, output);
         i++;
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

      for (String key : output.getOutputHeaders().keySet())
      {
         List<Object> value = output.getOutputHeaders().get(key);
         for (Object obj : value)
         {
            baseMethod.addRequestHeader(key, obj.toString());
         }
      }

      if (accepts != null)
      {
         baseMethod.setRequestHeader(HttpHeaderNames.ACCEPT, accepts.toString());
      }

      if (body != null) ((EntityEnclosingMethod) baseMethod).setRequestEntity(body);

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

            return extractClientResponse(baseMethod, status, genericReturnType, returnType);
         }
         else if (method.getReturnType() != null && !method.getReturnType().equals(void.class))
         {
            return extractClientResponse(baseMethod, status, method.getGenericReturnType(), method.getReturnType()).getEntity();
         }
         else
         {
            return null;
         }

      }
      finally
      {
         // todo better semantics/api for handling keep alive and such
         baseMethod.releaseConnection();
      }
   }

   protected ClientResponse extractClientResponse(HttpMethodBase baseMethod, int status, Type genericReturnType, Class returnType)
   {
      final CaseInsensitiveMap<String> headers = new CaseInsensitiveMap<String>();
      final int theStatus = status;


      for (Header header : baseMethod.getResponseHeaders())
      {
         headers.add(header.getName(), header.getValue());
      }

      Header contentType = baseMethod.getResponseHeader(HttpHeaderNames.CONTENT_TYPE);
      if (contentType == null)
      {
         return new ClientResponse()
         {
            public Object getEntity()
            {
               return null;
            }

            public MultivaluedMap getHeaders()
            {
               return headers;
            }

            public int getStatus()
            {
               return theStatus;
            }
         };
      }


      String mediaType = baseMethod.getResponseHeader(HttpHeaderNames.CONTENT_TYPE).getValue();
      if (mediaType == null)
      {
         ProduceMime produce = method.getAnnotation(ProduceMime.class);
         if (produce == null) produce = (ProduceMime) declaring.getAnnotation(ProduceMime.class);
         if (produce == null)
         {
            return new ClientResponse()
            {
               public Object getEntity()
               {
                  throw new RuntimeException("Unable to determine content type of response for " + method.toString());
               }

               public MultivaluedMap getHeaders()
               {
                  return headers;
               }

               public int getStatus()
               {
                  return theStatus;
               }
            };
         }
         mediaType = produce.value()[0];
      }
      if (returnType == null)
      {
         returnType = byte[].class;
      }
      MediaType media = MediaType.valueOf(mediaType);
      MessageBodyReader reader = providerFactory.createMessageBodyReader(returnType, genericReturnType, method.getAnnotations(), media);
      if (reader == null)
      {
         final String theMediaType = mediaType;
         return new ClientResponse()
         {
            public Object getEntity()
            {
               throw new RuntimeException("Unable to find a MessageBodyReader of content-type " + theMediaType + " for response of " + method.toString());
            }

            public MultivaluedMap getHeaders()
            {
               return headers;
            }

            public int getStatus()
            {
               return theStatus;
            }
         };
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
               return theStatus;
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
               return theStatus;
            }
         };
      }
   }

}
