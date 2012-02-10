package org.jboss.resteasy.client.impl;

import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MessageProcessingException;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.ResponseHeaders;
import javax.ws.rs.core.Variant;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientResponseBuilder extends Response.ResponseBuilder
{
   public ClientResponseBuilder(ResteasyProviderFactory providerFactory, Map<String, Object> properties)
   {
      response.setProperties(properties);
      response.setProviderFactory(providerFactory);
   }

   protected ClientResponseBuilder(ClientResponseBuilder copy)
   {
      response.status = copy.response.status;
      CaseInsensitiveMap<String> headers = new CaseInsensitiveMap<String>();
      headers.putAll(copy.response.headers);
      response.headers = headers;
      response.entity = copy.response.entity;
      response.providerFactory = copy.response.providerFactory;
      response.isClosed = copy.response.isClosed;

   }

   protected ClientResponse response = new ClientResponse()
   {
      @Override
      protected InputStream getInputStream()
      {
         return null;
      }

      @Override
      protected void releaseConnection()
      {
      }

      @Override
      public void bufferEntity() throws MessageProcessingException
      {
      }
   };

   @Override
   public Response build()
   {
      return response;
   }

   @Override
   public Response.ResponseBuilder clone()
   {
      /*
   protected int status;
   protected MultivaluedMap<String, String> headers;
   protected Map<String, Object> properties;
   protected Object entity;
   protected ResteasyProviderFactory providerFactory;
   protected boolean isClosed;

       */
      return new ClientResponseBuilder(this);
   }

   @Override
   public Response.ResponseBuilder status(int status)
   {
      response.setStatus(status);
      return this;
   }

   @Override
   public Response.ResponseBuilder entity(Object entity)
   {
      response.entity = entity;
      return this;
   }

   @Override
   public Response.ResponseBuilder allow(String... methods)
   {
      HeaderHelper.setAllow(response.headers, methods);
      return this;
   }

   @Override
   public Response.ResponseBuilder allow(Set<String> methods)
   {
      HeaderHelper.setAllow(response.headers, methods);
      return this;
   }

   @Override
   public Response.ResponseBuilder cacheControl(CacheControl cacheControl)
   {
      return putHeader("Cache-Control", cacheControl);
   }

   @Override
   public Response.ResponseBuilder encoding(String encoding)
   {
      return putHeader("Content-Encoding", encoding);
   }

   @Override
   public Response.ResponseBuilder header(String name, Object value)
   {
      if (value != null) response.headers.add(name, HeaderHelper.toHeaderString(value, response.providerFactory));
      else response.headers.remove(name);
      return null;
   }

   public Response.ResponseBuilder putHeader(String name, Object value)
   {
      if (value != null) response.headers.putSingle(name, HeaderHelper.toHeaderString(value, response.providerFactory));
      else response.headers.remove(name);
      return null;
   }

   @Override
   public Response.ResponseBuilder replaceAll(ResponseHeaders headers)
   {
      response.headers.clear();
      response.headers.putAll(headers.asMap());
      return this;
   }

   @Override
   public Response.ResponseBuilder language(String language)
   {
      return putHeader("Language", language);
   }

   @Override
   public Response.ResponseBuilder language(Locale language)
   {
      return putHeader("Language", language);
   }

   @Override
   public Response.ResponseBuilder type(MediaType type)
   {
      return putHeader("Content-Type", type);
   }

   @Override
   public Response.ResponseBuilder type(String type)
   {
      return putHeader("Content-Type", type);
   }

   @Override
   public Response.ResponseBuilder variant(Variant variant)
   {
      encoding(variant.getEncoding());
      type(variant.getMediaType());
      language(variant.getLanguage());
      return this;
   }

   @Override
   public Response.ResponseBuilder contentLocation(URI location)
   {
      return putHeader("Content-Location", location);
   }

   @Override
   public Response.ResponseBuilder cookie(NewCookie... cookies)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Response.ResponseBuilder expires(Date expires)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Response.ResponseBuilder lastModified(Date lastModified)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Response.ResponseBuilder location(URI location)
   {
      return putHeader("Location", location.toString());
   }

   @Override
   public Response.ResponseBuilder tag(EntityTag tag)
   {
      return putHeader("ETag", tag);
   }

   @Override
   public Response.ResponseBuilder tag(String tag)
   {
      return putHeader("ETag", tag);
   }

   @Override
   public Response.ResponseBuilder variants(Variant... variants)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Response.ResponseBuilder variants(List<Variant> variants)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Response.ResponseBuilder links(Link... links)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Response.ResponseBuilder link(URI uri, String rel)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Response.ResponseBuilder link(String uri, String rel)
   {
      throw new NotImplementedYetException();
   }
}
