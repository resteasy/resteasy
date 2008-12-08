package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.jboss.resteasy.util.GenericType;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;

//import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientResponseImpl<T> implements ClientResponse<T>
{
   protected ResteasyProviderFactory providerFactory;

   protected String attributeExceptionsTo;
   protected Iterable<ClientInterceptor> interceptors = Collections.emptyList();

   protected String restVerb;
   protected String url;
   protected HttpMethodBase baseMethod;
   protected CaseInsensitiveMap<String> headers;

   protected String alternateMediaType;
   protected Class<?> returnType;
   protected Type genericReturnType;
   protected Annotation[] annotations;

   protected int status;

   protected boolean wasReleased = false;
   protected boolean streamWasRead = false;
   protected byte[] rawResults;
   protected Object unmarshaledEntity;

   protected Exception exception;

   // These can only be set by an interceptor
   protected boolean allowRereads = false;
   protected boolean performExecute = true;

   protected boolean cacheInputStream;

   public void setProviderFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
   }

   public void setReturnType(Class<T> returnType)
   {
      this.returnType = returnType;
   }

   public void setGenericReturnType(Type genericReturnType)
   {
      this.genericReturnType = genericReturnType;
   }

   public void setAnnotations(Annotation[] annotations)
   {
      this.annotations = annotations;
   }

   public String getAttributeExceptionsTo()
   {
      return attributeExceptionsTo;
   }

   public void setAttributeExceptionsTo(String attributeExceptionsTo)
   {
      this.attributeExceptionsTo = attributeExceptionsTo;
   }

   public Exception getException()
   {
      return exception;
   }

   public void setException(Exception exception)
   {
      this.exception = exception;
   }

   public void setInterceptors(Iterable<ClientInterceptor> interceptors)
   {
      this.interceptors = interceptors;
   }

   public Header getContentTypeHeader()
   {
      return baseMethod.getResponseHeader(HttpHeaderNames.CONTENT_TYPE);
   }

   public String getContentType()
   {
      Header contentTypeHeader = getContentTypeHeader();
      return contentTypeHeader == null ? null : contentTypeHeader.getValue();
   }

   public Annotation[] getAnnotations()
   {
      return this.annotations;
   }

   public String getResponseHeader(String headerKey)
   {
      return headers.getFirst(headerKey);
   }

   public void setAlternateMediaType(String alternateMediaType)
   {
      this.alternateMediaType = alternateMediaType;
   }

   public HttpMethodBase getHttpBaseMethod()
   {
      return this.baseMethod;
   }

   public boolean isAllowRereads()
   {
      return allowRereads;
   }

   public void setAllowRereads(boolean allowRereads)
   {
      this.allowRereads = allowRereads;
   }

   public boolean isPerformExecute()
   {
      return performExecute;
   }

   public void setPerformExecute(boolean performExecute)
   {
      this.performExecute = performExecute;
   }

   @SuppressWarnings("unchecked")
   public T getEntity()
   {
      if (exception != null)
      {
         throw new RuntimeException("Unable to unmarshall response for "
                 + attributeExceptionsTo, exception);
      }
      if (returnType == null)
      {
         throw new RuntimeException(
                 "No type information to extract entity with, use other getEntity() methods");
      }
      return (T) getEntity(returnType, genericReturnType);
   }

   @SuppressWarnings("unchecked")
   public <T2> T2 getEntity(Class<T2> type, Type genericType)
   {
      if (streamWasRead)
      {
         if (unmarshaledEntity != null)
         {
            if (type.isInstance(this.unmarshaledEntity))
            {
               return (T2) unmarshaledEntity;
            }
            else
            {
               throw new RuntimeException("The entity was already read, and it was of type "
                       + unmarshaledEntity.getClass());
            }
         }
         else
         {
            throw new RuntimeException("Stream was already read");
         }
      }
      try
      {
         if (status == HttpResponseCodes.SC_NO_CONTENT) return null;
         String mediaType = headers.getFirst(HttpHeaderNames.CONTENT_TYPE);
         if (mediaType == null)
         {
            mediaType = alternateMediaType;
         }
         MediaType media = MediaType.valueOf(mediaType);

         Annotation[] annotations = null;
         if (this.returnType == type && this.genericReturnType == genericType)
         {
            annotations = this.annotations;
         }

         MessageBodyReader<T2> reader = providerFactory.getMessageBodyReader(
                 type, genericType, annotations, media);
         if (reader == null)
         {
            throw createResponseFailure("Unable to find a MessageBodyReader of content-type "
                    + mediaType + " and type " + type.getName());
         }
         try
         {
            streamWasRead = true;
            unmarshaledEntity = reader.readFrom(type, genericType, annotations, media, headers, getInputStream());
            for (ClientInterceptor<T> clientInterceptor : interceptors)
            {
               clientInterceptor.postUnMarshalling(this);
            }
            return (T2) unmarshaledEntity;
         }
         catch (Exception e)
         {
            this.exception = e;
            throw createResponseFailure(
                    "Failure reading from MessageBodyReader: "
                            + reader.getClass().getName(), e);
         }
      }
      finally
      {
         if (!wasReleased)
         {
            baseMethod.releaseConnection();
            wasReleased = true;
         }
      }
   }

   private InputStream getInputStream() throws IOException
   {
      return baseMethod.getResponseBodyAsStream();
      /*
      if (allowRereads)
      {
         this.rawResults = StreamUtil.getBytes(baseMethod.getResponseBodyAsStream(), false);
         return new ByteInputStream(rawResults, 0);
      }
      else
      {
      }
      */
   }

   public <T2> T2 getEntity(GenericType<T2> genericType)
   {
      return getEntity(genericType.getType(), genericType.getGenericType());
   }

   public MultivaluedMap<String, String> getHeaders()
   {
      return headers;
   }

   public int getStatus()
   {
      return status;
   }

   @Override
   protected void finalize() throws Throwable
   {
      if (!wasReleased)
      {
         baseMethod.releaseConnection();
         wasReleased = true;
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

   public void setUrl(String url)
   {
      this.url = url;
      for (ClientInterceptor<T> clientInterceptor : interceptors)
      {
         clientInterceptor.preBaseMethodConstruction(this);
      }
      this.baseMethod = createBaseMethodHelper(url);
   }

   public int execute(HttpClient client)
   {
      try
      {
         for (ClientInterceptor<T> clientInterceptor : interceptors)
         {
            clientInterceptor.preExecute(this);
         }

         // one of the interceptors can set performExecute to false, for
         // example, a caching interceptor.
         if (performExecute)
         {
            status = client.executeMethod(baseMethod);
            headers = extractHeaders(baseMethod);
            for (ClientInterceptor<T> clientInterceptor : interceptors)
            {
               clientInterceptor.postExecute(this);
            }
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException("Failed to execute GET request: " + url, e);
      }
      return status;
   }

   public static CaseInsensitiveMap<String> extractHeaders(
           HttpMethodBase baseMethod)
   {
      final CaseInsensitiveMap<String> headers = new CaseInsensitiveMap<String>();

      for (Header header : baseMethod.getResponseHeaders())
      {
         headers.add(header.getName(), header.getValue());
      }
      return headers;
   }

   private HttpMethodBase createBaseMethodHelper(String url)
   {
      if ("GET".equals(restVerb))
      {
         return new GetMethod(url);
      }
      else if ("POST".equals(restVerb))
      {
         return new PostMethod(url);
      }
      else if ("PUT".equals(restVerb))
      {
         return new PutMethod(url);
      }
      else if ("DELETE".equals(restVerb))
      {
         return new DeleteMethod(url);
      }
      return null;
   }

   public void checkFailureStatus()
   {
      if (status > 399 && status < 599)
      {
         throw createResponseFailure("Error status " + status + " "
                 + Response.Status.fromStatusCode(status) + " returned");
      }
   }

   public ClientResponseFailure createResponseFailure(String message)
   {
      return createResponseFailure(message, null);
   }

   @SuppressWarnings("unchecked")
   public ClientResponseFailure createResponseFailure(String message,
                                                      Exception e)
   {
      setException(e);
      this.returnType = byte[].class;
      this.genericReturnType = null;
      return new ClientResponseFailure(message, e,
              (ClientResponse<byte[]>) this);
   }

   public void releaseConnection()
   {
      baseMethod.releaseConnection();
   }

   public Status getResponseStatus()
   {
      return Response.Status.fromStatusCode(getStatus());
   }

}
