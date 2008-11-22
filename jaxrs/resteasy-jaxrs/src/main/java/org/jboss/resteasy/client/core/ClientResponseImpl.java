package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.HttpMethodBase;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.jboss.resteasy.util.GenericType;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientResponseImpl implements ClientResponse
{
   protected ResteasyProviderFactory providerFactory;
   protected HttpMethodBase baseMethod;
   protected Class returnType;
   protected Type genericReturnType;
   protected Annotation[] annotations;
   protected CaseInsensitiveMap<String> headers;
   protected int status;
   protected boolean wasReleased = false;

   public void setProviderFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
   }

   public void setBaseMethod(HttpMethodBase baseMethod)
   {
      this.baseMethod = baseMethod;
   }

   public void setReturnType(Class returnType)
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

   public void setHeaders(CaseInsensitiveMap<String> headers)
   {
      this.headers = headers;
   }

   public void setStatus(int status)
   {
      this.status = status;
   }

   public Object getEntity()
   {
      try
      {
         if (status == HttpResponseCodes.SC_NO_CONTENT) return null;
         String mediaType = headers.getFirst(HttpHeaderNames.CONTENT_TYPE);
         MediaType media = MediaType.valueOf(mediaType);
         MessageBodyReader reader = providerFactory.getMessageBodyReader(returnType, genericReturnType, annotations, media);
         if (reader == null)
         {
            throw new RuntimeException("Unable to find a MessageBodyReader of content-type " + mediaType);
         }
         try
         {
            return reader.readFrom(returnType, genericReturnType, annotations, media, headers, baseMethod.getResponseBodyAsStream());
         }
         catch (Exception e)
         {
            throw new RuntimeException("Failure reading from MessageBodyReader: " + reader.getClass().getName(), e);
         }
      }
      finally
      {
         wasReleased = true;
         baseMethod.releaseConnection();
      }
   }

   public Object getEntity(Class type, Type genericType)
   {
      try
      {
         if (status == HttpResponseCodes.SC_NO_CONTENT) return null;
         String mediaType = headers.getFirst(HttpHeaderNames.CONTENT_TYPE);
         MediaType media = MediaType.valueOf(mediaType);
         MessageBodyReader reader = providerFactory.getMessageBodyReader(type, genericType, null, media);
         if (reader == null)
         {
            throw new RuntimeException("Unable to find a MessageBodyReader of content-type " + mediaType);
         }
         try
         {
            return reader.readFrom(type, genericType, null, media, headers, baseMethod.getResponseBodyAsStream());
         }
         catch (Exception e)
         {
            throw new RuntimeException("Failure reading from MessageBodyReader: " + reader.getClass().getName(), e);
         }
      }
      finally
      {
         wasReleased = true;
         baseMethod.releaseConnection();
      }
   }

   public Object getBody(GenericType genericType)
   {
      return getEntity(genericType.getType(), genericType.getGenericType());
   }

   public MultivaluedMap getHeaders()
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
      if (!wasReleased) baseMethod.releaseConnection();
   }
}
