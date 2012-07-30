package org.jboss.resteasy.client.jaxrs.internal.engines;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.jboss.resteasy.client.core.SelfExpandingBufferredInputStream;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.util.CaseInsensitiveMap;

import javax.ws.rs.MessageProcessingException;
import javax.ws.rs.client.ClientException;
import javax.ws.rs.client.Configuration;
import javax.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApacheHttpClient4Engine implements ClientHttpEngine
{
   protected HttpClient httpClient;
   protected boolean createdHttpClient;
   protected HttpContext httpContext;
   protected boolean closed;


   public ApacheHttpClient4Engine()
   {
      this.httpClient = new DefaultHttpClient();
      this.createdHttpClient = true;
   }

   public ApacheHttpClient4Engine(Configuration configuration)
   {
      this.httpClient = new DefaultHttpClient();
      this.createdHttpClient = false;
   }

   public ApacheHttpClient4Engine(HttpClient httpClient)
   {
      this.httpClient = httpClient;
   }

   public ApacheHttpClient4Engine(HttpClient httpClient, HttpContext httpContext)
   {
      this.httpClient = httpClient;
      this.httpContext = httpContext;
   }

   public HttpClient getHttpClient()
   {
      return httpClient;
   }

   public HttpContext getHttpContext()
   {
      return httpContext;
   }

   public void setHttpContext(HttpContext httpContext)
   {
      this.httpContext = httpContext;
   }

   public static CaseInsensitiveMap<String> extractHeaders(
           HttpResponse response)
   {
      final CaseInsensitiveMap<String> headers = new CaseInsensitiveMap<String>();

      for (Header header : response.getAllHeaders())
      {
         headers.add(header.getName(), header.getValue());
      }
      return headers;
   }

   @SuppressWarnings("unchecked")
   public ClientResponse invoke(ClientInvocation request) throws ClientException
   {
      String uri = request.getUri().toString();
      final HttpRequestBase httpMethod = createHttpMethod(uri, request.getMethod());
      final HttpResponse res;
      try
      {
         loadHttpMethod(request, httpMethod);

         res = httpClient.execute(httpMethod, httpContext);
      }
      catch (Exception e)
      {
         throw new ClientException("Unable to invoke request", e);
      }

      ClientResponse response = new ClientResponse()
      {
         InputStream stream;

         @Override
         protected void setInputStream(InputStream is)
         {
            stream = is;
         }

         public InputStream getInputStream()
         {
            if (stream == null)
            {
               HttpEntity entity = res.getEntity();
               if (entity == null) return null;
               try
               {
                  stream = new SelfExpandingBufferredInputStream(entity.getContent());
               }
               catch (IOException e)
               {
                  throw new RuntimeException(e);
               }
            }
            return stream;
         }

         public void releaseConnection()
         {
            isClosed = true;
            // Apache Client 4 is stupid,  You have to get the InputStream and close it if there is an entity
            // otherwise the connection is never released.  There is, of course, no close() method on response
            // to make this easier.
            try
            {
               if (stream != null)
               {
                  stream.close();
               }
               else
               {
                  InputStream is = getInputStream();
                  if (is != null)
                  {
                     is.close();
                  }
               }
            }
            catch (Exception ignore)
            {
            }
         }
      };
      response.setProperties(request.getMutableProperties());
      response.setStatus(res.getStatusLine().getStatusCode());
      response.setHeaders(extractHeaders(res));
      response.setConfiguration(request.getConfiguration());
      return response;
   }

   private HttpRequestBase createHttpMethod(String url, String restVerb)
   {
      if ("GET".equals(restVerb))
      {
         return new HttpGet(url);
      }
      else if ("POST".equals(restVerb))
      {
         return new HttpPost(url);
      }
      else
      {
         final String verb = restVerb;
         return new HttpPost(url)
         {
            @Override
            public String getMethod()
            {
               return verb;
            }
         };
      }
   }

   public void loadHttpMethod(final ClientInvocation request, HttpRequestBase httpMethod) throws Exception
   {
      if (httpMethod instanceof HttpGet && false) // todo  && request.followRedirects())
      {
         HttpClientParams.setRedirecting(httpMethod.getParams(), true);
      }
      else
      {
         HttpClientParams.setRedirecting(httpMethod.getParams(), false);
      }

      if (request.getEntity() != null)
      {
         if (httpMethod instanceof HttpGet) throw new MessageProcessingException("A GET request cannot have a body.");

         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         request.getDelegatingOutputStream().setDelegate(baos);
         try
         {
            request.writeRequestBody(request.getEntityStream());
            ByteArrayEntity entity = new ByteArrayEntity(baos.toByteArray())
            {
               @Override
               public Header getContentType()
               {
                  return new BasicHeader("Content-Type", request.getHeaders().getMediaType().toString());
               }
            };
            HttpPost post = (HttpPost) httpMethod;
            commitHeaders(request, httpMethod);
            post.setEntity(entity);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      else // no body
      {
         commitHeaders(request, httpMethod);
      }
   }

   public void commitHeaders(ClientInvocation request, HttpRequestBase httpMethod)
   {
      MultivaluedMap<String, String> headers = request.getHeaders().asMap();
      for (Map.Entry<String, List<String>> header : headers.entrySet())
      {
         List<String> values = header.getValue();
         for (String value : values)
         {
//               System.out.println(String.format("setting %s = %s", header.getKey(), value));
            httpMethod.addHeader(header.getKey(), value);
         }
      }
   }

   public void close()
   {
      if (closed)
         return;

      if (createdHttpClient && httpClient != null)
      {
         ClientConnectionManager manager = httpClient.getConnectionManager();
         if (manager != null)
         {
            manager.shutdown();
         }
      }
      closed = true;
   }

   public boolean isClosed()
   {
      return closed;
   }

   public void finalize() throws Throwable
   {
      close();
      super.finalize();
   }
}