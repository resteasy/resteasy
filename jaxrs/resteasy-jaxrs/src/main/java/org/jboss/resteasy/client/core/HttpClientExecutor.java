package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.CaseInsensitiveMap;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpClientExecutor implements ClientExecutor
{
   private HttpClient httpClient;

   public HttpClientExecutor(HttpClient httpClient)
   {
      this.httpClient = httpClient;
   }

   public static CaseInsensitiveMap<String> extractHeaders(
           HttpMethodBase baseMethod)
   {
      final CaseInsensitiveMap<String> headers = new CaseInsensitiveMap<String>();

      for (Header header : baseMethod.getResponseHeaders())
      {
         headers.add(header.getName(), header.getValue());
      }
      return headers;
   }


   public ClientResponse execute(String verb, ClientRequest request) throws Exception
   {
      String uri = request.getUri();
      HttpMethodBase httpMethod = createHttpMethod(uri, verb);
      loadHttpMethod(request, httpMethod);

      int status = httpClient.executeMethod(httpMethod);


      ClientResponseImpl response = new ClientResponseImpl();
      response.setStatus(status);
      response.setHttpMethod(httpMethod);
      response.setHeaders(extractHeaders(httpMethod));
      response.setProviderFactory(request.getProviderFactory());
      return response;
   }

   private HttpMethodBase createHttpMethod(String url, String restVerb)
   {
      if ("GET".equals(restVerb))
      {
         return new GetMethod(url);
      }
      else if ("POST".equals(restVerb))
      {
         return new PostMethod(url);
      }
      else if ("DELETE".equals(restVerb))
      {
         return new DeleteMethod(url);
      }
      else
      {
         final String verb = restVerb;
         return new PostMethod(url)
         {
            @Override
            public String getName()
            {
               return verb;
            }
         };
      }
   }

   private static class ClientRequestEntity implements RequestEntity
   {
      private byte[] bytes;
      private MediaType bodyContentType;

      public ClientRequestEntity(HttpClientHeaderWrapper wrapper, MessageBodyWriter writer, MediaType bodyContentType, Object body, Class bodyType, Type bodyGenericType, Annotation[] bodyAnnotations)
      {
         this.bodyContentType = bodyContentType;
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try
         {
            writer.writeTo(body, bodyType, bodyGenericType, bodyAnnotations, bodyContentType, wrapper, baos);
            bytes = baos.toByteArray();
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }

      }

      public boolean isRepeatable()
      {
         return true;
      }

      public void writeRequest(OutputStream outputStream) throws IOException
      {
         //wrapper.sync();
         //writer.writeTo(target, type, genericType, annotations, mediaType, wrapper, outputStream);
         // i used to have it the above way, but I don't think you can set headers once you get into this method
         outputStream.write(bytes);
      }

      public long getContentLength()
      {
         return bytes.length;
      }

      public String getContentType()
      {
         return bodyContentType.toString();
      }
   }

   public void loadHttpMethod(ClientRequest request, HttpMethodBase httpMethod) throws Exception
   {
      if (httpMethod instanceof GetMethod && request.followRedirects()) httpMethod.setFollowRedirects(true);
      else httpMethod.setFollowRedirects(false);

      if (request.getHeaders() != null)
      {
         for (Map.Entry<String, List<String>> header : request.getHeaders().entrySet())
         {
            List<String> values = header.getValue();
            for (String value : values)
            {
               httpMethod.addRequestHeader(header.getKey(), value);
            }
         }
      }
      if (request.getBody() != null && request.getFormParameters() != null)
         throw new RuntimeException("You cannot send both form parameters and an entity body");

      if (request.getFormParameters() != null)
      {
         PostMethod post = (PostMethod) httpMethod;

         for (Map.Entry<String, List<String>> formParam : request.getFormParameters().entrySet())
         {
            List<String> values = formParam.getValue();
            for (String value : values)
            {
               post.addParameter(formParam.getKey(), value);
            }
         }
      }
      if (request.getBody() != null)
      {
         MessageBodyWriter writer = request.getProviderFactory().getMessageBodyWriter(request.getBodyType(), request.getBodyGenericType(), request.getBodyAnnotations(), request.getBodyContentType());
         ClientRequestEntity requestEntity = new ClientRequestEntity(new HttpClientHeaderWrapper(httpMethod, request.getProviderFactory()), writer, request.getBodyContentType(), request.getBody(), request.getBodyType(), request.getBodyGenericType(), request.getBodyAnnotations());
         EntityEnclosingMethod post = (EntityEnclosingMethod) httpMethod;
         post.setRequestEntity(requestEntity);
      }
   }

}
