package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.HttpMethodBase;
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
import javax.ws.rs.ext.MessageBodyReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

//import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientResponseImpl<T> extends ClientResponse<T>
{
   protected ResteasyProviderFactory providerFactory;

   protected String attributeExceptionsTo;
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
   protected boolean performExecute = true;

   protected boolean cacheInputStream;

   protected HttpMethodBase httpMethod;

   public void setStatus(int status)
   {
      this.status = status;
   }

   public void setHttpMethod(HttpMethodBase httpMethod)
   {
      this.httpMethod = httpMethod;
   }

   public void setHeaders(CaseInsensitiveMap<String> headers)
   {
      this.headers = headers;
   }

   public void setProviderFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
   }

   public void setReturnType(Class<T> returnType)
   {
      this.returnType = returnType;
   }

   public Class<?> getReturnType()
   {
      return returnType;
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

   public Annotation[] getAnnotations()
   {
      return this.annotations;
   }

   public String getResponseHeader(String headerKey)
   {
      if (headers == null) return null;
      return headers.getFirst(headerKey);
   }

   public void setAlternateMediaType(String alternateMediaType)
   {
      this.alternateMediaType = alternateMediaType;
   }

   @SuppressWarnings("unchecked")
   @Override
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
   @Override
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
         MediaType media = mediaType == null ? MediaType.WILDCARD_TYPE : MediaType.valueOf(mediaType);

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
         releaseConnection();
      }
   }

   @Override
   public <T2> T2 getEntity(GenericType<T2> genericType)
   {
      return getEntity(genericType.getType(), genericType.getGenericType());
   }

   public MultivaluedMap<String, String> getHeaders()
   {
      return headers;
   }


   @Override
   public MultivaluedMap<String, Object> getMetadata()
   {
      MultivaluedMap map = headers;
      return (MultivaluedMap<String, Object>) map;
   }

   @Override
   public int getStatus()
   {
      return status;
   }

   @Override
   protected void finalize() throws Throwable
   {
      releaseConnection();
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

   @Override
   public Status getResponseStatus()
   {
      return Response.Status.fromStatusCode(getStatus());
   }

   public boolean wasReleased()
   {
      return wasReleased;
   }

   protected InputStream getInputStream() throws IOException
   {
      return httpMethod.getResponseBodyAsStream();
   }

   public void releaseConnection()
   {
      if (!wasReleased)
      {
         httpMethod.releaseConnection();
         wasReleased = true;
      }
   }

}
