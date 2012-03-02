package org.jboss.resteasy.specimpl;

import org.jboss.resteasy.core.request.ServerDrivenNegotiation;
import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MessageProcessingException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.RequestHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.TypeLiteral;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A request object not attached to a client or server invocation.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BuiltRequest implements Request
{
   protected RequestHeadersImpl headers;
   protected String varyHeader;
   protected String httpMethod;
   protected Object entity;
   protected Annotation[] entityAnnotations;
   protected URI uri;

   public BuiltRequest(String httpMethod, URI uri, RequestHeadersImpl headers, Object entity, Annotation[] entityAnnotations)
   {
      this.httpMethod = httpMethod;
      this.uri = uri;
      this.headers = headers;
      this.entity = entity;
      this.entityAnnotations = entityAnnotations;
   }

   public String getMethod()
   {
      return httpMethod;
   }

   public Variant selectVariant(List<Variant> variants) throws IllegalArgumentException
   {
      if (variants == null || variants.size() == 0) throw new IllegalArgumentException("Variant list must not be zero");

      ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
      MultivaluedMap<String, String> requestHeaders = headers.asMap();
      negotiation.setAcceptHeaders(requestHeaders.get(HttpHeaderNames.ACCEPT));
      negotiation.setAcceptCharsetHeaders(requestHeaders.get(HttpHeaderNames.ACCEPT_CHARSET));
      negotiation.setAcceptEncodingHeaders(requestHeaders.get(HttpHeaderNames.ACCEPT_ENCODING));
      negotiation.setAcceptLanguageHeaders(requestHeaders.get(HttpHeaderNames.ACCEPT_LANGUAGE));

      varyHeader = ResponseBuilderImpl.createVaryHeader(variants);
      return negotiation.getBestMatch(variants);
   }

   public List<EntityTag> convertEtag(List<String> tags)
   {
      ArrayList<EntityTag> result = new ArrayList<EntityTag>();
      for (String tag : tags)
      {
         String[] split = tag.split(",");
         for (String etag : split)
         {
            result.add(EntityTag.valueOf(etag.trim()));
         }
      }
      return result;
   }

   public Response.ResponseBuilder ifMatch(List<EntityTag> ifMatch, EntityTag eTag)
   {
      boolean match = false;
      for (EntityTag tag : ifMatch)
      {
         if (tag.equals(eTag) || tag.getValue().equals("*"))
         {
            match = true;
            break;
         }
      }
      if (match) return null;
      return Response.status(HttpResponseCodes.SC_PRECONDITION_FAILED).tag(eTag);

   }

   public Response.ResponseBuilder ifNoneMatch(List<EntityTag> ifMatch, EntityTag eTag)
   {
      boolean match = false;
      for (EntityTag tag : ifMatch)
      {
         if (tag.equals(eTag) || tag.getValue().equals("*"))
         {
            match = true;
            break;
         }
      }
      if (match)
      {
         if ("GET".equals(httpMethod) || "HEAD".equals(httpMethod))
         {
            return Response.notModified(eTag);
         }

         return Response.status(HttpResponseCodes.SC_PRECONDITION_FAILED).tag(eTag);
      }
      return null;
   }


   public Response.ResponseBuilder evaluatePreconditions(EntityTag eTag)
   {
      Response.ResponseBuilder builder = null;
      List<String> ifMatch = headers.asMap().get(HttpHeaderNames.IF_MATCH);
      if (ifMatch != null && ifMatch.size() > 0)
      {
         builder = ifMatch(convertEtag(ifMatch), eTag);
      }
      if (builder == null)
      {
         List<String> ifNoneMatch = headers.asMap().get(HttpHeaderNames.IF_NONE_MATCH);
         if (ifNoneMatch != null && ifNoneMatch.size() > 0)
         {
            builder = ifNoneMatch(convertEtag(ifNoneMatch), eTag);
         }
      }
      if (builder != null)
      {
         builder.tag(eTag);
      }
      if (builder != null && varyHeader != null) builder.header(HttpHeaderNames.VARY, varyHeader);
      return builder;
   }

   public Response.ResponseBuilder ifModifiedSince(String strDate, Date lastModified)
   {
      Date date = DateUtil.parseDate(strDate);

      if (date.getTime() >= lastModified.getTime())
      {
         return Response.notModified();
      }
      return null;

   }

   public Response.ResponseBuilder ifUnmodifiedSince(String strDate, Date lastModified)
   {
      Date date = DateUtil.parseDate(strDate);

      if (date.getTime() >= lastModified.getTime())
      {
         return null;
      }
      return Response.status(HttpResponseCodes.SC_PRECONDITION_FAILED).lastModified(lastModified);

   }

   public Response.ResponseBuilder evaluatePreconditions(Date lastModified)
   {
      Response.ResponseBuilder builder = null;
      String ifModifiedSince = headers.asMap().getFirst(HttpHeaderNames.IF_MODIFIED_SINCE);
      if (ifModifiedSince != null)
      {
         builder = ifModifiedSince(ifModifiedSince, lastModified);
      }
      if (builder == null)
      {
         //System.out.println("ifModified returned null");
         String ifUnmodifiedSince = headers.asMap().getFirst(HttpHeaderNames.IF_UNMODIFIED_SINCE);
         if (ifUnmodifiedSince != null)
         {
            builder = ifUnmodifiedSince(ifUnmodifiedSince, lastModified);
         }
      }
      if (builder != null && varyHeader != null) builder.header(HttpHeaderNames.VARY, varyHeader);

      return builder;
   }

   public Response.ResponseBuilder evaluatePreconditions(Date lastModified, EntityTag eTag)
   {
      Response.ResponseBuilder rtn = null;
      Response.ResponseBuilder lastModifiedBuilder = evaluatePreconditions(lastModified);
      Response.ResponseBuilder etagBuilder = evaluatePreconditions(eTag);
      if (lastModifiedBuilder == null && etagBuilder == null) rtn = null;
      else if (lastModifiedBuilder != null && etagBuilder == null) rtn = lastModifiedBuilder;
      else if (lastModifiedBuilder == null && etagBuilder != null) rtn = etagBuilder;
      else
      {
         rtn = lastModifiedBuilder;
         rtn.tag(eTag);
      }
      if (rtn != null && varyHeader != null) rtn.header(HttpHeaderNames.VARY, varyHeader);
      return rtn;
   }

   public Response.ResponseBuilder evaluatePreconditions()
   {
      List<String> ifMatch = headers.asMap().get(HttpHeaderNames.IF_MATCH);
      if (ifMatch == null || ifMatch.size() == 0)
      {
         return null;
      }

      return Response.status(HttpResponseCodes.SC_PRECONDITION_FAILED);
   }


   // spec


   @Override
   public <T> T readEntity(Class<T> type) throws MessageProcessingException
   {
      throw new IllegalStateException("Request isn't attached yet");
   }

   @Override
   public <T> T readEntity(TypeLiteral<T> entityType) throws MessageProcessingException
   {
      throw new IllegalStateException("Request isn't attached yet");
   }

   @Override
   public Map<String, Object> getProperties()
   {
      throw new IllegalStateException("Request isn't attached yet");
   }

   @Override
   public <T> T readEntity(Class<T> type, Annotation[] annotations) throws MessageProcessingException
   {
      throw new IllegalStateException("Request isn't attached yet");
   }

   @Override
   public <T> T readEntity(TypeLiteral<T> entityType, Annotation[] annotations) throws MessageProcessingException
   {
      throw new IllegalStateException("Request isn't attached yet");
   }

   @Override
   public RequestHeaders getHeaders()
   {
      return headers;
   }

   @Override
   public URI getUri()
   {
      return uri;
   }

   @Override
   public boolean hasEntity()
   {
      return entity != null;
   }

   @Override
   public Object getEntity()
   {
      return entity;
   }

   public Annotation[] getEntityAnnotations()
   {
      return entityAnnotations;
   }

   @Override
   public boolean isEntityRetrievable()
   {
      return false;
   }

   @Override
   public void bufferEntity() throws MessageProcessingException
   {
      throw new IllegalStateException("Request isn't attached yet");
   }

   @Override
   public void close() throws MessageProcessingException
   {
      throw new IllegalStateException("Request isn't attached yet");
   }
}
