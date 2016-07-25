package org.jboss.resteasy.test.profiling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.SynchronousExecutionContext;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @author <a href="mailto:rsigal@redhat.com">Ron Sigal</a>
 * @version $Revision: 1 $
 */
public class InMemoryClientEngine implements ClientHttpEngine
{
   protected Dispatcher dispatcher;
   protected URI baseUri;

   public InMemoryClientEngine()
   {
      dispatcher = new SynchronousDispatcher(ResteasyProviderFactory.getInstance());
   }

   public InMemoryClientEngine(Dispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   
   public URI getBaseUri()
   {
      return baseUri;
   }

   public void setBaseUri(URI baseUri)
   {
      this.baseUri = baseUri;
   }

   @Override
   public ClientResponse invoke(ClientInvocation request)
   {
      MockHttpRequest mockHttpRequest = MockHttpRequest.create(request.getMethod(), request.getUri(), baseUri);
      final MockHttpResponse mockResponse = new MockHttpResponse();
      mockHttpRequest.setAsynchronousContext(new SynchronousExecutionContext((SynchronousDispatcher)dispatcher, mockHttpRequest, mockResponse));
      loadHttpMethod(request, mockHttpRequest);
      dispatcher.invoke(mockHttpRequest, mockResponse);
      return createResponse(request, mockResponse);
   }

   protected ClientResponse createResponse(ClientInvocation request, final MockHttpResponse mockResponse)
   {
      InputStream is = new ByteArrayInputStream(mockResponse.getOutput());
      ClientResponse response = new InMemoryClientResponse(request.getClientConfiguration(), is);
      response.setStatus(mockResponse.getStatus());
      setHeaders(mockResponse, response);
      return response;
   }

   protected void setHeaders(final MockHttpResponse mockResponse, ClientResponse response)
   {
      MultivaluedMapImpl<String, String> responseHeaders = new MultivaluedMapImpl<String, String>();
      for (Entry<String, List<Object>> entry : mockResponse.getOutputHeaders().entrySet())
      {
         List<String> values = new ArrayList<String>(entry.getValue().size());
         for (Object value : entry.getValue())
         {
            values.add(value.toString());
         }
         responseHeaders.addMultiple(entry.getKey(), values);
      }
      response.setHeaders(responseHeaders);
   }

   public void loadHttpMethod(ClientInvocation request, MockHttpRequest mockHttpRequest)// throws Exception
   {
      // TODO: punt on redirects, for now.
      // if (httpMethod instanceof GetMethod && request.followRedirects())
      // httpMethod.setFollowRedirects(true);
      // else httpMethod.setFollowRedirects(false);

//      if (request.getEntity() != null && !request.getFormParameters().isEmpty())
//         throw new RuntimeException(Messages.MESSAGES.cannotSendFormParametersAndEntity());

      if (request.getEntity() instanceof Form)
      {
         commitHeaders(request, mockHttpRequest);
         Form form = (Form) request.getEntity();
         MultivaluedMap<String, String> map = form.asMap();
         for (Map.Entry<String, List<String>> formParam : map.entrySet())
         {
            String key = formParam.getKey();
            for (String value : formParam.getValue())
            {
               mockHttpRequest.getFormParameters().add(key, value);
            }
         }
      }
      else if (request.getEntity() != null)
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         MediaType bodyContentType = request.getHeaders().getMediaType();
         request.getHeaders().asMap().add(HttpHeaders.CONTENT_TYPE, bodyContentType.toString());

         try
         {
            request.writeRequestBody(baos);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
         // commit headers after byte array is complete.
         commitHeaders(request, mockHttpRequest);
         mockHttpRequest.content(baos.toByteArray());
         mockHttpRequest.contentType(bodyContentType);
      }
      else
      {
         commitHeaders(request, mockHttpRequest);
      }
   }

   public void commitHeaders(ClientInvocation request, MockHttpRequest mockHttpRequest)
   {
      MultivaluedMap<String, String> headers = mockHttpRequest.getHttpHeaders().getRequestHeaders();
      headers.putAll(request.getHeaders().asMap());
   }

   public Registry getRegistry()
   {
      return this.dispatcher.getRegistry();
   }

   public Dispatcher getDispatcher()
   {
      return dispatcher;
   }

   public void close()
   {
      // empty
   }

   @Override
   public SSLContext getSslContext()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public HostnameVerifier getHostnameVerifier()
   {
      // TODO Auto-generated method stub
      return null;
   }
   
   public static class InMemoryClientResponse extends ClientResponse
   {
      private InputStream stream;
      
      protected InMemoryClientResponse(ClientConfiguration configuration, InputStream is)
      {
         super(configuration);
         stream = is;
      }
      
      @Override
      protected void setInputStream(InputStream is)
      {
         stream = is;
      }

      public InputStream getInputStream()
      {
         return stream;
      }

      public void releaseConnection() throws IOException
      {
      }
   }
}
