package org.jboss.resteasy.client;

import org.apache.commons.httpclient.HttpClient;
import org.jboss.resteasy.client.core.ClientResponseImpl;
import org.jboss.resteasy.client.core.HttpClientExecutor;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringConverter;
import org.jboss.resteasy.util.GenericType;
import static org.jboss.resteasy.util.HttpHeaderNames.*;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Create a hand coded request to send to the server.
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */

@SuppressWarnings("unchecked")
public class ClientRequest
{
   protected ResteasyProviderFactory providerFactory;
   private UriBuilderImpl uri;
   private ClientExecutor executor;
   private MultivaluedMap<String, String> headers;
   private MultivaluedMap<String, String> queryParameters;
   private MultivaluedMap<String, String> formParameters;
   private MultivaluedMap<String, String> pathParameters;
   private MultivaluedMap<String, String> matrixParameters;
   private Object body;
   private Class bodyType;
   private Type bodyGenericType;
   private Annotation[] bodyAnnotations;
   private MediaType bodyContentType;
   private boolean followRedirects;

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
      this(new UriBuilderImpl().uriTemplate(uriTemplate), new HttpClientExecutor(httpClient), providerFactory);
   }

   public ClientRequest(UriBuilder uri, ClientExecutor executor, ResteasyProviderFactory providerFactory)
   {
      this.uri = (UriBuilderImpl) uri;
      this.executor = executor;
      this.providerFactory = providerFactory;
   }

   public boolean followRedirects()
   {
      return followRedirects;
   }

   public ClientRequest followRedirects(boolean followRedirects)
   {
      this.followRedirects = followRedirects;
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

   protected String toString(Object object)
   {
      if (object instanceof String) return (String) object;
      StringConverter converter = providerFactory.getStringConverter(object.getClass());
      if (converter != null) return converter.toString(object);
      else return object.toString();

   }

   protected String toHeaderString(Object object)
   {
      StringConverter converter = providerFactory.getStringConverter(object.getClass());
      if (converter != null) return converter.toString(object);

      RuntimeDelegate.HeaderDelegate delegate = providerFactory.createHeaderDelegate(object.getClass());
      if (delegate != null) return delegate.toString(object);
      else return object.toString();

   }

   public ClientRequest formParameter(String parameterName, Object value)
   {
      if (formParameters == null) formParameters = new MultivaluedMapImpl<String, String>();
      formParameters.add(parameterName, toString(value));
      return this;
   }

   public ClientRequest queryParameter(String parameterName, Object value)
   {
      if (queryParameters == null) queryParameters = new MultivaluedMapImpl<String, String>();
      queryParameters.add(parameterName, toString(value));
      return this;
   }

   public ClientRequest matrixParameter(String parameterName, Object value)
   {
      if (matrixParameters == null) matrixParameters = new MultivaluedMapImpl<String, String>();
      matrixParameters.add(parameterName, toString(value));
      return this;
   }

   public ClientRequest header(String headerName, Object value)
   {
      if (headers == null) headers = new MultivaluedMapImpl<String, String>();
      headers.add(headerName, toHeaderString(value));
      return this;
   }

   public ClientRequest cookie(String cookieName, Object value)
   {
      return cookie(new Cookie(cookieName, toString(value)));
   }

   public ClientRequest cookie(Cookie cookie)
   {
      return header(HttpHeaders.COOKIE, cookie.toString());
   }

   public ClientRequest pathParameter(String parameterName, Object value)
   {
      if (pathParameters == null) pathParameters = new MultivaluedMapImpl<String, String>();
      pathParameters.add(parameterName, toString(value));
      return this;
   }

   public ClientRequest body(String contentType, Object data)
   {
      return body(MediaType.valueOf(contentType), data, data.getClass(), null, null);
   }

   public ClientRequest body(MediaType contentType, Object data)
   {
      return body(contentType, data, data.getClass(), null, null);
   }

   public ClientRequest body(MediaType contentType, Object data, GenericType genericType)
   {
      return body(contentType, data, genericType.getType(), genericType.getGenericType(), null);
   }

   public ClientRequest body(MediaType contentType, Object data, Type genericType)
   {
      return body(contentType, data, data.getClass(), genericType, null);
   }


   public ClientRequest body(MediaType contentType, Object data, Class type, Type genericType, Annotation[] annotations)
   {
      this.body = data;
      this.bodyContentType = contentType;
      this.bodyGenericType = genericType;
      this.bodyType = type;
      this.bodyAnnotations = annotations;
      return this;
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return providerFactory;
   }

   public ClientExecutor getExecutor()
   {
      return executor;
   }

   public MultivaluedMap<String, String> getHeaders()
   {
      return headers;
   }

   public MultivaluedMap<String, String> getQueryParameters()
   {
      return queryParameters;
   }

   public MultivaluedMap<String, String> getFormParameters()
   {
      return formParameters;
   }

   public MultivaluedMap<String, String> getPathParameters()
   {
      return pathParameters;
   }

   public MultivaluedMap<String, String> getMatrixParameters()
   {
      return matrixParameters;
   }

   public Object getBody()
   {
      return body;
   }

   public Class getBodyType()
   {
      return bodyType;
   }

   public Type getBodyGenericType()
   {
      return bodyGenericType;
   }

   public Annotation[] getBodyAnnotations()
   {
      return bodyAnnotations;
   }

   public MediaType getBodyContentType()
   {
      return bodyContentType;
   }

   public ClientResponse get() throws Exception
   {
      return executor.execute("GET", this);
   }

   public <T> ClientResponse<T> get(Class<T> returnType)
           throws Exception
   {
      ClientResponseImpl response = (ClientResponseImpl) get();
      response.setReturnType(returnType);
      return response;
   }

   public <T> ClientResponse<T> get(Class<T> returnType, Type genericType)
           throws Exception
   {
      ClientResponseImpl response = (ClientResponseImpl) get();
      response.setReturnType(returnType);
      response.setGenericReturnType(genericType);
      return response;
   }

   public <T> ClientResponse<T> get(GenericType type)
           throws Exception
   {
      ClientResponseImpl response = (ClientResponseImpl) get();
      response.setReturnType(type.getType());
      response.setGenericReturnType(type.getGenericType());
      return response;
   }

   public ClientResponse head() throws Exception
   {
      return executor.execute("HEAD", this);
   }

   public ClientResponse put() throws Exception
   {
      return executor.execute("PUT", this);
   }

   public <T> ClientResponse<T> put(Class<T> returnType)
           throws Exception
   {
      ClientResponseImpl response = (ClientResponseImpl) put();
      response.setReturnType(returnType);
      return response;
   }

   public <T> ClientResponse<T> put(Class<T> returnType, Type genericType)
           throws Exception
   {
      ClientResponseImpl response = (ClientResponseImpl) put();
      response.setReturnType(returnType);
      response.setGenericReturnType(genericType);
      return response;
   }

   public <T> ClientResponse<T> put(GenericType type)
           throws Exception
   {
      ClientResponseImpl response = (ClientResponseImpl) put();
      response.setReturnType(type.getType());
      response.setGenericReturnType(type.getGenericType());
      return response;
   }

   public ClientResponse post() throws Exception
   {
      return executor.execute("POST", this);
   }

   public <T> ClientResponse<T> post(Class<T> returnType)
           throws Exception
   {
      ClientResponseImpl response = (ClientResponseImpl) post();
      response.setReturnType(returnType);
      return response;
   }

   public <T> ClientResponse<T> post(Class<T> returnType, Type genericType)
           throws Exception
   {
      ClientResponseImpl response = (ClientResponseImpl) post();
      response.setReturnType(returnType);
      response.setGenericReturnType(genericType);
      return response;
   }

   public <T> ClientResponse<T> post(GenericType type)
           throws Exception
   {
      ClientResponseImpl response = (ClientResponseImpl) post();
      response.setReturnType(type.getType());
      response.setGenericReturnType(type.getGenericType());
      return response;
   }

   public ClientResponse delete() throws Exception
   {
      return executor.execute("DELETE", this);
   }

   public <T> ClientResponse<T> delete(Class<T> returnType)
           throws Exception
   {
      ClientResponseImpl response = (ClientResponseImpl) delete();
      response.setReturnType(returnType);
      return response;
   }

   public <T> ClientResponse<T> delete(Class<T> returnType, Type genericType)
           throws Exception
   {
      ClientResponseImpl response = (ClientResponseImpl) delete();
      response.setReturnType(returnType);
      response.setGenericReturnType(genericType);
      return response;
   }

   public <T> ClientResponse<T> delete(GenericType type)
           throws Exception
   {
      ClientResponseImpl response = (ClientResponseImpl) delete();
      response.setReturnType(type.getType());
      response.setGenericReturnType(type.getGenericType());
      return response;
   }

   public ClientResponse options() throws Exception
   {
      return executor.execute("OPTIONS", this);
   }

   public <T> ClientResponse<T> options(Class<T> returnType)
           throws Exception
   {
      ClientResponseImpl response = (ClientResponseImpl) options();
      response.setReturnType(returnType);
      return response;
   }

   public <T> ClientResponse<T> options(Class<T> returnType, Type genericType)
           throws Exception
   {
      ClientResponseImpl response = (ClientResponseImpl) options();
      response.setReturnType(returnType);
      response.setGenericReturnType(genericType);
      return response;
   }

   public <T> ClientResponse<T> options(GenericType type)
           throws Exception
   {
      ClientResponseImpl response = (ClientResponseImpl) options();
      response.setReturnType(type.getType());
      response.setGenericReturnType(type.getGenericType());
      return response;
   }


   public String getUri() throws Exception

   {
      UriBuilderImpl builder = (UriBuilderImpl) uri.clone();
      if (pathParameters != null)
      {
         for (Map.Entry<String, List<String>> entry : pathParameters.entrySet())
         {
            List<String> values = entry.getValue();
            for (String value : values) builder.substitutePathParam(entry.getKey(), value, false);
         }
      }
      if (matrixParameters != null)
      {
         if (matrixParameters != null)
         {
            for (Map.Entry<String, List<String>> entry : matrixParameters.entrySet())
            {
               List<String> values = entry.getValue();
               for (String value : values) builder.matrixParam(entry.getKey(), value);
            }
         }
      }
      if (queryParameters != null)
      {
         for (Map.Entry<String, List<String>> entry : queryParameters.entrySet())
         {
            List<String> values = entry.getValue();
            for (String value : values) builder.queryParam(entry.getKey(), value);
         }
      }

      return builder.build().toString();
   }

}
