package org.resteasy.specimpl;

import org.resteasy.util.HttpHeaderNames;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResponseBuilderImpl extends Response.ResponseBuilder
{
   private Object entity;
   private int status;
   private MultivaluedMap<String, Object> metadata = new MultivaluedMapImpl<String, Object>();
   private List<NewCookie> cookies = new ArrayList<NewCookie>();

   public Response build()
   {
      NewCookie[] newCookies = null;
      if (cookies.size() > 0)
      {
         newCookies = new NewCookie[cookies.size()];
         newCookies = cookies.toArray(newCookies);
      }
      return new ResponseImpl(entity, status, metadata, newCookies);
   }

   public Response.ResponseBuilder status(int status)
   {
      this.status = status;
      return this;
   }

   public Response.ResponseBuilder entity(Object entity)
   {
      this.entity = entity;
      return this;
   }

   public Response.ResponseBuilder type(MediaType type)
   {
      metadata.putSingle(HttpHeaderNames.CONTENT_TYPE, type);
      return this;
   }

   public Response.ResponseBuilder type(String type)
   {
      metadata.putSingle(HttpHeaderNames.CONTENT_TYPE, type);
      return this;
   }

   public Response.ResponseBuilder variant(Variant variant)
   {
      if (true) throw new RuntimeException("NOT SUPPORTED");
      return this;
   }

   public Response.ResponseBuilder variants(List<Variant> variants)
   {
      if (true) throw new RuntimeException("NOT SUPPORTED");
      return this;
   }

   public Response.ResponseBuilder language(String language)
   {
      metadata.putSingle(HttpHeaderNames.CONTENT_LANGUAGE, language);
      return this;
   }

   public Response.ResponseBuilder location(URI location)
   {
      metadata.putSingle(HttpHeaderNames.LOCATION, location.toString());
      return this;
   }

   public Response.ResponseBuilder contentLocation(URI location)
   {

      metadata.putSingle(HttpHeaderNames.LOCATION, location.toString());
      return this;
   }

   public Response.ResponseBuilder tag(EntityTag tag)
   {
      metadata.putSingle(HttpHeaderNames.ETAG, tag);
      return this;
   }

   public Response.ResponseBuilder tag(String tag)
   {
      metadata.putSingle(HttpHeaderNames.ETAG, tag);
      return this;
   }

   public Response.ResponseBuilder lastModified(Date lastModified)
   {
      metadata.putSingle(HttpHeaderNames.LAST_MODIFIED, lastModified);
      return this;
   }

   public Response.ResponseBuilder cacheControl(CacheControl cacheControl)
   {
      metadata.putSingle(HttpHeaderNames.CACHE_CONTROL, cacheControl);
      return this;
   }

   public Response.ResponseBuilder header(String name, Object value)
   {
      metadata.putSingle(name, value);
      return this;
   }

   public Response.ResponseBuilder cookie(NewCookie... cookies)
   {
      for (NewCookie cookie : cookies)
      {
         this.cookies.add(cookie);
      }
      return this;
   }
}
