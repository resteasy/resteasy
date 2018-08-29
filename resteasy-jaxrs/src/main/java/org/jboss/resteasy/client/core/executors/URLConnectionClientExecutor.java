package org.jboss.resteasy.client.core.executors;

import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse.BaseClientResponseStreamFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.jboss.resteasy.util.CommitHeaderOutputStream;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

import static org.jboss.resteasy.util.HttpHeaderNames.*;

public class URLConnectionClientExecutor implements ClientExecutor
{

   public ClientResponse<?> execute(ClientRequest request) throws Exception
   {
      HttpURLConnection connection = createConnection(request);
      return execute(request, connection);
   }

   private void commitHeaders(ClientRequest request, HttpURLConnection connection)
   {
      for (Entry<String, List<String>> entry : request.getHeaders().entrySet())
      {
         String value = null;
         if (entry.getValue().size() == 1)
            value = entry.getValue().get(0);
         else
         {
            StringBuilder b = new StringBuilder();
            String add = "";
            for (String v : entry.getValue())
            {
               b.append(add).append(v);
               add = ",";
            }
            value = b.toString();
         }
         connection.addRequestProperty(entry.getKey(), value);
      }
   }

   public ClientRequest createRequest(String uriTemplate)
   {
      return new ClientRequest(uriTemplate, this);
   }

   public ClientRequest createRequest(UriBuilder uriBuilder)
   {
      return new ClientRequest(uriBuilder, this);
   }

   protected HttpURLConnection createConnection(ClientRequest request) throws Exception
   {
      String uri = request.getUri();
      String httpMethod = request.getHttpMethod();

      HttpURLConnection connection = (HttpURLConnection) new URL(uri).openConnection();
      connection.setRequestMethod(httpMethod);
      return connection;
   }

   private <T> ClientResponse<T> execute(ClientRequest request, final HttpURLConnection connection) throws IOException
   {
      outputBody(request, connection);
      final int status = connection.getResponseCode();
      BaseClientResponse<T> response = new BaseClientResponse<T>(new BaseClientResponseStreamFactory()
      {
         public InputStream getInputStream() throws IOException
         {
            return (status < 300) ? connection.getInputStream() : connection.getErrorStream();
         }

         public void performReleaseConnection()
         {
            try
            {
               getInputStream().close();
            }
            catch (IOException e)
            {
            }
            connection.disconnect();
         }
      }, this);
      response.setProviderFactory(request.getProviderFactory());
      response.setStatus(status);
      response.setHeaders(getHeaders(connection));
      response.setAttributes(request.getAttributes());
      return response;
   }

   public void close()
   {
      // empty
   }

   private MultivaluedMap<String, String> getHeaders(
           final HttpURLConnection connection)
   {
      MultivaluedMap<String, String> headers = new CaseInsensitiveMap<String>();

      for (Entry<String, List<String>> header : connection.getHeaderFields()
              .entrySet())
      {
         if (header.getKey() != null)
            for (String value : header.getValue())
               headers.add(header.getKey(), value);
      }
      return headers;
   }

   private void outputBody(final ClientRequest request, final HttpURLConnection connection)
   {
      if (request.getBody() != null)
      {
         // System.out.println(request.getBody());
         if (connection.getRequestProperty(CONTENT_TYPE) == null)
         {
            String type = request.getBodyContentType().toString();
            connection.addRequestProperty(CONTENT_TYPE, type);
         }
         try
         {
            final CommitHeaderOutputStream commit = new CommitHeaderOutputStream();
            CommitHeaderOutputStream.CommitCallback callback = new CommitHeaderOutputStream.CommitCallback()
            {
               @Override
               public void commit()
               {
                  connection.setDoOutput(true);
                  commitHeaders(request, connection);
                  OutputStream os = null;
                  try
                  {
                     os = connection.getOutputStream();
                  }
                  catch (IOException e)
                  {
                     throw new RuntimeException(e);
                  }
                  commit.setDelegate(os);

               }
            };
            commit.setHeaders(callback);
            try
            {
               request.writeRequestBody(request.getHeadersAsObjects(), commit);
            }
            finally
            {
               commit.getDelegate().flush();
               commit.getDelegate().close();
            }
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      else
      {
         commitHeaders(request, connection);
      }
   }
}
