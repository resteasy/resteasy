package org.resteasy.plugins.client.httpclient;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.resteasy.ClientInvoker;
import org.resteasy.Marshaller;
import org.resteasy.MessageBodyParameterMarshaller;
import org.resteasy.specimpl.MultivaluedMapImpl;
import org.resteasy.specimpl.UriBuilderImpl;
import org.resteasy.spi.ClientHttpOutput;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.HttpHeaderNames;
import org.resteasy.util.HttpResponseCodes;

import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.List;

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

      try
      {
         int status = client.executeMethod(baseMethod);
         if (status != HttpResponseCodes.SC_OK)
         {
            // todo better semantics/api for handling keep alive and such
            baseMethod.releaseConnection();
            throw new RuntimeException("Execution of Get " + url + " was unsuccessful with status code: " + status);
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException("Failed to execute GET request: " + url, e);
      }

      try
      {
         if (method.getReturnType() != null && !method.getReturnType().equals(void.class))
         {
            Header contentType = baseMethod.getResponseHeader(HttpHeaderNames.CONTENT_TYPE);
            if (contentType == null)
            {
               throw new RuntimeException("Unable to determine content type of resource: " + url);
            }
            String mediaType = baseMethod.getResponseHeader(HttpHeaderNames.CONTENT_TYPE).getValue();
            if (mediaType == null)
            {
               ProduceMime produce = method.getAnnotation(ProduceMime.class);
               if (produce == null) produce = (ProduceMime) declaring.getAnnotation(ProduceMime.class);
               if (produce == null)
                  throw new RuntimeException("Unable to determine content type of response for GET " + url);
               mediaType = produce.value()[0];
            }
            MediaType media = MediaType.valueOf(mediaType);
            MessageBodyReader reader = providerFactory.createMessageBodyReader(method.getReturnType(), method.getGenericReturnType(), method.getAnnotations(), media);
            if (reader == null)
               throw new RuntimeException("Unable to find a message body reader for GET " + url + " content-type: " + mediaType);
            MultivaluedMap<String, String> responseHeaders = new MultivaluedMapImpl<String, String>();
            for (Header header : baseMethod.getResponseHeaders())
            {
               responseHeaders.add(header.getName(), header.getValue());
            }
            try
            {
               return reader.readFrom(method.getReturnType(), method.getGenericReturnType(), method.getAnnotations(), media, responseHeaders, baseMethod.getResponseBodyAsStream());

            }
            catch (IOException e)
            {
               throw new RuntimeException("Unable to unmarshall response from GET " + url + " content-type: " + mediaType, e);
            }
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
}
