package org.jboss.resteasy.specimpl;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HeaderHelper;
import org.jboss.resteasy.util.HttpHeaderNames;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResponseBuilderImpl extends Response.ResponseBuilder
{
   protected Object entity;
   protected Annotation[] entityAnnotations;
   protected int status = -1;
   protected String reasonPhrase;
   protected Headers<Object> metadata = new Headers<Object>();

   @Override
   public Response build()
   {
      if (status == -1 && entity == null) status = 204;
      else if (status == -1) status = 200;
      return new BuiltResponse(status, reasonPhrase, metadata, entity, entityAnnotations);
   }

   @Override
   public Response.ResponseBuilder clone()
   {
      ResponseBuilderImpl impl = new ResponseBuilderImpl();
      impl.metadata.putAll(metadata);
      impl.entity = entity;
      impl.status = status;
      impl.entityAnnotations = entityAnnotations;
      return impl;
   }

   @Override
   public Response.ResponseBuilder status(int status, String reasonPhrase)
   {
      this.status = status;
      this.reasonPhrase = reasonPhrase;
      return this;
   }

   @Override
   public Response.ResponseBuilder status(int status)
   {
      return status(status, null);
   }

   @Override
   public Response.ResponseBuilder entity(Object entity)
   {
      this.entity = entity;
      return this;
   }

   @Override
   public Response.ResponseBuilder entity(Object entity, Annotation[] annotations)
   {
      this.entity = entity;
      this.entityAnnotations = annotations;
      return this;
   }

   @Override
   public Response.ResponseBuilder type(MediaType type)
   {
      if (type == null)
      {
         metadata.remove(HttpHeaderNames.CONTENT_TYPE);
         return this;
      }
      metadata.putSingle(HttpHeaderNames.CONTENT_TYPE, type);
      return this;
   }

   @Override
   public Response.ResponseBuilder type(String type)
   {
      if (type == null)
      {
         metadata.remove(HttpHeaderNames.CONTENT_TYPE);
         return this;
      }
      metadata.putSingle(HttpHeaderNames.CONTENT_TYPE, type);
      return this;
   }

   @Override
   public Response.ResponseBuilder variant(Variant variant)
   {
      if (variant == null)
      {
         type((String)null);
         language((String)null);
         metadata.remove(HttpHeaderNames.CONTENT_ENCODING);
         return this;
      }
      type(variant.getMediaType());
      language(variant.getLanguage());
      if (variant.getEncoding() != null) metadata.putSingle(HttpHeaderNames.CONTENT_ENCODING, variant.getEncoding());
      else metadata.remove(HttpHeaderNames.CONTENT_ENCODING);
      return this;
   }

   @Override
   public Response.ResponseBuilder variants(List<Variant> variants)
   {
      if (variants == null)
      {
         metadata.remove(HttpHeaderNames.VARY);
         return this;
      }
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

   @Override
   public Response.ResponseBuilder language(String language)
   {
      if (language == null)
      {
         metadata.remove(HttpHeaderNames.CONTENT_LANGUAGE);
         return this;
      }
      metadata.putSingle(HttpHeaderNames.CONTENT_LANGUAGE, language);
      return this;
   }

   @Override
   public Response.ResponseBuilder location(URI location)
   {
      if (location == null)
      {
         metadata.remove(HttpHeaderNames.LOCATION);
         return this;
      }
      if (!location.isAbsolute() && ResteasyProviderFactory.getContextData(HttpRequest.class) != null)
      {
         String path = location.toString();
         if (path.startsWith("/")) path = path.substring(1);
         URI baseUri = ResteasyProviderFactory.getContextData(HttpRequest.class).getUri().getBaseUri();
         location = baseUri.resolve(path);
      }
      metadata.putSingle(HttpHeaderNames.LOCATION, location);
      return this;
   }

   @Override
   public Response.ResponseBuilder contentLocation(URI location)
   {
      if (location == null)
      {
         metadata.remove(HttpHeaderNames.CONTENT_LOCATION);
         return this;
      }
      if (!location.isAbsolute() && ResteasyProviderFactory.getContextData(HttpRequest.class) != null)
      {
         String path = location.toString();
         if (path.startsWith("/")) path = path.substring(1);
         URI baseUri = ResteasyProviderFactory.getContextData(HttpRequest.class).getUri().getBaseUri();
         location = baseUri.resolve(path);
      }
      metadata.putSingle(HttpHeaderNames.CONTENT_LOCATION, location);
      return this;
   }

   @Override
   public Response.ResponseBuilder tag(EntityTag tag)
   {
      if (tag == null)
      {
         metadata.remove(HttpHeaderNames.ETAG);
         return this;
      }
      metadata.putSingle(HttpHeaderNames.ETAG, tag);
      return this;
   }

   @Override
   public Response.ResponseBuilder tag(String tag)
   {
      if (tag == null)
      {
         metadata.remove(HttpHeaderNames.ETAG);
         return this;
      }
      return tag(new EntityTag(tag));
   }

   @Override
   public Response.ResponseBuilder lastModified(Date lastModified)
   {
      if (lastModified == null)
      {
         metadata.remove(HttpHeaderNames.LAST_MODIFIED);
         return this;
      }
      metadata.putSingle(HttpHeaderNames.LAST_MODIFIED, lastModified);
      return this;
   }

   @Override
   public Response.ResponseBuilder cacheControl(CacheControl cacheControl)
   {
      if (cacheControl == null)
      {
         metadata.remove(HttpHeaderNames.CACHE_CONTROL);
         return this;
      }
      metadata.putSingle(HttpHeaderNames.CACHE_CONTROL, cacheControl);
      return this;
   }

   @Override
   public Response.ResponseBuilder header(String name, Object value)
   {
      if (value == null)
      {
         metadata.remove(name);
         return this;
      }
      metadata.add(name, value);
      return this;
   }

   @Override
   public Response.ResponseBuilder cookie(NewCookie... cookies)
   {
      if (cookies == null)
      {
         metadata.remove(HttpHeaderNames.SET_COOKIE);
         return this;
      }
      for (NewCookie cookie : cookies)
      {
         metadata.add(HttpHeaderNames.SET_COOKIE, cookie);
      }
      return this;
   }

   public Response.ResponseBuilder language(Locale language)
   {
      if (language == null)
      {
         metadata.remove(HttpHeaderNames.CONTENT_LANGUAGE);
         return this;
      }
      metadata.putSingle(HttpHeaderNames.CONTENT_LANGUAGE, language);
      return this;
   }

   public static SimpleDateFormat getDateFormatRFC822()
   {
      SimpleDateFormat dateFormatRFC822 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
      dateFormatRFC822.setTimeZone(TimeZone.getTimeZone("GMT"));
      return dateFormatRFC822;
   }

   public Response.ResponseBuilder expires(Date expires)
   {
      if (expires == null)
      {
         metadata.remove(HttpHeaderNames.EXPIRES);
         return this;
      }
      metadata.putSingle(HttpHeaderNames.EXPIRES, getDateFormatRFC822().format(expires));
      return this;
   }

   // spec


   public Response.ResponseBuilder allow(String... methods)
   {
      if (methods == null)
      {
         return allow((Set<String>)null);
      }
      HashSet<String> set = new HashSet<String>();
      for (String m : methods) set.add(m);
      return allow(set);
   }

   public Response.ResponseBuilder allow(Set<String> methods)
   {
      HeaderHelper.setAllow(this.metadata, methods);
      return this;
   }


   @Override
   public Response.ResponseBuilder encoding(String encoding)
   {
      if (encoding == null)
      {
         metadata.remove(HttpHeaders.CONTENT_ENCODING);
         return this;
      }
      metadata.putSingle(HttpHeaders.CONTENT_ENCODING, encoding);
      return this;
   }

   @Override
   public Response.ResponseBuilder variants(Variant... variants)
   {
      return this.variants(Arrays.asList(variants));
   }

   @Override
   public Response.ResponseBuilder links(Link... links)
   {
      if (links == null)
      {
         metadata.remove(HttpHeaders.LINK);
         return this;
      }
      for (Link link : links)
      {
         metadata.add(HttpHeaders.LINK, link);
      }
      return this;
   }

   @Override
   public Response.ResponseBuilder link(URI uri, String rel)
   {
      Link link = Link.fromUri(uri).rel(rel).build();
      metadata.add(HttpHeaders.LINK, link);
      return this;
   }

   @Override
   public Response.ResponseBuilder link(String uri, String rel)
   {
      Link link = Link.fromUri(uri).rel(rel).build();
      metadata.add(HttpHeaders.LINK, link);
      return this;
   }

   @Override
   public Response.ResponseBuilder replaceAll(MultivaluedMap<String, Object> headers)
   {
      metadata.clear();
      if (headers == null) return this;
      metadata.putAll(headers);
      return this;
   }

}
