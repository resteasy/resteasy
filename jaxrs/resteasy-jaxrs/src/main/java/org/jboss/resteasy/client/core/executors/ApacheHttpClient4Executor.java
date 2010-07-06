package org.jboss.resteasy.client.core.executors;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse.BaseClientResponseStreamFactory;
import org.jboss.resteasy.client.core.SelfExpandingBufferredInputStream;
import org.jboss.resteasy.util.CaseInsensitiveMap;

import javax.ws.rs.core.UriBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApacheHttpClient4Executor implements ClientExecutor
{
   protected HttpClient httpClient;

   public ApacheHttpClient4Executor()
   {
      this.httpClient = new DefaultHttpClient();
   }

   public ApacheHttpClient4Executor(HttpClient httpClient)
   {
      this.httpClient = httpClient;
   }

   public HttpClient getHttpClient()
   {
      return httpClient;
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
      final HttpRequestBase httpMethod = createHttpMethod(uri, request.getHttpMethod());
      loadHttpMethod(request, httpMethod);

      final HttpResponse res = httpClient.execute(httpMethod);

      BaseClientResponse response = new BaseClientResponse(new BaseClientResponseStreamFactory()
      {
         InputStream stream;

         public InputStream getInputStream() throws IOException
         {
            if (stream == null)
            {
               HttpEntity entity = res.getEntity();
               if (entity == null) return null;
               stream = new SelfExpandingBufferredInputStream(entity.getContent());
            }
            return stream;
         }

         public void performReleaseConnection()
         {
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
      }, this);
      response.setStatus(res.getStatusLine().getStatusCode());
      response.setHeaders(extractHeaders(res));
      response.setProviderFactory(request.getProviderFactory());
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
      else if ("DELETE".equals(restVerb))
      {
         return new HttpDelete(url);
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

   public void loadHttpMethod(final ClientRequest request, HttpRequestBase httpMethod) throws Exception
   {
      if (httpMethod instanceof HttpGet && request.followRedirects())
      {
         HttpClientParams.setRedirecting(httpMethod.getParams(), true);
      }
      else
      {
         HttpClientParams.setRedirecting(httpMethod.getParams(), false);
      }

      if (request.getHeaders() != null)
      {
         for (Map.Entry<String, List<String>> header : request.getHeaders().entrySet())
         {
            List<String> values = header.getValue();
            for (String value : values)
            {
//               System.out.println(String.format("setting %s = %s", header.getKey(), value));
               httpMethod.addHeader(header.getKey(), value);
            }
         }
      }
      if (request.getBody() != null && !request.getFormParameters().isEmpty())
         throw new RuntimeException("You cannot send both form parameters and an entity body");

      if (!request.getFormParameters().isEmpty())
      {
         HttpPost post = (HttpPost) httpMethod;

         List<NameValuePair> formparams = new ArrayList<NameValuePair>();

         for (Map.Entry<String, List<String>> formParam : request.getFormParameters().entrySet())
         {
            List<String> values = formParam.getValue();
            for (String value : values)
            {
               formparams.add(new BasicNameValuePair(formParam.getKey(), value));
            }
         }

         UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
         post.setEntity(entity);
      }
      if (request.getBody() != null)
      {
         if (httpMethod instanceof HttpGet) throw new RuntimeException("A GET request cannot have a body.");

         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try
         {
            request.writeRequestBody(new HttpClient4HeaderWrapper(httpMethod, request.getProviderFactory()), baos);
            ByteArrayEntity entity = new ByteArrayEntity(baos.toByteArray())
            {
               @Override
               public Header getContentType()
               {
                  return new BasicHeader("Content-Type", request.getBodyContentType().toString());
               }
            };
            HttpPost post = (HttpPost) httpMethod;
            post.setEntity(entity);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
   }

}