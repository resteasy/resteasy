package org.jboss.resteasy.client.core.executors;

import static org.jboss.resteasy.util.HttpHeaderNames.CONTENT_TYPE;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse.BaseClientResponseStreamFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;

@SuppressWarnings("unchecked")
public class URLConnectionClientExecutor implements ClientExecutor
{

   public ClientResponse execute(ClientRequest request) throws Exception
   {
      String uri = request.getUri();
      String httpMethod = request.getHttpMethod();

      HttpURLConnection connection = (HttpURLConnection) new URL(uri)
            .openConnection();
      connection.setRequestMethod(httpMethod);
      setupRequest(request, connection);
      return execute(request, connection);
   }

   private void setupRequest(ClientRequest request, HttpURLConnection connection)
         throws ProtocolException
   {
      boolean isGet = "GET".equals(request.getHttpMethod());
      connection.setInstanceFollowRedirects(isGet && request.followRedirects());
      connection.setDoOutput(request.getBody() != null
            || !request.getFormParameters().isEmpty());

      setHeaders(request, connection);

      if (request.getBody() != null && !request.getFormParameters().isEmpty())
         throw new RuntimeException(
               "You cannot send both form parameters and an entity body");

      if (!request.getFormParameters().isEmpty())
      {
         throw new RuntimeException(
               "URLConnectionClientExecutor doesn't support form parameters yet");
      }
   }

   private void setHeaders(ClientRequest request, HttpURLConnection connection)
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

   private ClientResponse execute(ClientRequest request, final HttpURLConnection connection) throws IOException
   {
      MultivaluedMap<String, String> outputHeaders = outputBody(request, connection);
      final int status = connection.getResponseCode();
      BaseClientResponse response = new BaseClientResponse(new BaseClientResponseStreamFactory(){
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
      });
      response.setProviderFactory(request.getProviderFactory());
      response.setStatus(status);
      response.setHeaders(getHeaders(connection, outputHeaders));
      return response;
   }

   private MultivaluedMap<String, String> getHeaders(
         final HttpURLConnection connection,
         MultivaluedMap<String, String> outputHeaders)
   {
      MultivaluedMap<String, String> headers = outputHeaders == null ? new CaseInsensitiveMap<String>() : outputHeaders;

      for (Entry<String, List<String>> header : connection.getHeaderFields()
            .entrySet())
      {
         if (header.getKey() != null)
            for (String value : header.getValue())
               headers.add(header.getKey(), value);
      }
      return headers;
   }

   private MultivaluedMap<String, String> outputBody(ClientRequest request, HttpURLConnection connection)
   {
      if (request.getBody() != null)
      {
         // System.out.println(request.getBody());
         if (connection.getRequestProperty(CONTENT_TYPE) == null){
            String type = request.getBodyContentType().toString();
            connection.addRequestProperty(CONTENT_TYPE, type);
         }
         MultivaluedMap headers = new URLConnectionHeaderWrapper(
               connection, request.getProviderFactory());
         try
         {
            OutputStream os = connection.getOutputStream();
            request.writeRequestBody(headers, os);
            os.flush();
            os.close();
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
         return headers;
      } 
      else 
      {
         return null;
      }
   }
}
