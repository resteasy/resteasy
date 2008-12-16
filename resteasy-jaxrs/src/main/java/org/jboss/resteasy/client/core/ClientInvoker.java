package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.jboss.resteasy.client.ClientResponse;
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
      this.urlRetriever = new WebRequestIntializer(method, providerFactory);
      this.providerFactory = providerFactory;
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
         return extractEntity(clientResponse);
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

      String url = urlRetriever.buildUrl(uri, false, args);
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
      if (ClientResponse.class.isAssignableFrom(method.getReturnType()))
      {
         Type methodGenericReturnType = method.getGenericReturnType();
         if (methodGenericReturnType instanceof ParameterizedType)
         {
            ParameterizedType zType = (ParameterizedType) methodGenericReturnType;
            Type genericReturnType = zType.getActualTypeArguments()[0];
            clientResponse.setReturnType(Types.getRawType(genericReturnType));
            clientResponse.setGenericReturnType(genericReturnType);
         }

         clientResponse.setAnnotations(method.getAnnotations());
         return clientResponse;
      }

      if (method.getReturnType().equals(Response.Status.class))
      {
         clientResponse.releaseConnection();
         return Response.Status.fromStatusCode(clientResponse.getStatus());
      }

      clientResponse.checkFailureStatus();

      if (method.getReturnType() == null || method.getReturnType().equals(void.class))
      {
         clientResponse.releaseConnection();
         return null;
      }

      clientResponse.setReturnType(method.getReturnType());
      clientResponse.setGenericReturnType(method.getGenericReturnType());

      // TODO: Bill, why do we need this?
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

   public String getRestVerb()
   {
      return restVerb;
   }

   public void setRestVerb(String restVerb)
   {
      this.restVerb = restVerb;
   }
}