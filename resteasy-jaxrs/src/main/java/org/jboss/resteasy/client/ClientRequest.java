package org.jboss.resteasy.client;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.jboss.resteasy.client.core.ClientInterceptor;
import org.jboss.resteasy.client.core.ClientResponseImpl;
import org.jboss.resteasy.client.core.CookieParamMarshaller;
import org.jboss.resteasy.client.core.FormParamMarshaller;
import org.jboss.resteasy.client.core.HeaderParamMarshaller;
import org.jboss.resteasy.client.core.Marshaller;
import org.jboss.resteasy.client.core.MessageBodyParameterMarshaller;
import org.jboss.resteasy.client.core.PathParamMarshaller;
import org.jboss.resteasy.client.core.QueryParamMarshaller;
import org.jboss.resteasy.client.core.WebRequestIntializer;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import static org.jboss.resteasy.util.HttpHeaderNames.*;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */

@SuppressWarnings("unchecked")
public class ClientRequest
{
   protected ResteasyProviderFactory providerFactory;
   private String uriTemplate;
   private HttpClient httpClient;
   private Collection<ClientInterceptor> interceptors = new ArrayList<ClientInterceptor>();
   private List<Marshaller> marshallers = new ArrayList<Marshaller>();
   private List<Object> args = new ArrayList<Object>();

   public ClientRequest(String uriTemplate)
   {
      this(uriTemplate, new HttpClient(), ResteasyProviderFactory.getInstance());
   }

   public ClientRequest(String uriTemplate, HttpClient httpClient)
   {
      this(uriTemplate, httpClient, ResteasyProviderFactory.getInstance());
   }

   public ClientRequest(String uriTemplate, HttpClient httpClient, ResteasyProviderFactory providerFactory)
   {
      this.uriTemplate = uriTemplate;
      this.httpClient = httpClient;
      this.providerFactory = providerFactory;
   }

   public ClientRequest interceptor(ClientInterceptor clientInterceptor)
   {
      interceptors.add(clientInterceptor);
      return this;
   }

   public ClientRequest interceptors(
           Collection<ClientInterceptor> clientInterceptors)
   {
      interceptors.addAll(clientInterceptors);
      return this;
   }

   public ClientRequest accept(MediaType accepts)
   {
      return header(ACCEPT, accepts.toString());
   }

   public ClientRequest accept(String accept)
   {
      return header(ACCEPT, accept);
   }

   public ClientRequest formParameter(String parameterName, Object value)
   {
      return marshaller(
              new FormParamMarshaller(parameterName, providerFactory), value);
   }

   public ClientRequest queryParameter(String parameterName, Object value)
   {
      return marshaller(
              new QueryParamMarshaller(parameterName, providerFactory), value);
   }

   public ClientRequest header(String headerName, Object value)
   {
      return marshaller(new HeaderParamMarshaller(headerName, providerFactory),
              value);
   }

   public ClientRequest cookie(String cookieName, Object value)
   {
      return marshaller(new CookieParamMarshaller(cookieName), value);
   }

   public ClientRequest cookie(Cookie cookie)
   {
      return marshaller(new CookieParamMarshaller(null), cookie);
   }

   public ClientRequest pathParameter(String parameterName, Object value)
   {
      return pathParameter(parameterName, value, false);
   }

   public ClientRequest pathParameter(String parameterName, Object value,
                                      boolean encoded)
   {
      return marshaller(new PathParamMarshaller(parameterName, encoded,
              providerFactory), value);
   }

   /**
    * This is mostly used internally, but the Marshaller can be used as an
    * "interceptor"
    *
    * @param marshaller
    * @param value
    * @return
    */
   public ClientRequest marshaller(Marshaller marshaller, Object value)
   {
      marshallers.add(marshaller);
      args.add(value);
      return this;
   }

   public ClientRequest body(String contentType, Object data)
   {
      MessageBodyParameterMarshaller marshaller = new MessageBodyParameterMarshaller(
              MediaType.valueOf(contentType), data.getClass(), null, null,
              this.providerFactory);
      return marshaller(marshaller, data);
   }

   public ClientResponse<byte[]> get() throws Exception
   {
      return get(byte[].class);
   }

   public <T> ClientResponse<T> get(Class<T> returnType)
           throws Exception
   {
      return (ClientResponse<T>) getResponse(returnType, null, true, "GET");
   }


   public ClientResponse<Void> post() throws Exception
   {
      return post(Void.class);
   }

   public <T> ClientResponse<T> post(Class<T> returnType) throws Exception
   {
      return (ClientResponse<T>) getResponse(returnType, null, true, "POST");
   }


   public ClientResponse<Void> put() throws Exception
   {
      return put(Void.class);
   }

   public <T> ClientResponse<T> put(Class<T> returnType) throws Exception
   {
      return (ClientResponse<T>) getResponse(returnType, null, true, "PUT");
   }


   public ClientResponse<Void> delete() throws Exception
   {
      return delete(Void.class);
   }

   public <T> ClientResponse<T> delete(Class<T> returnType) throws Exception
   {
      return (ClientResponse<T>) getResponse(returnType, null, true, "DELETE");
   }

   private <T> Object getResponse(Class<T> returnType, Type genericReturnType,
                                  boolean isClientResponse, String restVerb) throws Exception
   {
      ClientResponseImpl<T> clientResponse = createResponseImpl(restVerb,
              returnType, genericReturnType);

      WebRequestIntializer urlRetriever = new WebRequestIntializer(marshallers.toArray(new Marshaller[marshallers.size()]));
      clientResponse.setUrl(urlRetriever.buildUrl(uriTemplate, true, args.toArray()));

      HttpMethodBase baseMethod = clientResponse.getHttpBaseMethod();
      if (isClientResponse)
      {
         baseMethod.setFollowRedirects(false);
      }

      urlRetriever.setHeadersAndRequestBody(baseMethod, args.toArray());
      clientResponse.execute(this.httpClient);
      if (isClientResponse)
      {
         return clientResponse;
      }
      else if (returnType == null || returnType.equals(void.class))
      {
         clientResponse.releaseConnection();
         return null;
      }
      else
      {
         return clientResponse.getEntity();
      }
   }

   private <T> ClientResponseImpl<T> createResponseImpl(String restVerb,
                                                        Class<T> returnType, Type genericReturnType) throws Exception
   {
      ClientResponseImpl<T> clientResponse = new ClientResponseImpl<T>();
      clientResponse.setReturnType(returnType);
      clientResponse.setGenericReturnType(genericReturnType);
      clientResponse.setProviderFactory(providerFactory);
      clientResponse.setRestVerb(restVerb);
      clientResponse.setAttributeExceptionsTo("WebRequest");
      clientResponse.setInterceptors(interceptors);
      return clientResponse;
   }


}
