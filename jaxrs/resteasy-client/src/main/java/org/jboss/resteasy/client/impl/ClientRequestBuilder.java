package org.jboss.resteasy.client.impl;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.RequestHeaders;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import java.net.URI;
import java.util.Locale;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientRequestBuilder implements Request.RequestBuilder
{
   protected InvocationBuilder invocationBuilder;

   public ClientRequestBuilder(ClientInvocation invocation)
   {
      this.invocationBuilder = new InvocationBuilder(invocation);
   }

   @Override
   public Request.RequestBuilder allow(String... methods)
   {
      invocationBuilder.allow(methods);
      return this;
   }

   @Override
   public Request.RequestBuilder allow(Set<String> methods)
   {
      invocationBuilder.allow(methods);
      return this;
   }

   @Override
   public Request.RequestBuilder cacheControl(CacheControl cacheControl)
   {
      invocationBuilder.cacheControl(cacheControl);
      return this;
   }

   @Override
   public Request.RequestBuilder encoding(String encoding)
   {
      invocationBuilder.header("Content-Encoding", encoding);
      return this;
   }

   @Override
   public Request.RequestBuilder header(String name, Object value)
   {
      invocationBuilder.header(name, value);
      return this;
   }

   @Override
   public Request.RequestBuilder replaceAll(RequestHeaders headers)
   {
      invocationBuilder.headers(headers);
      return this;
   }

   @Override
   public Request.RequestBuilder language(String language)
   {
      invocationBuilder.getHeaders().setLanguage(language);
      return this;
   }

   @Override
   public Request.RequestBuilder language(Locale language)
   {
      invocationBuilder.getHeaders().setLanguage(language);
      return this;
   }

   @Override
   public Request.RequestBuilder type(MediaType type)
   {
      invocationBuilder.getHeaders().setMediaType(type);
      return this;
   }

   @Override
   public Request.RequestBuilder type(String type)
   {
      invocationBuilder.getHeaders().setMediaType(MediaType.valueOf(type));
      return this;
   }

   @Override
   public Request.RequestBuilder variant(Variant variant)
   {
      encoding(variant.getEncoding());
      language(variant.getLanguage());
      type(variant.getMediaType());
      return this;
   }

   @Override
   public Request.RequestBuilder accept(MediaType... types)
   {
      invocationBuilder.getHeaders().accept(types);
      return this;
   }

   @Override
   public Request.RequestBuilder accept(String... types)
   {
      invocationBuilder.getHeaders().accept(types);
      return this;
   }

   @Override
   public Request.RequestBuilder acceptLanguage(Locale... locales)
   {
      invocationBuilder.getHeaders().acceptLanguage(locales);
      return this;
   }

   @Override
   public Request.RequestBuilder acceptLanguage(String... locales)
   {
      invocationBuilder.getHeaders().acceptLanguage(locales);
      return this;
   }

   @Override
   public Request.RequestBuilder cookie(Cookie cookie)
   {
      invocationBuilder.getHeaders().cookie(cookie);
      return this;
   }

   @Override
   public Request.RequestBuilder redirect(String uri)
   {
      invocationBuilder.invocation.uri = URI.create(uri);
      return this;
   }

   @Override
   public Request.RequestBuilder redirect(URI uri)
   {
      invocationBuilder.invocation.uri = uri;
      return this;
   }

   @Override
   public Request.RequestBuilder redirect(UriBuilder uri)
   {
      invocationBuilder.invocation.uri = uri.build();
      return this;
   }

   @Override
   public Request.RequestBuilder method(String httpMethod)
   {
      invocationBuilder.invocation.method = httpMethod;
      return this;
   }

   @Override
   public Request.RequestBuilder entity(Object entity)
   {
      invocationBuilder.invocation.entity = entity;
      return this;
   }

   @Override
   public Request.RequestBuilder clone()
   {
      return new ClientRequestBuilder(invocationBuilder.invocation);
   }

   @Override
   public Request build()
   {
      return invocationBuilder.invocation;
   }
}
