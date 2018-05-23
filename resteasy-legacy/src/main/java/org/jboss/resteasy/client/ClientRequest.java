package org.jboss.resteasy.client;

import org.jboss.resteasy.client.Link;
import org.jboss.resteasy.client.LinkHeader;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.ClientInterceptorRepositoryImpl;
import org.jboss.resteasy.core.interception.ClientExecutionContextImpl;
import org.jboss.resteasy.core.interception.ClientWriterInterceptorContext;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.jboss.resteasy.spi.ProviderFactoryDelegate;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringConverter;
import org.jboss.resteasy.util.Encode;
import org.jboss.resteasy.util.GenericType;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jboss.resteasy.util.HttpHeaderNames.ACCEPT;

/**
 * Create a hand coded request to send to the server.  You call methods like accept(), body(), pathParameter()
 * etc. to create the state of the request.  Then you call a get(), post(), etc. method to execute the request.
 * After an execution of a request, the internal state remains the same.  You can invoke the request again.
 * You can clear the request with the clear() method.
 *
 * @deprecated The Resteasy client framework in resteasy-jaxrs is replaced by the JAX-RS 2.0 compliant resteasy-client module.
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @see <a href="https://jcp.org/en/jsr/detail?id=339">jaxrs-api</a>
 * @see javax.ws.rs.client.Invocation
 */
@Deprecated
@SuppressWarnings("unchecked")
public class ClientRequest extends ClientInterceptorRepositoryImpl implements Cloneable
{
   protected org.jboss.resteasy.spi.old.ResteasyProviderFactory providerFactory;
   protected ResteasyUriBuilder uri;
   protected ClientExecutor executor;
   protected MultivaluedMap<String, Object> headers;
   protected MultivaluedMap<String, String> queryParameters;
   protected MultivaluedMap<String, String> formParameters;
   protected MultivaluedMap<String, String> pathParameters;
   protected MultivaluedMap<String, String> matrixParameters;
   protected Object body;
   protected Class bodyType;
   protected Type bodyGenericType;
   protected Annotation[] bodyAnnotations;
   protected MediaType bodyContentType;
   protected boolean followRedirects;
   protected String httpMethod;
   protected String finalUri;
   protected List<String> pathParameterList;
   protected LinkHeader linkHeader;
   protected Map<String, Object> attributes = new HashMap<String, Object>();
 
   private static String defaultExecutorClasss = "org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor";

   /**
    * Set the default executor class name.
    *
    * @param classname class name
    */
   public static void setDefaultExecutorClass(String classname)
   {
      defaultExecutorClasss = classname;
   }

   public static ClientExecutor getDefaultExecutor()
   {
      try
      {
         Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(defaultExecutorClasss);
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

   public ClientRequest(String uriTemplate, ClientExecutor executor)
   {
      this(getBuilder(uriTemplate), executor);
   }

   public ClientRequest(UriBuilder uri, ClientExecutor executor)
   {
      this(uri, executor, ResteasyProviderFactory.getInstance());
   }

   public ClientRequest(UriBuilder uri, ClientExecutor executor,
                        ResteasyProviderFactory providerFactory)
   {
      this.uri = (ResteasyUriBuilder) uri;
      this.executor = executor;
      if (providerFactory instanceof ProviderFactoryDelegate)
      {
         this.providerFactory = (org.jboss.resteasy.spi.old.ResteasyProviderFactory) ((ProviderFactoryDelegate) providerFactory)
               .getDelegate();
      }
      else
      {
         this.providerFactory = (org.jboss.resteasy.spi.old.ResteasyProviderFactory) providerFactory;
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
      return new ResteasyUriBuilder().uriTemplate(uriTemplate);
   }

   public boolean followRedirects()
   {
      return followRedirects;
   }

   public Map<String, Object> getAttributes()
   {
      return attributes;
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
      String curr = (String) getHeadersAsObjects().getFirst(ACCEPT);
      if (curr != null)
         curr += "," + accept;
      else
         curr = accept;
      getHeadersAsObjects().putSingle(ACCEPT, curr);
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
      return providerFactory.toHeaderString(object);

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
      getHeadersAsObjects().add(headerName, value);
      return this;
   }

   public ClientRequest cookie(String cookieName, Object value)
   {
      return cookie(new Cookie(cookieName, toString(value)));
   }

   public ClientRequest cookie(Cookie cookie)
   {
      return header(HttpHeaders.COOKIE, cookie);
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

   /**
    * @return a copy of all header objects converted to a string
    */
   public MultivaluedMap<String, String> getHeaders()
   {
      MultivaluedMap<String, String> rtn = new MultivaluedMapImpl<String, String>();
      if (headers == null) return rtn;
      for (Map.Entry<String, List<Object>> entry : headers.entrySet())
      {
         for (Object obj : entry.getValue())
         {
            rtn.add(entry.getKey(), toHeaderString(obj));
         }
      }
      return rtn;
   }

   public MultivaluedMap<String, Object> getHeadersAsObjects()
   {
      if (headers == null)
         headers = new MultivaluedMapImpl<String, Object>();
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
      Providers current = ResteasyProviderFactory.getContextData(Providers.class);
      ResteasyProviderFactory.pushContext(Providers.class, providerFactory);
      try
      {

         if (linkHeader != null) header("Link", linkHeader);

         if (getReaderInterceptorList().isEmpty())
         {
            setReaderInterceptors(providerFactory.getClientReaderInterceptorRegistry().postMatch(null, null));
         }

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
         response.setAttributes(attributes);
         response.setReaderInterceptors(getReaderInterceptors());
         return response;
      }
      finally
      {
         ResteasyProviderFactory.popContextData(Providers.class);
         if (current != null) ResteasyProviderFactory.pushContext(Providers.class, current);

      }
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
         setWriterInterceptors(providerFactory.getClientWriterInterceptorRegistry().postMatch(null, null));
      }
      new ClientWriterInterceptorContext(getWriterInterceptors(), providerFactory, body,
              bodyType, bodyGenericType, bodyAnnotations, bodyContentType,
              headers, outputStream, attributes).proceed();
   }

   public ClientResponse get() throws Exception
   {
      return httpMethod("GET");
   }

   /**
    * Tries to automatically unmarshal to target type.
    *
    * @param returnType return type
    * @param <T> type
    * @return response entity
    * @throws Exception if error occurred
    */
   public <T> T getTarget(Class<T> returnType) throws Exception
   {
      BaseClientResponse<T> response = (BaseClientResponse<T>) get(returnType);
      if (response.getStatus() == 204) return null;
      if (response.getStatus() != 200) throw new ClientResponseFailure(response);
      T obj = response.getEntity();
      if (obj instanceof InputStream)
      {
         response.setWasReleased(true);
      }
      return obj;
   }

   /**
    * Templates the returned ClientResponse for easy access to returned entity
    *
    * @param returnType return type
    * @param <T> type
    * @return response
    * @throws Exception if error occurred
    */
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
      if (response.getStatus() == 204) return null;
      if (response.getStatus() != 200) throw new ClientResponseFailure(response);
      T obj = response.getEntity();
      if (obj instanceof InputStream)
      {
         response.setWasReleased(true);
      }
      return obj;
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

   /**
    * Automatically does POST/Create pattern.  Will throw a ClientResponseFailure
    * if status is something other than 201
    *
    * @return Link to created resource
    * @throws Exception if error occurred
    * @throws ClientResponseFailure if response status is not 201
    */
   public Link create() throws Exception, ClientResponseFailure
   {
      BaseClientResponse response = (BaseClientResponse) post();
      if (response.getStatus() != 201) throw new ClientResponseFailure(response);
      return response.getLocationLink();
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

   public <T> ClientResponse<T> httpMethod(String method, Class<T> returnType,
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
    * @return uri
    * @throws Exception if error occurred
    */
   public String getUri() throws Exception

   {
      if (finalUri != null)
         return finalUri;

      ResteasyUriBuilder builder = (ResteasyUriBuilder) uri.clone();
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
               builder.clientQueryParam(entry.getKey(), value);
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
            {
               value = Encode.encodePathAsIs(value);
               builder.substitutePathParam(entry.getKey(), value, true);
            }
         }
      }
      if (finalUri == null)
         finalUri = builder.build().toString();
      return finalUri;
   }

   public ClientRequest createSubsequentRequest(URI uri)
   {
      try
      {
         ClientRequest clone = (ClientRequest) this.clone();
         clone.clear();
         clone.uri = new ResteasyUriBuilder();
         clone.uri.uri(uri);
         return clone;
      }
      catch (CloneNotSupportedException e)
      {
         // this shouldn't happen
         throw new RuntimeException(Messages.MESSAGES.clientRequestDoesntSupportClonable());
      }
   }
}
