package org.jboss.resteasy.client;

import static org.jboss.resteasy.util.HttpHeaderNames.ACCEPT;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.commons.httpclient.HttpClient;
import org.jboss.resteasy.client.core.ApacheHttpClientExecutor;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.ClientInterceptorRepositoryImpl;
import org.jboss.resteasy.core.interception.ClientExecutionContextImpl;
import org.jboss.resteasy.core.interception.MessageBodyWriterContextImpl;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ProviderFactoryDelegate;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringConverter;
import org.jboss.resteasy.util.GenericType;

/**
 * Create a hand coded request to send to the server.
 * 
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */

@SuppressWarnings("unchecked")
public class ClientRequest extends ClientInterceptorRepositoryImpl
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
   private MessageBodyWriter writer;
   private String httpMethod;
   private String finalUri;
   private List<String> pathParameterList;

   public ClientRequest(String uriTemplate)
   {
      this(uriTemplate, new HttpClient(), ResteasyProviderFactory.getInstance());
   }

   public ClientRequest(String uriTemplate, HttpClient httpClient)
   {
      this(uriTemplate, httpClient, ResteasyProviderFactory.getInstance());
   }

   public ClientRequest(String uriTemplate, HttpClient httpClient,
         ResteasyProviderFactory providerFactory)
   {
      this(new UriBuilderImpl().uriTemplate(uriTemplate),
            new ApacheHttpClientExecutor(httpClient), providerFactory);
   }

   public ClientRequest(UriBuilder uri, ClientExecutor executor,
         ResteasyProviderFactory providerFactory)
   {
      this.uri = (UriBuilderImpl) uri;
      this.executor = executor;
      if (providerFactory instanceof ProviderFactoryDelegate)
      {
         providerFactory = ((ProviderFactoryDelegate) providerFactory)
               .getDelegate();
      }
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
      String curr = getHeaders().getFirst(ACCEPT);
      if (curr != null)
         curr += "," + accept;
      else
         curr = accept;
      getHeaders().putSingle(ACCEPT, curr);
      return this;
   }

   protected String toString(Object object)
   {
      if (object instanceof String)
         return (String) object;
      StringConverter converter = providerFactory.getStringConverter(object
            .getClass());
      if (converter != null)
         return converter.toString(object);
      else
         return object.toString();

   }

   protected String toHeaderString(Object object)
   {
      StringConverter converter = providerFactory.getStringConverter(object
            .getClass());
      if (converter != null)
         return converter.toString(object);

      RuntimeDelegate.HeaderDelegate delegate = providerFactory
            .createHeaderDelegate(object.getClass());
      if (delegate != null)
         return delegate.toString(object);
      else
         return object.toString();

   }

   public ClientRequest formParameter(String parameterName, Object value)
   {
      getFormParameters().add(parameterName, toString(value));
      return this;
   }

   public ClientRequest queryParameter(String parameterName, Object value)
   {
      getQueryParameters().add(parameterName, toString(value));
      return this;
   }

   public ClientRequest matrixParameter(String parameterName, Object value)
   {
      getMatrixParameters().add(parameterName, toString(value));
      return this;
   }

   public ClientRequest header(String headerName, Object value)
   {
      getHeaders().add(headerName, toHeaderString(value));
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
      getPathParameters().add(parameterName, toString(value));
      return this;
   }

   public ClientRequest pathParameters(Object... values)
   {
      for (Object value : values)
      {
         getPathParameterList().add(toString(value));
      }
      return this;
   }

   public ClientRequest body(String contentType, Object data)
   {
      return body(MediaType.valueOf(contentType), data, data.getClass(), null,
            null);
   }

   public ClientRequest body(MediaType contentType, Object data)
   {
      return body(contentType, data, data.getClass(), null, null);
   }

   public ClientRequest body(MediaType contentType, Object data,
         GenericType genericType)
   {
      return body(contentType, data, genericType.getType(), genericType
            .getGenericType(), null);
   }

   public ClientRequest body(MediaType contentType, Object data,
         Type genericType)
   {
      return body(contentType, data, data.getClass(), genericType, null);
   }

   public ClientRequest body(MediaType contentType, Object data, Class type,
         Type genericType, Annotation[] annotations)
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
      if (headers == null)
         headers = new MultivaluedMapImpl<String, String>();
      return headers;
   }

   public MultivaluedMap<String, String> getQueryParameters()
   {
      if (queryParameters == null)
         queryParameters = new MultivaluedMapImpl<String, String>();
      return queryParameters;
   }

   public MultivaluedMap<String, String> getFormParameters()
   {
      if (formParameters == null)
         formParameters = new MultivaluedMapImpl<String, String>();
      return formParameters;
   }

   public MultivaluedMap<String, String> getPathParameters()
   {
      if (pathParameters == null)
         pathParameters = new MultivaluedMapImpl<String, String>();
      return pathParameters;
   }

   public List<String> getPathParameterList()
   {
      if (pathParameterList == null)
         pathParameterList = new ArrayList<String>();
      return pathParameterList;
   }

   public MultivaluedMap<String, String> getMatrixParameters()
   {
      if (matrixParameters == null)
         matrixParameters = new MultivaluedMapImpl<String, String>();
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

   public String getHttpMethod()
   {
      return httpMethod;
   }

   public void setHttpMethod(String httpMethod)
   {
      this.httpMethod = httpMethod;
   }

   public ClientResponse execute() throws Exception
   {
      if (getReaderInterceptorList().isEmpty())
         setReaderInterceptors(providerFactory
               .getClientMessageBodyReaderInterceptorRegistry().bindForList(
                     null, null));

      if (getExecutionInterceptorList().isEmpty())
      {
         setExecutionInterceptors(providerFactory
               .getClientExecutionInterceptorRegistry().bindForList(null, null));
      }

      BaseClientResponse response = null;
      if (getExecutionInterceptorList().isEmpty())
      {
         response = (BaseClientResponse) executor.execute(this);
      }
      else
      {
         ClientExecutionContextImpl ctx = new ClientExecutionContextImpl(
               getExecutionInterceptorList(), executor, this);
         response = (BaseClientResponse) ctx.proceed();
      }
      response.setMessageBodyReaderInterceptors(getReaderInterceptors());
      return response;
   }

   public void writeRequestBody(MultivaluedMap<String, Object> headers,
         OutputStream outputStream) throws IOException
   {
      if (body == null)
         return;
      if (getWriterInterceptorList().isEmpty())
         setWriterInterceptors(providerFactory
               .getClientMessageBodyWriterInterceptorRegistry().bindForList(
                     null, null));
      if (writer == null)
      {
         writer = providerFactory
               .getMessageBodyWriter(getBodyType(), getBodyGenericType(),
                     getBodyAnnotations(), getBodyContentType());
      }

      if (getWriterInterceptorList().isEmpty())
      {
         MessageBodyWriterContextImpl ctx = new MessageBodyWriterContextImpl(
               getWriterInterceptors(), writer, body, bodyType,
               bodyGenericType, bodyAnnotations, bodyContentType, headers,
               outputStream);
         ctx.proceed();
      }
      else
      {
         writer.writeTo(body, bodyType, bodyGenericType, bodyAnnotations,
               bodyContentType, headers, outputStream);
      }

   }

   public ClientResponse get() throws Exception
   {
      return httpMethod("GET");
   }

   public <T> ClientResponse<T> get(Class<T> returnType) throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) get();
      response.setReturnType(returnType);
      return response;
   }

   public <T> ClientResponse<T> get(Class<T> returnType, Type genericType)
         throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) get();
      response.setReturnType(returnType);
      response.setGenericReturnType(genericType);
      return response;
   }

   public <T> ClientResponse<T> get(GenericType type) throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) get();
      response.setReturnType(type.getType());
      response.setGenericReturnType(type.getGenericType());
      return response;
   }

   public ClientResponse head() throws Exception
   {
      return httpMethod("HEAD");
   }

   public ClientResponse put() throws Exception
   {
      return httpMethod("PUT");
   }

   public <T> ClientResponse<T> put(Class<T> returnType) throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) put();
      response.setReturnType(returnType);
      return response;
   }

   public <T> ClientResponse<T> put(Class<T> returnType, Type genericType)
         throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) put();
      response.setReturnType(returnType);
      response.setGenericReturnType(genericType);
      return response;
   }

   public <T> ClientResponse<T> put(GenericType type) throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) put();
      response.setReturnType(type.getType());
      response.setGenericReturnType(type.getGenericType());
      return response;
   }

   public ClientResponse post() throws Exception
   {
      return httpMethod("POST");
   }

   public <T> ClientResponse<T> post(Class<T> returnType) throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) post();
      response.setReturnType(returnType);
      return response;
   }

   public <T> ClientResponse<T> post(Class<T> returnType, Type genericType)
         throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) post();
      response.setReturnType(returnType);
      response.setGenericReturnType(genericType);
      return response;
   }

   public <T> ClientResponse<T> post(GenericType type) throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) post();
      response.setReturnType(type.getType());
      response.setGenericReturnType(type.getGenericType());
      return response;
   }

   public ClientResponse delete() throws Exception
   {
      return httpMethod("DELETE");
   }

   public <T> ClientResponse<T> delete(Class<T> returnType) throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) delete();
      response.setReturnType(returnType);
      return response;
   }

   public <T> ClientResponse<T> delete(Class<T> returnType, Type genericType)
         throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) delete();
      response.setReturnType(returnType);
      response.setGenericReturnType(genericType);
      return response;
   }

   public <T> ClientResponse<T> delete(GenericType type) throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) delete();
      response.setReturnType(type.getType());
      response.setGenericReturnType(type.getGenericType());
      return response;
   }

   public ClientResponse options() throws Exception
   {
      return httpMethod("OPTIONS");
   }

   public <T> ClientResponse<T> options(Class<T> returnType) throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) options();
      response.setReturnType(returnType);
      return response;
   }

   public <T> ClientResponse<T> options(Class<T> returnType, Type genericType)
         throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) options();
      response.setReturnType(returnType);
      response.setGenericReturnType(genericType);
      return response;
   }

   public <T> ClientResponse<T> options(GenericType type) throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) options();
      response.setReturnType(type.getType());
      response.setGenericReturnType(type.getGenericType());
      return response;
   }

   public ClientResponse httpMethod(String httpMethod) throws Exception
   {
      this.httpMethod = httpMethod;
      return execute();
   }

   public <T> ClientResponse<T> httpMethod(String method, Class<T> returnType)
         throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) httpMethod(method);
      response.setReturnType(returnType);
      return response;
   }

   public <T> ClientResponse<T> httpmethod(String method, Class<T> returnType,
         Type genericType) throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) httpMethod(method);
      response.setReturnType(returnType);
      response.setGenericReturnType(genericType);
      return response;
   }

   public <T> ClientResponse<T> httpMethod(String method, GenericType type)
         throws Exception
   {
      BaseClientResponse response = (BaseClientResponse) httpMethod(method);
      response.setReturnType(type.getType());
      response.setGenericReturnType(type.getGenericType());
      return response;
   }

   public void overrideUri(URI uri)
   {
      this.uri.uri(uri);
   }

   /**
    * This method populates all path, matrix, and query parameters and saves it
    * internally. Once its called once it returns the cached value.
    * 
    * @return
    * @throws Exception
    */
   public String getUri() throws Exception

   {
      if (finalUri != null)
         return finalUri;

      UriBuilderImpl builder = (UriBuilderImpl) uri.clone();
      if (matrixParameters != null)
      {
         if (matrixParameters != null)
         {
            for (Map.Entry<String, List<String>> entry : matrixParameters
                  .entrySet())
            {
               List<String> values = entry.getValue();
               for (String value : values)
                  builder.matrixParam(entry.getKey(), value);
            }
         }
      }
      if (queryParameters != null)
      {
         for (Map.Entry<String, List<String>> entry : queryParameters
               .entrySet())
         {
            List<String> values = entry.getValue();
            for (String value : values)
               builder.queryParam(entry.getKey(), value);
         }
      }
      if (pathParameterList != null && !pathParameterList.isEmpty())
      {
         finalUri = builder.build(pathParameterList.toArray()).toString();
      }
      else if (pathParameters != null && !pathParameters.isEmpty())
      {
         for (Map.Entry<String, List<String>> entry : pathParameters.entrySet())
         {
            List<String> values = entry.getValue();
            for (String value : values)
               builder.substitutePathParam(entry.getKey(), value, false);
         }
      }
      if (finalUri == null)
         finalUri = builder.build().toString();
      return finalUri;
   }

}
