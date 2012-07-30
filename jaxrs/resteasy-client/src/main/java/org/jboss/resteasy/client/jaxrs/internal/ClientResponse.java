package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.plugins.delegates.LocaleDelegate;
import org.jboss.resteasy.spi.LinkHeaders;
import org.jboss.resteasy.spi.MarshalledEntity;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.InputStreamToByteArray;
import org.jboss.resteasy.util.ReadFromStream;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.MessageProcessingException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
   // One thing to note, I don't cache header objects because I was too lazy to proxy the headers multivalued map
   protected MultivaluedMap<String, String> headers;
   protected Map<String, Object> properties;
   protected Object entity;
   protected ClientConfiguration configuration;
   protected boolean isClosed;
   protected byte[] bufferedEntity;

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

   public Map<String, Object> getProperties()
   {
      return properties;
   }

   public void setConfiguration(ClientConfiguration configuration)
   {
      this.configuration = configuration;
   }

   @Override
   public int getStatus()
   {
      return status;
   }

   @Override
   public StatusType getStatusInfo()
   {
      return Status.fromStatusCode(status);
   }

   @Override
   public MultivaluedMap<String, String> getStringHeaders()
   {
      return headers;
   }


   @Override
   public Object getEntity()
   {
      if (entity != null) return entity;
      return entity;
   }

   @Override
   public <T> T readEntity(Class<T> type)
   {
      return readEntity(type, null, null);
   }

   @Override
   public <T> T readEntity(GenericType<T> entityType) throws MessageProcessingException
   {
      return readEntity((Class<T>) entityType.getRawType(), entityType.getType(), null);
   }


   @Override
   public boolean hasEntity()
   {
      return entity != null || getMediaType() != null;
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

   protected InputStream getEntityStream()
   {
      if (bufferedEntity != null) return new ByteArrayInputStream(bufferedEntity);
      if (isClosed) throw new MessageProcessingException("Stream is closed");
      return getInputStream();
   }

   protected abstract void setInputStream(InputStream is);

   protected abstract void releaseConnection();


   @Override
   public MediaType getMediaType()
   {
      String mediaType = headers.getFirst(HttpHeaderNames.CONTENT_TYPE);
      if (mediaType == null) return null;
      return MediaType.valueOf(mediaType);
   }

   public <T2> T2 readEntity(Class<T2> type, Type genericType, Annotation[] anns)
   {
      if (entity != null && !type.isInstance(this.entity))
      {
         if (bufferedEntity == null)
         {
            throw new RuntimeException("The entity was already read, and it was of type "
                    + entity.getClass());
         }
         else
         {
            entity = null;
         }
      }

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


      MessageBodyReader reader1 = configuration.getMessageBodyReader(useType,
              useGeneric, annotations, media);
      if (reader1 == null)
      {
         throw new MessageProcessingException(format(
                 "Unable to find a MessageBodyReader of content-type %s and type %s",
                 media, genericType));
      }

      try
      {
         InputStream is = getEntityStream();
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
      return readEntity(type, null, annotations);
   }

   @Override
   public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) throws MessageProcessingException
   {
      return readEntity((Class<T>) entityType.getRawType(), entityType.getType(), annotations);
   }

   @Override
   public boolean bufferEntity() throws MessageProcessingException
   {
      if (bufferedEntity != null) return true;
      if (entity != null) return false;
      String mediaType = headers.getFirst(HttpHeaderNames.CONTENT_TYPE);
      if (mediaType == null) return false;
      try
      {
         bufferedEntity = ReadFromStream.readFromStream(1024, getInputStream());
      }
      catch (IOException e)
      {
         throw new MessageProcessingException(e);
      }
      return true;
   }

   @Override
   public Locale getLanguage()
   {
      String lang = headers.getFirst("Language");
      if (lang == null) return null;
      return new LocaleDelegate().fromString(lang);
   }

   @Override
   public int getLength()
   {
      String cl = headers.getFirst(HttpHeaders.CONTENT_LENGTH);
      if (cl == null)
      {
         return -1;
      }
      else
      {
         return Integer.parseInt(cl);
      }
   }

   @Override
   public Map<String, NewCookie> getCookies()
   {
      Map<String, NewCookie> cookies = new HashMap<String, NewCookie>();
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
   public Date getDate()
   {
      String d = headers.getFirst(HttpHeaders.DATE);
      if (d == null) return null;
      return DateUtil.parseDate(d);
   }

   @Override
   public EntityTag getEntityTag()
   {
      String tag = headers.getFirst(HttpHeaders.ETAG);
      if (tag == null) return null;
      return EntityTag.valueOf(tag);
   }

   @Override
   public Date getLastModified()
   {
      String d = headers.getFirst(HttpHeaders.LAST_MODIFIED);
      if (d == null) return null;
      return DateUtil.parseDate(d);
   }

   @Override
   public URI getLocation()
   {
      String uri = headers.getFirst(HttpHeaders.LOCATION);
      if (uri == null) return null;
      return URI.create(uri);
   }

   @Override
   public Set<Link> getLinks()
   {
      LinkHeaders linkHeaders = getLinkHeaders();
      Set<Link> links = new HashSet<Link>();
      links.addAll(linkHeaders.getLinks());
      return links;
   }

   protected LinkHeaders getLinkHeaders()
   {
      LinkHeaders linkHeaders = new LinkHeaders();
      linkHeaders.addLinks(headers);
      return linkHeaders;
   }

   @Override
   public boolean hasLink(String relation)
   {
      return getLinkHeaders().getLinkByRelationship(relation) != null;
   }

   @Override
   public Link getLink(String relation)
   {
      return getLinkHeaders().getLinkByRelationship(relation);
   }

   @Override
   public Link.Builder getLinkBuilder(String relation)
   {
      Link link = getLinkHeaders().getLinkByRelationship(relation);
      Link.Builder builder = new Link.Builder();
      for (Map.Entry<String, List<String>> entry : link.getParams().entrySet())
      {
         for (String val : entry.getValue())
         {
            builder.param(entry.getKey(), val);
         }
      }
      return builder;
   }

   @Override
   public Set<String> getAllowedMethods()
   {
      Set<String> allowedMethods = new HashSet<String>();
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
   public String getHeaderString(String name)
   {
      return headers.getFirst(name);
   }
}
