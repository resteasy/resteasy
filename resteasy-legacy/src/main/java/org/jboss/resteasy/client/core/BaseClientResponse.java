package org.jboss.resteasy.client.core;

import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.Link;
import org.jboss.resteasy.client.LinkHeader;
import org.jboss.resteasy.client.LinkHeaderDelegate;
import org.jboss.resteasy.core.ProvidersContextRetainer;
import org.jboss.resteasy.core.interception.ClientReaderInterceptorContext;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.MarshalledEntity;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.jboss.resteasy.util.GenericType;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.InputStreamToByteArray;
import org.jboss.resteasy.util.ReadFromStream;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.ext.ReaderInterceptor;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class BaseClientResponse<T> extends ClientResponse<T>
{
   public static interface BaseClientResponseStreamFactory
   {
      InputStream getInputStream() throws IOException;

      void performReleaseConnection();
   }

   protected ResteasyProviderFactory providerFactory;
   protected String attributeExceptionsTo;
   protected MultivaluedMap<String, String> headers;
   protected String alternateMediaType;
   protected Class<?> returnType;
   protected Type genericReturnType;
   protected Annotation[] annotations = {};
   protected int status;
   protected boolean wasReleased = false;
   protected Object unmarshaledEntity;
   protected ReaderInterceptor[] readerInterceptors;
   protected Exception exception;// These can only be set by an interceptor
   protected BaseClientResponseStreamFactory streamFactory;
   protected LinkHeader linkHeader;
   protected Link location;
   protected ClientExecutor executor;
   protected Map<String, Object> attributes;

   public BaseClientResponse(BaseClientResponseStreamFactory streamFactory, ClientExecutor executor)
   {
      this.streamFactory = streamFactory;
      this.executor = executor;
   }

   public BaseClientResponse(BaseClientResponseStreamFactory streamFactory)
   {
      this.streamFactory = streamFactory;
   }

   /**
    * Store entity within a byte array input stream because we want to release the connection
    * if a ClientResponseFailure is thrown.  Copy status and headers, but ignore
    * all type information stored in the ClientResponse.
    *
    * @param copy client response
    * @return client response
    */
   public static ClientResponse copyFromError(ClientResponse copy)
   {
      if (copy instanceof BaseClientResponse)
      {
         BaseClientResponse base = (BaseClientResponse) copy;
         InputStream is = null;
         if (copy.getResponseHeaders().containsKey("Content-Type"))
         {
            try
            {
               is = base.streamFactory.getInputStream();
               if (is != null)
               {
                  byte[] bytes = ReadFromStream.readFromStream(1024, is);
                  is = new ByteArrayInputStream(bytes);
               }
            }
            catch (IOException e)
            {
               // ignored
            }
         }

         final InputStream theIs = is;
         BaseClientResponse tmp = new BaseClientResponse(new BaseClientResponse.BaseClientResponseStreamFactory()
         {
            InputStream stream;

            public InputStream getInputStream() throws IOException
            {
               return theIs;
            }

            public void performReleaseConnection()
            {
            }
         });
         tmp.executor = base.executor;
         tmp.status = base.status;
         tmp.providerFactory = base.providerFactory;
         tmp.headers = new CaseInsensitiveMap<String>();
         tmp.headers.putAll(base.headers);
         tmp.readerInterceptors = base.readerInterceptors;
         return tmp;
      }
      else
      {
         // Not sure how this codepath could ever be reached.
         InputStream is = null;
         if (copy.getResponseHeaders().containsKey("Content-Type"))
         {
            GenericType<byte[]> gt = new GenericType<byte[]>()
            {
            };
            try
            {
               byte[] bytes = (byte[]) copy.getEntity(gt);
               is = new ByteArrayInputStream(bytes);
            }
            catch (Exception ignore)
            {
            }
         }
         final InputStream theIs = is;
         BaseClientResponse tmp = new BaseClientResponse(new BaseClientResponse.BaseClientResponseStreamFactory()
         {
            InputStream stream;

            public InputStream getInputStream() throws IOException
            {
               return theIs;
            }

            public void performReleaseConnection()
            {
            }
         });
         tmp.status = copy.getStatus();
         tmp.providerFactory = ResteasyProviderFactory.getInstance();
         tmp.headers = new CaseInsensitiveMap<String>();
         tmp.headers.putAll(copy.getResponseHeaders());
         tmp.headers.remove("Content-Encoding"); // remove encoding because we will have already extracted byte array
         return tmp;
      }
   }

   @Override
   public Map<String, Object> getAttributes()
   {
      return attributes;
   }

   public void setAttributes(Map<String, Object> attributes)
   {
      this.attributes = attributes;
   }

   public void setReaderInterceptors(ReaderInterceptor[] readerInterceptors)
   {
      this.readerInterceptors = readerInterceptors;
   }

   public void setStatus(int status)
   {
      this.status = status;
   }

   public void setHeaders(MultivaluedMap<String, String> headers)
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

   public LinkHeader getLinkHeader()
   {
      if (linkHeader != null) return linkHeader;
      linkHeader = new LinkHeader();
      if (!headers.containsKey("Link"))
      {
         return linkHeader;
      }
      List<String> links = headers.get("Link");
      LinkHeaderDelegate delegate = new LinkHeaderDelegate();
      for (String link : links)
      {
         LinkHeader tmp = delegate.fromString(link);
         linkHeader.getLinks().addAll(tmp.getLinks());
         linkHeader.getLinksByRelationship().putAll(tmp.getLinksByRelationship());
         linkHeader.getLinksByTitle().putAll(tmp.getLinksByTitle());

      }
      for (Link link : linkHeader.getLinks())
      {
         link.setExecutor(executor);
      }
      return linkHeader;
   }

   public Link getLocationLink()
   {
      if (location != null) return location;
      if (!headers.containsKey("Location")) return null;
      String header = headers.getFirst("Location");

      location = new Link();
      location.setHref(header);
      location.setExecutor(executor);

      return location;
   }

   @Override
   public Link getHeaderAsLink(String headerName)
   {
      String value = headers.getFirst(headerName);
      if (value == null) return null;
      String type = headers.getFirst(headerName + "-type");
      Link link = new Link();
      link.setHref(value);
      link.setType(type);
      link.setExecutor(executor);
      return link;
   }

   public void setAlternateMediaType(String alternateMediaType)
   {
      this.alternateMediaType = alternateMediaType;
   }

   public BaseClientResponseStreamFactory getStreamFactory()
   {
      return streamFactory;
   }

   public void setStreamFactory(BaseClientResponseStreamFactory streamFactory)
   {
      this.streamFactory = streamFactory;
   }

   @Override
   public void resetStream()
   {
      try
      {
          if (this.streamFactory.getInputStream().markSupported())
          {
             this.streamFactory.getInputStream().reset();
          }
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public T getEntity()
   {
      if (returnType == null)
      {
         throw new RuntimeException(Messages.MESSAGES.noTypeInformationForEntity());
      }
      return (T) getEntity(returnType, genericReturnType, this.annotations);
   }

   @Override
   public <T2> T2 getEntity(Class<T2> type)
   {
      return getEntity(type, null);
   }

   @Override
   public <T2> T2 getEntity(Class<T2> type, Type genericType)
   {
      return getEntity(type, genericType, getAnnotations(type, genericType));
   }

   private <T2> Annotation[] getAnnotations(Class<T2> type, Type genericType)
   {
      if (this.annotations != null)
      {
         return this.annotations;
      }
      return (this.returnType == type && this.genericReturnType == genericType) ? this.annotations
              : null;
   }

   @Override
   public <T2> T2 getEntity(Class<T2> type, Type genericType, Annotation[] anns)
   {
      if (exception != null)
      {
         throw new RuntimeException(Messages.MESSAGES.unableToUnmarshalResponse(attributeExceptionsTo), exception);
      }

      if (unmarshaledEntity != null && !type.isInstance(this.unmarshaledEntity))
         throw new RuntimeException(Messages.MESSAGES.entityAlreadyRead(unmarshaledEntity.getClass()));

      if (unmarshaledEntity == null)
      {
         if (status == HttpResponseCodes.SC_NO_CONTENT)
            return null;

         unmarshaledEntity = readFrom(type, genericType, getMediaType(), anns);
         // only release connection if we actually unmarshalled something and if the object is *NOT* an InputStream
         // If it is an input stream, the user may be doing their own stream processing.
         if (unmarshaledEntity != null && !InputStream.class.isInstance(unmarshaledEntity)) releaseConnection();
      }
      return (T2) unmarshaledEntity;
   }

   public MediaType getMediaType()
   {
      String mediaType = getResponseHeader(HttpHeaderNames.CONTENT_TYPE);
      if (mediaType == null)
      {
         mediaType = alternateMediaType;
      }

      return mediaType == null ? MediaType.WILDCARD_TYPE : MediaType.valueOf(mediaType);
   }

   // this is synchronized in conjunction with finalize to protect against premature finalize called by the GC
   protected synchronized <T2> Object readFrom(Class<T2> type, Type genericType,
                                  MediaType media, Annotation[] annotations)
   {
      Type useGeneric = genericType == null ? type : genericType;
      Class<?> useType = type;
      boolean isMarshalledEntity = false;
      if (type.equals(MarshalledEntity.class))
      {
         isMarshalledEntity = true;
         ParameterizedType param = (ParameterizedType) useGeneric;
         useGeneric = param.getActualTypeArguments()[0];
         useType = Types.getRawType(useGeneric);
      }

      Providers current = ResteasyProviderFactory.getContextData(Providers.class);
      ResteasyProviderFactory.pushContext(Providers.class, providerFactory);
      Object obj = null;
      try
      {
         InputStream is = streamFactory.getInputStream();
         if (is == null)
         {
            throw new ClientResponseFailure(Messages.MESSAGES.inputStreamEmpty(), this);
         }
         if (isMarshalledEntity)
         {
            is = new InputStreamToByteArray(is);

         }

         final Object finalObj = new ClientReaderInterceptorContext(readerInterceptors, providerFactory, useType,
               useGeneric, annotations, media, getResponseHeaders(), new InputStreamWrapper(is), attributes)
               .proceed();
         obj = finalObj;
         if (isMarshalledEntity)
         {
            InputStreamToByteArray isba = (InputStreamToByteArray) is;
            final byte[] bytes = isba.toByteArray();
            return new MarshalledEntity()
            {
               @Override
               public byte[] getMarshalledBytes()
               {
                  return bytes;
               }

               @Override
               public Object getEntity()
               {
                  return finalObj;
               }
            };
         }
         else
         {
            return (T2) finalObj;
         }

      }
      catch (Exception e)
      {
         if (e instanceof ReaderException)
         {
            throw (ReaderException) e;
         }
         else
         {
            throw new ReaderException(e);
         }
      }
      finally
      {
         ResteasyProviderFactory.popContextData(Providers.class);
         if (current != null) ResteasyProviderFactory.pushContext(Providers.class, current);
         if (obj instanceof ProvidersContextRetainer)
         {
            ((ProvidersContextRetainer) obj).setProviders(providerFactory);
         }
      }
   }

   private static class InputStreamWrapper extends FilterInputStream {
      protected InputStreamWrapper(InputStream in) {
         super(in);
      }

      @Override
      public void close() throws IOException {
      }
   }

   @Override
   public <T2> T2 getEntity(GenericType<T2> genericType)
   {
      return getEntity(genericType.getType(), genericType.getGenericType());
   }

   @Override
   public <T2> T2 getEntity(GenericType<T2> genericType, Annotation[] ann)
   {
      return getEntity(genericType.getType(), genericType.getGenericType(), ann);
   }

   public MultivaluedMap<String, String> getResponseHeaders()
   {
      return headers;
   }

   @Override
   public MultivaluedMap<String, Object> getMetadata()
   {
      // hack to cast from <String, String> to <String, Object>
      return (MultivaluedMap) headers;
   }

   @Override
   public int getStatus()
   {
      return status;
   }

   @Override
   public StatusType getStatusInfo()
   {
      StatusType statusType = Status.fromStatusCode(status);
      if (statusType == null)
      {
         statusType = new StatusType()
         {
            @Override
            public int getStatusCode()
            {
               return status;
            }

            @Override
            public Status.Family getFamily()
            {
               return Status.Family.OTHER;
            }

            @Override
            public String getReasonPhrase()
            {
               return "Unknown Code";
            }
         };
      }
      return statusType;
   }

   public void checkFailureStatus()
   {
      if (status > 399 && status < 599)
      {
//         throw createResponseFailure(format("Error status %d %s returned", status, getResponseStatus()));
         throw createResponseFailure(Messages.MESSAGES.clientResponseFailureStatus(status, getResponseStatus()));
      }
   }

   public ClientResponseFailure createResponseFailure(String message)
   {
      return createResponseFailure(message, null);
   }

   public ClientResponseFailure createResponseFailure(String message, Exception e)
   {
      setException(e);
      this.returnType = byte[].class;
      this.genericReturnType = null;
      this.annotations = null;
      return new ClientResponseFailure(message, e, (ClientResponse<byte[]>) this);
   }

   @Override
   public Status getResponseStatus()
   {
      return Status.fromStatusCode(getStatus());
   }

   public boolean wasReleased()
   {
      return wasReleased;
   }

   public void setWasReleased(boolean wasReleased)
   {
      this.wasReleased = wasReleased;
   }

   public final void releaseConnection()
   {
      if (!wasReleased)
      {
         if (streamFactory != null) streamFactory.performReleaseConnection();
         wasReleased = true;
      }
   }

   @Override
   // this is synchronized to protect against premature finalize called by the GC
   protected synchronized final void finalize() throws Throwable
   {
      releaseConnection();
   }

   @Override
   public <T> T readEntity(Class<T> entityType)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(javax.ws.rs.core.GenericType<T> entityType)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(Class<T> entityType, Annotation[] annotations)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(javax.ws.rs.core.GenericType<T> entityType, Annotation[] annotations)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public boolean hasEntity()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public boolean bufferEntity()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public void close()
   {
      releaseConnection();
   }

   @Override
   public String getHeaderString(String name)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Locale getLanguage()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public int getLength()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Map<String, NewCookie> getCookies()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public EntityTag getEntityTag()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Date getDate()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Date getLastModified()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Set<javax.ws.rs.core.Link> getLinks()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public boolean hasLink(String relation)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public javax.ws.rs.core.Link getLink(String relation)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public javax.ws.rs.core.Link.Builder getLinkBuilder(String relation)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public URI getLocation()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Set<String> getAllowedMethods()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public MultivaluedMap<String, String> getStringHeaders()
   {
      throw new NotImplementedYetException();
   }
}
