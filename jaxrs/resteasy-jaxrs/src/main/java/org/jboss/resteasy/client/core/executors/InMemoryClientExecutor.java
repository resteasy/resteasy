package org.jboss.resteasy.client.core.executors;

import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse.BaseClientResponseStreamFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.SynchronousExecutionContext;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class InMemoryClientExecutor implements ClientExecutor
{
   protected Dispatcher dispatcher;
   protected URI baseUri;

   public InMemoryClientExecutor()
   {
      dispatcher = new SynchronousDispatcher(ResteasyProviderFactory.getInstance());
   }

   public InMemoryClientExecutor(Dispatcher dispatcher)
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

   public ClientRequest createRequest(String uriTemplate)
   {
      return new ClientRequest(uriTemplate, this);
   }

   public ClientRequest createRequest(UriBuilder uriBuilder)
   {
      return new ClientRequest(uriBuilder, this);
   }


   public ClientResponse execute(ClientRequest request) throws Exception
   {
      MockHttpRequest mockHttpRequest = MockHttpRequest.create(request.getHttpMethod(), new URI(request.getUri()),
              baseUri);
      final MockHttpResponse mockResponse = new MockHttpResponse();
      mockHttpRequest.setAsynchronousContext(new SynchronousExecutionContext((SynchronousDispatcher)dispatcher, mockHttpRequest, mockResponse));
      loadHttpMethod(request, mockHttpRequest);

      dispatcher.invoke(mockHttpRequest, mockResponse);
      return createResponse(request, mockResponse);
   }

   protected BaseClientResponse createResponse(ClientRequest request, final MockHttpResponse mockResponse)
   {
      BaseClientResponseStreamFactory streamFactory = createStreamFactory(mockResponse);
      BaseClientResponse response = new BaseClientResponse(streamFactory, this);
      response.setStatus(mockResponse.getStatus());
      setHeaders(mockResponse, response);
      response.setProviderFactory(request.getProviderFactory());
      response.setAttributes(request.getAttributes());
      return response;
   }

   protected void setHeaders(final MockHttpResponse mockResponse, BaseClientResponse response)
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

   public static BaseClientResponseStreamFactory createStreamFactory(final MockHttpResponse mockResponse)
   {
      return new BaseClientResponseStreamFactory()
      {
         InputStream stream;

         public InputStream getInputStream() throws IOException
         {
            if (stream == null)
            {
               stream = new ByteArrayInputStream(mockResponse.getOutput());
            }
            return stream;
         }

         public void performReleaseConnection()
         {
         }
      };
   }

   public void loadHttpMethod(ClientRequest request, MockHttpRequest mockHttpRequest) throws Exception
   {
      // TODO: punt on redirects, for now.
      // if (httpMethod instanceof GetMethod && request.followRedirects())
      // httpMethod.setFollowRedirects(true);
      // else httpMethod.setFollowRedirects(false);

      if (request.getBody() != null && !request.getFormParameters().isEmpty())
         throw new RuntimeException(Messages.MESSAGES.cannotSendFormParametersAndEntity());

      if (!request.getFormParameters().isEmpty())
      {
         commitHeaders(request, mockHttpRequest);
         for (Map.Entry<String, List<String>> formParam : request.getFormParameters().entrySet())
         {
            String key = formParam.getKey();
            for (String value : formParam.getValue())
            {
               mockHttpRequest.getFormParameters().add(key, value);
            }
         }
      }
      else if (request.getBody() != null)
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         MediaType bodyContentType = request.getBodyContentType();
         request.getHeadersAsObjects().add(HttpHeaders.CONTENT_TYPE, bodyContentType.toString());

         request.writeRequestBody(request.getHeadersAsObjects(), baos);
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

   public void commitHeaders(ClientRequest request, MockHttpRequest mockHttpRequest)
   {
      MultivaluedMap headers = mockHttpRequest.getHttpHeaders().getRequestHeaders();
      headers.putAll(request.getHeaders());
   }

   private void setBody(ClientRequest request, MockHttpRequest mockHttpRequest) throws IOException
   {
      if (request.getBody() == null)
         return;

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
   
}
