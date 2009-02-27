package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.HttpClient;
import org.jboss.resteasy.annotations.ClientResponseType;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.EntityTypeFactory;
import org.jboss.resteasy.core.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.core.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.core.interception.MessageBodyWriterInterceptor;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.MediaTypeHelper;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class ClientInvoker
{
   protected ResteasyProviderFactory providerFactory;
   protected String httpMethod;
   protected UriBuilderImpl uri;
   protected Method method;
   protected Class declaring;
   protected MediaType accepts;
   protected Marshaller[] marshallers;
   protected HttpClient client;
   protected MessageBodyReaderInterceptor[] readerInterceptors;
   protected MessageBodyWriterInterceptor[] writerInterceptors;
   protected ClientExecutionInterceptor[] executionInterceptors;


   public ClientInvoker(URI baseUri, Class declaring, Method method, ResteasyProviderFactory providerFactory, HttpClient client)
   {
      this.declaring = declaring;
      this.method = method;
      this.marshallers = ClientMarshallerFactory.createMarshallers(method,
              providerFactory);
      this.providerFactory = providerFactory;
      accepts = MediaTypeHelper.getProduces(declaring, method);
      this.client = client;
      this.uri = new UriBuilderImpl();
      uri.uri(baseUri);
      if (declaring.isAnnotationPresent(Path.class)) uri.path(declaring);
      if (method.isAnnotationPresent(Path.class)) uri.path(method);
      readerInterceptors = providerFactory.getClientMessageBodyReaderInterceptorRegistry().bind(declaring, method);
      writerInterceptors = providerFactory.getClientMessageBodyWriterInterceptorRegistry().bind(declaring, method);
      executionInterceptors = providerFactory.getClientExecutionInterceptorRegistry().bind(declaring, method);
   }

   public MediaType getAccepts()
   {
      return accepts;
   }

   public MessageBodyReaderInterceptor[] getReaderInterceptors()
   {
      return readerInterceptors;
   }

   public void setReaderInterceptors(MessageBodyReaderInterceptor[] readerInterceptors)
   {
      this.readerInterceptors = readerInterceptors;
   }

   public MessageBodyWriterInterceptor[] getWriterInterceptors()
   {
      return writerInterceptors;
   }

   public void setWriterInterceptors(MessageBodyWriterInterceptor[] writerInterceptors)
   {
      this.writerInterceptors = writerInterceptors;
   }

   public ClientExecutionInterceptor[] getExecutionInterceptors()
   {
      return executionInterceptors;
   }

   public void setExecutionInterceptors(ClientExecutionInterceptor[] executionInterceptors)
   {
      this.executionInterceptors = executionInterceptors;
   }

   public Method getMethod()
   {
      return method;
   }

   public Class getDeclaring()
   {
      return declaring;
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return providerFactory;
   }

   public Object invoke(Object[] args)
   {
      boolean isProvidersSet = ResteasyProviderFactory.getContextData(Providers.class) != null;
      if (!isProvidersSet) ResteasyProviderFactory.pushContext(Providers.class, providerFactory);

      try
      {
         if (uri == null) throw new RuntimeException("You have not set a base URI for the client proxy");

         ClientRequest request = new ClientRequest(uri, new ApacheHttpClientExecutor(client), providerFactory);
         if (accepts != null) request.header(HttpHeaders.ACCEPT, accepts.toString());
         request.setWriterInterceptors(writerInterceptors);
         request.setReaderInterceptors(readerInterceptors);
         request.setExecutionInterceptors(executionInterceptors);

         boolean isClientResponseResult = ClientResponse.class.isAssignableFrom(method.getReturnType());
         if (isClientResponseResult)
         {
            request.followRedirects(false);
         }
         else
         {
            request.followRedirects(true);
         }

         for (int i = 0; i < marshallers.length; i++)
         {
            marshallers[i].build(request, args[i]);
         }


         BaseClientResponse clientResponse = null;
         try
         {
            clientResponse = (BaseClientResponse) request.httpMethod(httpMethod);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
         clientResponse.setAttributeExceptionsTo(method.toString());
         Object extractedEntity = extractEntity(clientResponse);
         return extractedEntity;
      }
      finally
      {
         if (!isProvidersSet) ResteasyProviderFactory.popContextData(Providers.class);
      }
   }

   private Object extractEntity(BaseClientResponse clientResponse)
   {
      clientResponse.setAnnotations(method.getAnnotations());
      final Class<?> returnType = method.getReturnType();
      if (ClientResponse.class.isAssignableFrom(returnType))
      {
         Type methodGenericReturnType = method.getGenericReturnType();
         if (methodGenericReturnType instanceof ParameterizedType)
         {
            ParameterizedType zType = (ParameterizedType) methodGenericReturnType;
            Type genericReturnType = zType.getActualTypeArguments()[0];
            clientResponse.setReturnType(Types.getRawType(genericReturnType));
            clientResponse.setGenericReturnType(genericReturnType);
         }

         return clientResponse;
      }
      else if (returnType.equals(Response.Status.class))
      {
         clientResponse.releaseConnection();
         return clientResponse.getResponseStatus();
      }
      else if (Response.class.isAssignableFrom(returnType))
      {
         ClientResponseType responseHint = method.getAnnotation(ClientResponseType.class);
         if (responseHint != null)
         {
            handleResponseHint(clientResponse, responseHint);
         }
         else
         {
            clientResponse.releaseConnection();
         }
         return clientResponse;
      }

      // We are not a ClientResposne type so we need to unmarshall and narrow it to right type

      clientResponse.checkFailureStatus();

      if (returnType == null || isVoidReturnType(returnType))
      {
         clientResponse.releaseConnection();
         return null;
      }

      clientResponse.setReturnType(returnType);
      clientResponse.setGenericReturnType(method.getGenericReturnType());

      return clientResponse.getEntity();
   }

   private boolean isVoidReturnType(final Class<?> returnType)
   {
      return void.class.equals(returnType) || Void.class.equals(returnType);
   }

   private void handleResponseHint(BaseClientResponse clientResponse,
                                   ClientResponseType responseHint)
   {
      Class returnType = responseHint.entityType();
      Class<? extends EntityTypeFactory> entityTypeFactory = responseHint.entityTypeFactory();
      if (isVoidReturnType(returnType))
      {
         EntityTypeFactory factory = null;
         try
         {
            factory = entityTypeFactory.newInstance();
         }
         catch (InstantiationException e)
         {
            throw clientResponse
                    .createResponseFailure("Could not create a default entity type factory of type "
                            + entityTypeFactory.getClass().getName());
         }
         catch (IllegalAccessException e)
         {
            throw clientResponse
                    .createResponseFailure("Could not create a default entity type factory of type "
                            + entityTypeFactory.getClass().getName()
                            + ". "
                            + e.getMessage());
         }
         returnType = factory.getEntityType(clientResponse.getStatus(), clientResponse.getMetadata());
      }
      if (!isVoidReturnType(returnType))
      {
         clientResponse.setReturnType(returnType);
      }
   }

   public String getHttpMethod()
   {
      return httpMethod;
   }

   public void setHttpMethod(String httpMethod)
   {
      this.httpMethod = httpMethod;
   }
}