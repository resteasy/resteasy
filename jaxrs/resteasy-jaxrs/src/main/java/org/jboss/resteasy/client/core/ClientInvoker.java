package org.jboss.resteasy.client.core;

import org.jboss.resteasy.annotations.ClientResponseType;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.EntityTypeFactory;
import org.jboss.resteasy.client.core.marshallers.ClientMarshallerFactory;
import org.jboss.resteasy.client.core.marshallers.Marshaller;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.MediaTypeHelper;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class ClientInvoker extends ClientInterceptorRepositoryImpl
{
   protected ResteasyProviderFactory providerFactory;
   protected String httpMethod;
   protected UriBuilderImpl uri;
   protected Method method;
   protected Class declaring;
   protected MediaType accepts;
   protected Marshaller[] marshallers;
   protected ClientExecutor executor;
   protected boolean followRedirects;


   public ClientInvoker(URI baseUri, Class declaring, Method method, ResteasyProviderFactory providerFactory, ClientExecutor executor)
   {
      this.declaring = declaring;
      this.method = method;
      this.marshallers = ClientMarshallerFactory.createMarshallers(declaring, method,
              providerFactory);
      this.providerFactory = providerFactory;
      this.executor = executor;
      accepts = MediaTypeHelper.getProduces(declaring, method);
      this.uri = new UriBuilderImpl();
      uri.uri(baseUri);
      if (declaring.isAnnotationPresent(Path.class)) uri.path(declaring);
      if (method.isAnnotationPresent(Path.class)) uri.path(method);
   }

   public MediaType getAccepts()
   {
      return accepts;
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

         ClientRequest request = new ClientRequest(uri, executor, providerFactory);
         if (accepts != null) request.header(HttpHeaders.ACCEPT, accepts.toString());
         this.copyClientInterceptorsTo(request);

         boolean isClientResponseResult = ClientResponse.class.isAssignableFrom(method.getReturnType());
         request.followRedirects(!isClientResponseResult || this.followRedirects);

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
         return clientResponse;
      }
      // We are not a ClientResposne type so we need to unmarshall and narrow it
      // to right type. If we are unable to unmarshall, or encounter any kind of
      // Exception, give the ClientErrorHandlers a chance to handle the
      // ClientResponse manually.

      // only release connection if it is not an instance of an InputStream
      boolean releaseConnectionAfter = true;
      try
      {
         clientResponse.checkFailureStatus();

         if (returnType == null || isVoidReturnType(returnType))
         {
            clientResponse.releaseConnection();
            return null;
         }

         clientResponse.setReturnType(returnType);
         clientResponse.setGenericReturnType(method.getGenericReturnType());

         Object obj = clientResponse.getEntity();
         if (obj instanceof InputStream) releaseConnectionAfter = false;
         return obj;
      }
      catch (RuntimeException e)
      {
         for (ClientErrorInterceptor handler : providerFactory.getClientErrorInterceptors())
         {
            try
            {
               // attempt to reset the stream in order to provide a fresh stream
               // to each ClientErrorInterceptor -- failing to reset the stream
               // could mean that an unusable stream will be passed to the
               // interceptor
               InputStream stream = clientResponse.getStreamFactory().getInputStream();
               if (stream != null)
               {
                  stream.reset();
               }
            }
            catch (IOException e1)
            {
               // eat this exception since it's not really relevant for the client response
            }
            handler.handle(clientResponse);
         }
         throw e;
      }
      finally
      {
         if (releaseConnectionAfter) clientResponse.releaseConnection();
      }
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

   public boolean isFollowRedirects()
   {
      return followRedirects;
   }

   public void setFollowRedirects(boolean followRedirects)
   {
      this.followRedirects = followRedirects;
   }

   public void followRedirects()
   {
      setFollowRedirects(true);
   }

}