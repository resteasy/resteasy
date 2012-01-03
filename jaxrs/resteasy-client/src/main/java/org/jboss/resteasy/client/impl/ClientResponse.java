package org.jboss.resteasy.client.impl;

import org.apache.http.client.methods.HttpHead;
import org.jboss.resteasy.plugins.delegates.LocaleDelegate;
import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.jboss.resteasy.util.DateUtil;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MessageProcessingException;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.ResponseHeaders;
import javax.ws.rs.core.TypeLiteral;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientResponse extends Response
{
   protected int status;
   protected MultivaluedMap<String, String> headers;
   protected Map<String, Object> properties;
   protected Object entity;

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
            language =  new LocaleDelegate().fromString(lang);
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
            return null;
         }

         @Override
         public Set<Link> getLinks()
         {
            return null;
         }

         @Override
         public Link getLink(String relation)
         {
            return null;
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
   public <T> T getEntity(Class<T> type) throws MessageProcessingException
   {
      return null;
   }

   @Override
   public <T> T getEntity(TypeLiteral<T> entityType) throws MessageProcessingException
   {
      return null;
   }

   @Override
   public boolean hasEntity()
   {
      return entity != null;
   }

   @Override
   public void bufferEntity() throws MessageProcessingException
   {
   }

   @Override
   public void close() throws MessageProcessingException
   {
   }

   @Override
   public MultivaluedMap<String, Object> getMetadata()
   {
      return headers;
   }
}
