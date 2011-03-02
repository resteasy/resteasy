package org.jboss.resteasy.client.core.executors;

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
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse.BaseClientResponseStreamFactory;
import org.jboss.resteasy.client.core.SelfExpandingBufferredInputStream;
import org.jboss.resteasy.util.CaseInsensitiveMap;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApacheHttpClientExecutor implements ClientExecutor
{
   protected HttpClient httpClient;

   public ApacheHttpClientExecutor()
   {
      this.httpClient = new HttpClient();
   }

   public ApacheHttpClientExecutor(HttpClient httpClient)
   {
      this.httpClient = httpClient;
   }

   public HttpClient getHttpClient()
   {
      return httpClient;
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

   public ClientRequest createRequest(String uriTemplate)
   {
      return new ClientRequest(uriTemplate, this);
   }

   public ClientRequest createRequest(UriBuilder uriBuilder)
   {
      return new ClientRequest(uriBuilder, this);
   }


   @SuppressWarnings("unchecked")
   public ClientResponse execute(ClientRequest request) throws Exception
   {
      String uri = request.getUri();
      final HttpMethodBase httpMethod = createHttpMethod(uri, request.getHttpMethod());
      loadHttpMethod(request, httpMethod);

      int status = httpClient.executeMethod(httpMethod);

      BaseClientResponse response = new BaseClientResponse(new BaseClientResponseStreamFactory()
      {
         InputStream stream;

         public InputStream getInputStream() throws IOException
         {
            if (stream == null)
            {
               stream = new SelfExpandingBufferredInputStream(httpMethod.getResponseBodyAsStream());
            }
            return stream;
         }

         public void performReleaseConnection()
         {
            try
            {
               httpMethod.releaseConnection();
            }
            catch (Exception ignored)
            {}
            try
            {
               stream.close();
            }
            catch (Exception ignored)
            {}
         }
      }, this);
      response.setStatus(status);
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
      private String contentType;

      private ClientRequestEntity(String contentType, byte[] bytes)
      {
         this.contentType = contentType;
         this.bytes = bytes;
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
         return contentType;
      }
   }

   public void loadHttpMethod(ClientRequest request, HttpMethodBase httpMethod) throws Exception
   {
      if (httpMethod instanceof GetMethod && request.followRedirects()) httpMethod.setFollowRedirects(true);
      else httpMethod.setFollowRedirects(false);

      if (request.getBody() != null && !request.getFormParameters().isEmpty())
         throw new RuntimeException("You cannot send both form parameters and an entity body");

      if (!request.getFormParameters().isEmpty())
      {
         commitHeaders(request, httpMethod);
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
      else if (request.getBody() != null)
      {
         if (!(httpMethod instanceof EntityEnclosingMethod))
            throw new RuntimeException("A GET request cannot have a body.");
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         request.writeRequestBody(request.getHeadersAsObjects(), baos);
         commitHeaders(request, httpMethod);
         ClientRequestEntity requestEntity = new ClientRequestEntity(request.getBodyContentType().toString(), baos.toByteArray());
         EntityEnclosingMethod post = (EntityEnclosingMethod) httpMethod;
         post.setRequestEntity(requestEntity);
      }
      else
      {
         commitHeaders(request, httpMethod);
      }
   }

   public void commitHeaders(ClientRequest request, HttpMethodBase httpMethod)
   {
      MultivaluedMap<String, String> headers = request.getHeaders();
      for (Map.Entry<String, List<String>> header : headers.entrySet())
      {
         List<String> values = header.getValue();
         for (String value : values)
         {
//               System.out.println(String.format("setting %s = %s", header.getKey(), value));
            httpMethod.addRequestHeader(header.getKey(), value);
         }
      }
   }


}
