package org.jboss.resteasy.specimpl;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.spi.LinkHeaders;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.MessageProcessingException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * A response object not attached to a client or server invocation.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BuiltResponse extends Response
{
   protected Object entity;
   protected int status = HttpResponseCodes.SC_OK;
   protected Headers<Object> metadata = new Headers<Object>();
   protected Annotation[] annotations;
   protected Class entityClass;
   protected Type genericType;

   public BuiltResponse()
   {
   }

   public BuiltResponse(int status, Headers<Object> metadata, Object entity, Annotation[] entityAnnotations)
   {
      setEntity(entity);
      this.status = status;
      this.metadata = metadata;
      this.annotations = entityAnnotations;
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return ResteasyProviderFactory.getInstance();
   }

   @Override
   public Object getEntity()
   {
      return entity;
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
   public MultivaluedMap<String, Object> getMetadata()
   {
      return metadata;
   }

   public void setEntity(Object entity)
   {
      if (entity == null)
      {
         this.entity = null;
         this.genericType = null;
         this.entityClass = null;
      }
      else if (entity instanceof GenericEntity)
      {

         GenericEntity ge = (GenericEntity) entity;
         this.entity = ge.getEntity();
         this.genericType = ge.getType();
         this.entityClass = ge.getRawType();
      }
      else
      {
         this.entity = entity;
         this.entityClass = entity.getClass();
         this.genericType = null;
      }
   }

   public void setStatus(int status)
   {
      this.status = status;
   }

   public void setMetadata(MultivaluedMap<String, Object> metadata)
   {
      this.metadata.clear();
      this.metadata.putAll(metadata);
   }

   public Annotation[] getAnnotations()
   {
      return annotations;
   }

   public void setAnnotations(Annotation[] annotations)
   {
      this.annotations = annotations;
   }

   public Type getGenericType()
   {
      return genericType;
   }

   public void setGenericType(Type genericType)
   {
      this.genericType = genericType;
   }

   @Override
   public <T> T readEntity(Class<T> entityType) throws MessageProcessingException, IllegalStateException
   {
      throw new IllegalStateException("Not usable on server side response object");
   }

   @Override
   public <T> T readEntity(GenericType<T> entityType) throws MessageProcessingException, IllegalStateException
   {
      throw new IllegalStateException("Not usable on server side response object");
   }

   @Override
   public <T> T readEntity(Class<T> entityType, Annotation[] annotations) throws MessageProcessingException, IllegalStateException
   {
      throw new IllegalStateException("Not usable on server side response object");
   }

   @Override
   public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) throws MessageProcessingException, IllegalStateException
   {
      throw new IllegalStateException("Not usable on server side response object");
   }

   @Override
   public boolean hasEntity()
   {
      return entity != null;
   }

   @Override
   public boolean bufferEntity() throws MessageProcessingException
   {
      // no-op
      return false;
   }

   @Override
   public void close() throws MessageProcessingException
   {
      // no-op
   }

   @Override
   public Locale getLanguage()
   {
      Object obj = metadata.getFirst(HttpHeaders.CONTENT_LANGUAGE);
      if (obj == null) return null;
      if (obj instanceof Locale) return (Locale) obj;
      return new Locale(obj.toString());
   }

   @Override
   public int getLength()
   {
      Object obj = metadata.getFirst(HttpHeaders.CONTENT_LANGUAGE);
      if (obj == null) return -1;
      if (obj instanceof Integer) return (Integer) obj;
      return Integer.valueOf(getProviderFactory().toHeaderString(obj));
   }

   @Override
   public MediaType getMediaType()
   {
      Object obj = metadata.getFirst(HttpHeaders.CONTENT_TYPE);
      if (obj instanceof MediaType) return (MediaType) obj;
      if (obj == null) return null;
      return MediaType.valueOf(getProviderFactory().toHeaderString(obj));
   }

   @Override
   public Map<String, NewCookie> getCookies()
   {
      Map<String, NewCookie> cookies = new HashMap<String, NewCookie>();
      List list = metadata.get(HttpHeaders.COOKIE);
      if (list == null) return cookies;
      for (Object obj : list)
      {
         if (obj instanceof NewCookie)
         {
            NewCookie cookie = (NewCookie)obj;
            cookies.put(cookie.getName(), cookie);
         }
         else
         {
            String str = getProviderFactory().toHeaderString(obj);
            NewCookie cookie = NewCookie.valueOf(str);
            cookies.put(cookie.getName(), cookie);
         }
      }
      return cookies;
   }

   @Override
   public EntityTag getEntityTag()
   {
      Object d = metadata.getFirst(HttpHeaders.ETAG);
      if (d == null) return null;
      if (d instanceof EntityTag) return (EntityTag) d;
      return EntityTag.valueOf(getProviderFactory().toHeaderString(d));
   }

   @Override
   public Date getDate()
   {
      Object d = metadata.getFirst(HttpHeaders.DATE);
      if (d == null) return null;
      if (d instanceof Date) return (Date) d;
      return DateUtil.parseDate(d.toString());
   }

   @Override
   public Date getLastModified()
   {
      Object d = metadata.getFirst(HttpHeaders.LAST_MODIFIED);
      if (d == null) return null;
      if (d instanceof Date) return (Date) d;
      return DateUtil.parseDate(d.toString());
   }

   @Override
   public Set<String> getAllowedMethods()
   {
      Set<String> allowedMethods = new HashSet<String>();
      List<Object> allowed = metadata.get("Allow");
      if (allowed == null) return allowedMethods;
      for (Object header : allowed)
      {
         allowedMethods.add(getProviderFactory().toHeaderString(header));
      }

      return allowedMethods;
   }

   @Override
   public MultivaluedMap<String, String> getStringHeaders()
   {
      CaseInsensitiveMap<String> map = new CaseInsensitiveMap<String>();
      for (Map.Entry<String, List<Object>> entry : metadata.entrySet())
      {
         for (Object obj : entry.getValue())
         {
            map.add(entry.getKey(), getProviderFactory().toHeaderString(obj));
         }
      }
      return map;
   }

   @Override
   public String getHeaderString(String name)
   {
      List vals = metadata.get(name);
      if (vals == null) return null;
      StringBuilder builder = new StringBuilder();
      boolean first = true;
      for (Object val : vals)
      {
         if (first) first = false;
         else builder.append(",");
         builder.append(getProviderFactory().toHeaderString(val));
      }
      return builder.toString();
   }

   @Override
   public URI getLocation()
   {
      Object uri = metadata.getFirst(HttpHeaders.LOCATION);
      if (uri == null) return null;
      if (uri instanceof URI) return (URI)uri;
      String str = null;
      if (uri instanceof String) str = (String)uri;
      else str = getProviderFactory().toHeaderString(uri);
      return URI.create(str);
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
      linkHeaders.addLinkObjects(metadata, getProviderFactory());
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

}
