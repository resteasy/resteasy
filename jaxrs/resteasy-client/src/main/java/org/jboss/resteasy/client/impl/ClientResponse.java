package org.jboss.resteasy.client.impl;

import org.jboss.resteasy.plugins.delegates.LocaleDelegate;
import org.jboss.resteasy.spi.MarshalledEntity;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.InputStreamToByteArray;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MessageProcessingException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.ResponseHeaders;
import javax.ws.rs.core.TypeLiteral;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import static java.lang.String.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class ClientResponse extends Response
{
   protected int status;
   protected MultivaluedMap<String, String> headers;
   protected Map<String, Object> properties;
   protected Object entity;
   protected ResteasyProviderFactory providerFactory;
   protected boolean isClosed;

   public void setHeaders(MultivaluedMap<String, String> headers)
   {
      this.headers = headers;
   }

   public void setProperties(Map<String, Object> properties)
   {
      this.properties = properties;
   }

   public void setStatus(int status)
   {
      this.status = status;
   }

   public void setProviderFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
   }

   @Override
   public Map<String, Object> getProperties()
   {
      return properties;
   }

   @Override
   public int getStatus()
   {
      return status;
   }

   @Override
   public Status getStatusEnum()
   {
      return Status.fromStatusCode(status);
   }

   @Override
   public ResponseHeaders getHeaders()
   {
      return new ResponseHeaders()
      {
         protected Set<String> allowedMethods;
         protected Date date;
         protected Date lastModified;
         protected Locale language;
         protected Integer contentLength;
         protected MediaType contentType;
         protected Map<String, NewCookie> cookies;
         protected EntityTag entityTag;

         @Override
         public Set<String> getAllowedMethods()
         {
            if (allowedMethods != null) return allowedMethods;

            allowedMethods = new HashSet<String>();
            List<String> allowed = headers.get("Allow");
            if (allowed == null) return allowedMethods;
            for (String header : allowed)
            {
               StringTokenizer tokenizer = new StringTokenizer(header, ",");
               while (tokenizer.hasMoreTokens())
               {
                  allowedMethods.add(tokenizer.nextToken());
               }
            }

            return allowedMethods;
         }

         @Override
         public Date getDate()
         {
            if (date != null) return date;
            String d = headers.getFirst(HttpHeaders.DATE);
            if (d == null) return null;
            date = DateUtil.parseDate(d);
            return date;
         }

         @Override
         public String getHeader(String name)
         {
            return headers.getFirst(name);
         }

         @Override
         public MultivaluedMap<String, String> asMap()
         {
            return headers;
         }

         @Override
         public List<String> getHeaderValues(String name)
         {
            return headers.get(name);
         }

         @Override
         public Locale getLanguage()
         {
            if (language != null) return language;
            String lang = headers.getFirst("Language");
            if (lang == null) return null;
            language = new LocaleDelegate().fromString(lang);
            return language;
         }

         @Override
         public int getLength()
         {
            if (contentLength != null) return contentLength;
            String cl = headers.getFirst(HttpHeaders.CONTENT_LENGTH);
            if (cl == null)
            {
               contentLength = new Integer(-1);
            }
            else
            {
               contentLength = Integer.parseInt(cl);
            }
            return contentLength;
         }

         @Override
         public MediaType getMediaType()
         {
            if (contentType != null) return contentType;
            String ct = headers.getFirst(HttpHeaders.CONTENT_TYPE);
            if (ct == null) return null;
            contentType = MediaType.valueOf(ct);
            return contentType;
         }

         @Override
         public Map<String, NewCookie> getCookies()
         {
            if (cookies != null) return cookies;
            cookies = new HashMap<String, NewCookie>();
            List<String> cooks = headers.get(HttpHeaders.SET_COOKIE);
            if (cooks == null) return cookies;
            for (String setCookie : cooks)
            {
               NewCookie cookie = NewCookie.valueOf(setCookie);
               cookies.put(cookie.getName(), cookie);
            }

            return cookies;
         }

         @Override
         public EntityTag getEntityTag()
         {
            if (entityTag != null) return entityTag;
            String tag = headers.getFirst(HttpHeaders.ETAG);
            if (tag == null) return null;
            entityTag = EntityTag.valueOf(tag);
            return entityTag;
         }

         @Override
         public Date getLastModified()
         {
            if (lastModified != null) return lastModified;
            String d = headers.getFirst(HttpHeaders.LAST_MODIFIED);
            if (d == null) return null;
            lastModified = DateUtil.parseDate(d);
            return lastModified;
         }

         @Override
         public URI getLocation()
         {
            throw new NotImplementedYetException();
         }

         @Override
         public Set<Link> getLinks()
         {
            throw new NotImplementedYetException();
         }

         @Override
         public Link getLink(String relation)
         {
            throw new NotImplementedYetException();
         }

         @Override
         public boolean hasLink(String relation)
         {
            throw new NotImplementedYetException();
         }

         @Override
         public Link.Builder getLinkBuilder(String relation)
         {
            throw new NotImplementedYetException();
         }
      };
   }

   @Override
   public Object getEntity()
   {
      if (entity != null) return entity;
      return entity;
   }

   @Override
   public <T> T readEntity(Class<T> type) throws MessageProcessingException
   {
      return readEntity(type, null, null);
   }

   @Override
   public <T> T readEntity(TypeLiteral<T> entityType) throws MessageProcessingException
   {
      return readEntity(entityType.getRawType(), entityType.getType(), null);
   }


   @Override
   public boolean hasEntity()
   {
      return entity != null;
   }

   @Override
   public MultivaluedMap<String, Object> getMetadata()
   {
      return (MultivaluedMap) headers;
   }

   @Override
   public void close() throws MessageProcessingException
   {
      if (isClosed) return;
      releaseConnection();
   }

   @Override
   protected void finalize() throws Throwable
   {
      if (isClosed) return;
      releaseConnection();
   }

   protected abstract InputStream getInputStream();
   protected abstract void setInputStream(InputStream is);
   protected abstract void releaseConnection();


   protected MediaType getMediaType()
   {
      String mediaType = headers.getFirst(HttpHeaderNames.CONTENT_TYPE);
      return mediaType == null ? MediaType.WILDCARD_TYPE : MediaType.valueOf(mediaType);
   }


   public <T2> T2 readEntity(Class<T2> type, Type genericType, Annotation[] anns)
   {
      if (entity != null && !type.isInstance(this.entity))
         throw new RuntimeException("The entity was already read, and it was of type "
                 + entity.getClass());

      if (entity == null)
      {
         if (status == HttpResponseCodes.SC_NO_CONTENT)
            return null;

         entity = readFrom(type, genericType, getMediaType(), anns);
         // only release connection if we actually unmarshalled something and if the object is *NOT* an InputStream
         // If it is an input stream, the user may be doing their own stream processing.
         if (entity != null && !InputStream.class.isInstance(entity)) close();
      }
      return (T2) entity;
   }

   protected <T2> Object readFrom(Class<T2> type, Type genericType,
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


      MessageBodyReader reader1 = providerFactory.getMessageBodyReader(useType,
              useGeneric, annotations, media);
      if (reader1 == null)
      {
         throw new MessageProcessingException(format(
                 "Unable to find a MessageBodyReader of content-type %s and type %s",
                 media, genericType));
      }

      try
      {
         InputStream is = getInputStream();
         if (is == null)
         {
            throw new MessageProcessingException("Input stream was empty, there is no entity");
         }
         if (isMarshalledEntity)
         {
            is = new InputStreamToByteArray(is);

         }

         // todo put in reader interception
         final Object obj = reader1.readFrom(type, genericType, annotations, media, headers, is);

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
                  return obj;
               }
            };
         }
         else
         {
            return (T2) obj;
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
   }

   @Override
   public <T> T readEntity(Class<T> type, Annotation[] annotations) throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(TypeLiteral<T> entityType, Annotation[] annotations) throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public boolean isEntityRetrievable()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public void bufferEntity() throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }
}
