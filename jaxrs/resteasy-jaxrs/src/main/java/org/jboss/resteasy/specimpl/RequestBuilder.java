package org.jboss.resteasy.specimpl;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.jboss.resteasy.util.HeaderHelper;
import org.jboss.resteasy.util.LocaleHelper;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.RequestHeaders;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Locale;
import java.util.Set;

/**
 * Creates a BuiltRequest by default.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RequestBuilder implements Request.RequestBuilder
{
   protected CaseInsensitiveMap<Object> headers = new CaseInsensitiveMap<Object>();
   protected String method;
   protected URI uri;
   protected Object entity;
   protected Annotation[] entityAnnotations;
   protected ResteasyProviderFactory providerFactory;

   public RequestBuilder(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
   }

   @Override
   public Request.RequestBuilder entity(Object entity, Annotation[] annotations)
   {
      this.entity = entity;
      this.entityAnnotations = annotations;
      return this;
   }

   @Override
   public Request.RequestBuilder encoding(String encoding)
   {
      header(HttpHeaders.CONTENT_ENCODING, encoding);
      return this;
   }

   @Override
   public Request.RequestBuilder replaceAll(RequestHeaders headers)
   {
      this.headers.putAll(headers.asMap());
      return this;
   }

   @Override
   public Request.RequestBuilder language(String language)
   {
      header(HttpHeaders.CONTENT_LANGUAGE, language);
      return this;
   }

   @Override
   public Request.RequestBuilder language(Locale language)
   {
      header(HttpHeaders.CONTENT_LANGUAGE, LocaleHelper.toLanguageString(language));
      return this;
   }

   @Override
   public Request.RequestBuilder type(MediaType type)
   {
      header(HttpHeaders.CONTENT_TYPE, type);
      return this;
   }

   @Override
   public Request.RequestBuilder type(String type)
   {
      header(HttpHeaders.CONTENT_TYPE, type);
      return this;
   }

   @Override
   public Request.RequestBuilder variant(Variant variant)
   {
      if (variant == null)
      {
         encoding(null);
         language((String)null);
         type((String)null);

      }
      encoding(variant.getEncoding());
      language(variant.getLanguage());
      type(variant.getMediaType());
      return this;
   }

   @Override
   public Request.RequestBuilder redirect(String uri)
   {
      if (uri == null)
      {
         this.uri = null;
      }
      else
      {
         this.uri = URI.create(uri);
      }
      return this;
   }

   @Override
   public Request.RequestBuilder redirect(URI uri)
   {
      this.uri = uri;
      return this;
   }

   @Override
   public Request.RequestBuilder redirect(UriBuilder uri)
   {
      if (uri == null)
      {
         this.uri = null;
      }
      else
      {
         this.uri = uri.build();
      }
      return this;
   }

   @Override
   public Request.RequestBuilder method(String httpMethod)
   {
      this.method = httpMethod;
      return this;
   }

   @Override
   public Request.RequestBuilder entity(Object entity)
   {
      this.entity = entity;
      return this;
   }

   @Override
   public Request.RequestBuilder clone()
   {
      RequestBuilder builder = new RequestBuilder(providerFactory);
      builder.headers.putAll(headers);
      builder.method = method;
      builder.uri = uri;
      builder.entity = entity;
      return builder;
   }

   @Override
   public Request build()
   {
      RequestHeadersImpl headers = new RequestHeadersImpl(this.headers, providerFactory);
      return new BuiltRequest(method, uri, headers, entity, entityAnnotations);
   }

   @Override
   public Request.RequestBuilder acceptLanguage(Locale... locales)
   {
      headers.remove(HttpHeaders.ACCEPT_LANGUAGE);
      StringBuilder builder = new StringBuilder();
      boolean isFirst = true;
      for (Locale l : locales)
      {
         if (isFirst)
         {
            isFirst = false;
         }
         else
         {
            builder.append(", ");
         }
         builder.append(l.toString());
      }
      headers.putSingle(HttpHeaders.ACCEPT_LANGUAGE, builder.toString());
      return this;
   }

   @Override
   public Request.RequestBuilder acceptLanguage(String... locales)
   {
      headers.remove(HttpHeaders.ACCEPT_LANGUAGE);
      StringBuilder builder = new StringBuilder();
      boolean isFirst = true;
      for (String l : locales)
      {
         if (isFirst)
         {
            isFirst = false;
         }
         else
         {
            builder.append(", ");
         }
         builder.append(l.toString());
      }
      headers.putSingle(HttpHeaders.ACCEPT_LANGUAGE, builder.toString());
      return this;
   }

   @Override
   public Request.RequestBuilder accept(String... types)
   {
      headers.remove(HttpHeaders.ACCEPT);
      StringBuilder builder = new StringBuilder();
      boolean isFirst = true;
      for (String l : types)
      {
         if (isFirst)
         {
            isFirst = false;
         }
         else
         {
            builder.append(", ");
         }
         builder.append(l.toString());
      }
      headers.putSingle(HttpHeaders.ACCEPT, builder.toString());
      return this;
   }

   @Override
   public Request.RequestBuilder accept(MediaType... types)
   {
      headers.remove(HttpHeaders.ACCEPT);
      StringBuilder builder = new StringBuilder();
      boolean isFirst = true;
      for (MediaType l : types)
      {
         if (isFirst)
         {
            isFirst = false;
         }
         else
         {
            builder.append(", ");
         }
         builder.append(l.toString());
      }
      headers.putSingle(HttpHeaders.ACCEPT, builder.toString());
      return this;
   }

   @Override
   public Request.RequestBuilder cookie(Cookie cookie)
   {
      headers.add(HttpHeaders.COOKIE, cookie);
      return this;
   }

   @Override
   public Request.RequestBuilder allow(String... methods)
   {
      HeaderHelper.setAllow(this.headers, methods);
      return this;
   }

   @Override
   public Request.RequestBuilder allow(Set<String> methods)
   {
      HeaderHelper.setAllow(headers, methods);
      return this;
   }

   @Override
   public Request.RequestBuilder cacheControl(CacheControl cacheControl)
   {
      headers.putSingle(HttpHeaders.CACHE_CONTROL, cacheControl);
      return this;
   }

   @Override
   public Request.RequestBuilder header(String name, Object value)
   {
      if (value == null)
      {
         headers.remove(name);
         return this;
      }
      headers.add(name, value);
      return this;
   }


}
