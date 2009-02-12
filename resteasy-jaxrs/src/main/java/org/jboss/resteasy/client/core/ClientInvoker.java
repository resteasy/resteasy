package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.jboss.resteasy.annotations.ClientResponseType;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.EntityTypeFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.MediaTypeHelper;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.Produces;
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
   private String restVerb;
   protected WebRequestIntializer urlRetriever;
   protected URI uri;
   private Method method;
   protected Class<?> declaring;
   protected MediaType accepts;
   protected HttpClient client;
   private Iterable<ClientInterceptor> interceptors;

   public ClientInvoker(Class<?> declaring, Method method, ResteasyProviderFactory providerFactory, HttpClient client, Iterable<ClientInterceptor> interceptors)
   {
      this.declaring = declaring;
      this.method = method;
      Marshaller[] marshallers = ClientMarshallerFactory.createMarshallers(method,
              providerFactory);
      this.providerFactory = providerFactory;
      this.urlRetriever = new WebRequestIntializer(marshallers);
      accepts = MediaTypeHelper.getProduces(declaring, method);
      this.client = client;
      this.interceptors = interceptors;
   }

   public void setBaseUri(URI uri)
   {
      this.uri = uri;
   }

   public Object invoke(Object[] args)
   {
      boolean isProvidersSet = ResteasyProviderFactory.getContextData(Providers.class) != null;
      if (!isProvidersSet) ResteasyProviderFactory.pushContext(Providers.class, providerFactory);

      try
      {
         if (uri == null) throw new RuntimeException("You have not set a base URI for the client proxy");

         ClientResponseImpl clientResponse = createClientResponse(args);
         initBaseMethod(args, clientResponse);
         clientResponse.execute(client);
         Object extractedEntity = extractEntity(clientResponse);
         return extractedEntity;
      }
      finally
      {
         if (!isProvidersSet) ResteasyProviderFactory.popContextData(Providers.class);
      }
   }

   private ClientResponseImpl createClientResponse(Object[] args)
   {
      ClientResponseImpl clientResponse = new ClientResponseImpl();
      clientResponse.setProviderFactory(providerFactory);
      clientResponse.setRestVerb(restVerb);
      clientResponse.setAttributeExceptionsTo(method.toString());
      if (interceptors != null)
         clientResponse.setInterceptors(interceptors);

      String url = urlRetriever.buildUrl(uri, false, method, args);
      clientResponse.setUrl(url);
      return clientResponse;
   }

   private void initBaseMethod(Object[] args,
                               ClientResponseImpl clientResponse)
   {
      HttpMethodBase baseMethod = clientResponse.getHttpBaseMethod();
      boolean isClientResponseResult = ClientResponse.class.isAssignableFrom(method.getReturnType());
      if (isClientResponseResult)
      {
         baseMethod.setFollowRedirects(false);
      }

      if (accepts != null)
      {
         baseMethod.setRequestHeader(HttpHeaderNames.ACCEPT, accepts.toString());
      }

      urlRetriever.setHeadersAndRequestBody(baseMethod, args);
   }

   private Object extractEntity(ClientResponseImpl clientResponse)
   {
      final Class<?> returnType = method.getReturnType();
      clientResponse.setAnnotations(method.getAnnotations());
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

      if (returnType.equals(Response.Status.class))
      {
         clientResponse.releaseConnection();
         return clientResponse.getResponseStatus();
      }

      if (Response.class.isAssignableFrom(returnType))
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

      clientResponse.checkFailureStatus();

      if (returnType == null || isVoidReturnType(returnType))
      {
         clientResponse.releaseConnection();
         return null;
      }

      clientResponse.setReturnType(returnType);
      clientResponse.setGenericReturnType(method.getGenericReturnType());

      if (clientResponse.getContentType() == null)
      {
         Produces produce = method.getAnnotation(Produces.class);
         if (produce == null) produce = (Produces) declaring.getAnnotation(Produces.class);
         if (produce == null)
         {
            throw clientResponse.createResponseFailure("@Produces on your proxy method, " + method.toString() + ", is required");
         }
         clientResponse.setAlternateMediaType(produce.value()[0]);
      }
      return clientResponse.getEntity();
   }

   private boolean isVoidReturnType(final Class<?> returnType)
   {
      return void.class.equals(returnType) || Void.class.equals(returnType);
   }

   private void handleResponseHint(ClientResponseImpl clientResponse,
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

   public String getRestVerb()
   {
      return restVerb;
   }

   public void setRestVerb(String restVerb)
   {
      this.restVerb = restVerb;
   }
}