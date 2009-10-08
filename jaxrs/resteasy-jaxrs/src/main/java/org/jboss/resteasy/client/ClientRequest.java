package org.jboss.resteasy.client;

import org.apache.commons.httpclient.HttpClient;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.ClientInterceptorRepositoryImpl;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jboss.resteasy.core.interception.ClientExecutionContextImpl;
import org.jboss.resteasy.core.interception.MessageBodyWriterContextImpl;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ProviderFactoryDelegate;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringConverter;
import org.jboss.resteasy.spi.LinkHeader;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.util.GenericType;
import static org.jboss.resteasy.util.HttpHeaderNames.*;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Create a hand coded request to send to the server.  You call methods like accept(), body(), pathParameter()
 * etc. to create the state of the request.  Then you call a get(), post(), etc. method to execute the request.
 * After an execution of a request, the internal state remains the same.  You can invoke the request again.
 * You can clear the request with the clear() method.
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
   private String httpMethod;
   private String finalUri;
   private List<String> pathParameterList;
   private LinkHeader linkHeader;

   private static String defaultExecutorClasss = "org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor";
   //private static String defaultExecutorClasss = "org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor";

   /**
    * Set the default executor class name.
    *
    * @param classname
    * @param createPerRequestInstance whether the instance can be used by every request
    */
   public static void setDefaultExecutorClass(String classname, boolean createPerRequestInstance)
   {
      synchronized (lock)
      {
         defaultExecutorClasss = classname;
         defaultExecutor = null;
         createPerInstance = createPerRequestInstance;
      }
   }

   private static volatile boolean createPerInstance = true;

   private static volatile ClientExecutor defaultExecutor = null;

   private static final Object lock = new Object();

   private static ClientExecutor getDefaultExecutor()
   {
      if (createPerInstance) return createDefaultExecutorInstance();
      ClientExecutor result = defaultExecutor;
      if (result == null)
      {
         synchronized (lock)
         {
            result = defaultExecutor;
            if (result == null)
            {
               defaultExecutor = result = createDefaultExecutorInstance();
            }
         }
      }
      return result;
   }

   private static ClientExecutor createDefaultExecutorInstance()
   {
      try
      {
         Class clazz = Thread.currentThread().getContextClassLoader().loadClass(defaultExecutorClasss);
         return (ClientExecutor) clazz.newInstance();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public ClientRequest(String uriTemplate)
   {
      this(uriTemplate, getDefaultExecutor());
   }

   @Deprecated
   public ClientRequest(String uriTemplate, HttpClient httpClient)
   {
      this(uriTemplate, new ApacheHttpClientExecutor(httpClient));
   }

   public ClientRequest(String uriTemplate, ClientExecutor executor)
   {
      this(getBuilder(uriTemplate), executor);
   }

   @Deprecated
   public ClientRequest(String uriTemplate, HttpClient httpClient,
                        ResteasyProviderFactory providerFactory)
   {
      this(getBuilder(uriTemplate), new ApacheHttpClientExecutor(httpClient),
              providerFactory);
   }

   public ClientRequest(UriBuilder uri, ClientExecutor executor)
   {
      this(uri, executor, ResteasyProviderFactory.getInstance());
   }

   public ClientRequest(UriBuilder uri, ClientExecutor executor,
                        ResteasyProviderFactory providerFactory)
   {
      this.uri = (UriBuilderImpl) uri;
      this.executor = executor;
      if (providerFactory instanceof ProviderFactoryDelegate)
      {
         this.providerFactory = ((ProviderFactoryDelegate) providerFactory)
                 .getDelegate();
      }
      else
      {
         this.providerFactory = providerFactory;
      }
   }

   /**
    * Clear this request's state so that it can be re-used
    */
   public void clear()
   {
      headers = null;
      queryParameters = null;
      formParameters = null;
      pathParameters = null;
      matrixParameters = null;
      body = null;
      bodyType = null;
      bodyGenericType = null;
      bodyAnnotations = null;
      bodyContentType = null;
      httpMethod = null;
      finalUri = null;
      pathParameterList = null;
      linkHeader = null;

   }


   private static UriBuilder getBuilder(String uriTemplate)
   {
      return new UriBuilderImpl().uriTemplate(uriTemplate);
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

   public ClientRequest addLink(Link link)
   {
      if (linkHeader == null)
      {
         linkHeader = new LinkHeader();
      }
      linkHeader.getLinks().add(link);
      return this;
   }

   public ClientRequest addLink(String title, String rel, String href, String type)
   {
      Link link = new Link(title, rel, href, type, null);
      return addLink(link);
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
      if (linkHeader != null) header("Link", linkHeader);
      
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
      {
         return;
      }

      if (getWriterInterceptorList().isEmpty())
      {
         setWriterInterceptors(providerFactory
                 .getClientMessageBodyWriterInterceptorRegistry().bindForList(
                 null, null));
      }
      MessageBodyWriter writer = providerFactory
              .getMessageBodyWriter(bodyType, bodyGenericType,
                      bodyAnnotations, bodyContentType);
      if (writer == null)
      {
         throw new RuntimeException("could not find writer for content-type "
                 + bodyContentType + " type: " + bodyType.getName());
      }
      new MessageBodyWriterContextImpl(getWriterInterceptors(), writer, body,
              bodyType, bodyGenericType, bodyAnnotations, bodyContentType,
              headers, outputStream).proceed();
   }

   public ClientResponse get() throws Exception
   {
      return httpMethod("GET");
   }

   /**
    * Tries to automatically unmarshal to target type.
    *
    * @param returnType
    * @param <T>
    * @return
    * @throws Exception
    */
   public <T> T getTarget(Class<T> returnType) throws Exception
   {
      BaseClientResponse<T> response = (BaseClientResponse<T>) get(returnType);
      return response.getEntity();
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

   public <T> T postTarget(Class<T> returnType) throws Exception
   {
      BaseClientResponse<T> response = (BaseClientResponse<T>) post(returnType);
      return response.getEntity();
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
         for (Map.Entry<String, List<String>> entry : matrixParameters
                 .entrySet())
         {
            List<String> values = entry.getValue();
            for (String value : values)
               builder.matrixParam(entry.getKey(), value);
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
