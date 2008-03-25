package org.resteasy.specimpl;

import org.resteasy.Headers;
import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.HttpHeaderNames;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
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
   private Headers<Object> metadata = new Headers<Object>();
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

   @Override
   public Response.ResponseBuilder clone()
   {
      ResponseBuilderImpl impl = new ResponseBuilderImpl();
      impl.metadata.putAll(metadata);
      impl.entity = entity;
      impl.cookies.addAll(cookies);
      impl.status = status;
      return impl;
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
      if (variant.getMediaType() != null) type(variant.getMediaType());
      if (variant.getLanguage() != null) language(variant.getLanguage());
      if (variant.getEncoding() != null) metadata.putSingle(HttpHeaderNames.CONTENT_ENCODING, variant.getEncoding());
      return this;
   }

   public Response.ResponseBuilder variants(List<Variant> variants)
   {
      String vary = createVaryHeader(variants);
      metadata.putSingle(HttpHeaderNames.VARY, vary);

      return this;
   }

   public static String createVaryHeader(List<Variant> variants)
   {
      boolean accept = false;
      boolean acceptLanguage = false;
      boolean acceptEncoding = false;

      for (Variant variant : variants)
      {
         if (variant.getMediaType() != null) accept = true;
         if (variant.getLanguage() != null) acceptLanguage = true;
         if (variant.getEncoding() != null) acceptEncoding = true;
      }

      String vary = null;
      if (accept) vary = HttpHeaderNames.ACCEPT;
      if (acceptLanguage)
      {
         if (vary == null) vary = HttpHeaderNames.ACCEPT_LANGUAGE;
         else vary += ", " + HttpHeaderNames.ACCEPT_LANGUAGE;
      }
      if (acceptEncoding)
      {
         if (vary == null) vary = HttpHeaderNames.ACCEPT_ENCODING;
         else vary += ", " + HttpHeaderNames.ACCEPT_ENCODING;
      }
      return vary;
   }

   public Response.ResponseBuilder language(String language)
   {
      metadata.putSingle(HttpHeaderNames.CONTENT_LANGUAGE, language);
      return this;
   }

   public Response.ResponseBuilder location(URI location)
   {
      if (!location.isAbsolute() && ResteasyProviderFactory.getContextData(HttpRequest.class) != null)
      {
         String path = location.getPath();
         if (path.startsWith("/")) path = path.substring(1);
         URI baseUri = ResteasyProviderFactory.getContextData(HttpRequest.class).getUri().getBaseUri();
         location = baseUri.resolve(path);
      }
      metadata.putSingle(HttpHeaderNames.LOCATION, location);
      return this;
   }

   public Response.ResponseBuilder contentLocation(URI location)
   {
      if (!location.isAbsolute() && ResteasyProviderFactory.getContextData(HttpRequest.class) != null)
      {
         String path = location.getPath();
         if (path.startsWith("/")) path = path.substring(1);
         URI baseUri = ResteasyProviderFactory.getContextData(HttpRequest.class).getUri().getBaseUri();
         location = baseUri.resolve(path);
      }
      metadata.putSingle(HttpHeaderNames.CONTENT_LOCATION, location);
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
         metadata.add(HttpHeaderNames.SET_COOKIE, cookie);
      }
      return this;
   }

}
