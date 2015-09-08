package org.jboss.resteasy.specimpl;

import org.jboss.resteasy.core.request.ServerDrivenNegotiation;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RequestImpl implements Request
{
   private final HttpHeaders headers;
   private String varyHeader;
   private final String httpMethod;
   private final HttpRequest request;
   private final HttpResponse response;

   public RequestImpl(HttpRequest request, HttpResponse response)
   {
      this.headers = request.getHttpHeaders();
      this.httpMethod = request.getHttpMethod().toUpperCase();
      this.request = request;
      this.response = response;
   }

   @Override
   public String getMethod()
   {
      return httpMethod;
   }

   public MultivaluedMap<String, String> getFormParameters()
   {
      return request.getDecodedFormParameters();
   }

   @Override
   public Variant selectVariant(List<Variant> variants) throws IllegalArgumentException
   {
      if (variants == null || variants.isEmpty()) throw new IllegalArgumentException("Variant list must not be zero");

      ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
      MultivaluedMap<String, String> requestHeaders = headers.getRequestHeaders();
      negotiation.setAcceptHeaders(requestHeaders.get(HttpHeaderNames.ACCEPT));
      negotiation.setAcceptCharsetHeaders(requestHeaders.get(HttpHeaderNames.ACCEPT_CHARSET));
      negotiation.setAcceptEncodingHeaders(requestHeaders.get(HttpHeaderNames.ACCEPT_ENCODING));
      negotiation.setAcceptLanguageHeaders(requestHeaders.get(HttpHeaderNames.ACCEPT_LANGUAGE));

      varyHeader = ResponseBuilderImpl.createVaryHeader(variants);
      response.getOutputHeaders().add(HttpHeaderNames.VARY, varyHeader);
      return negotiation.getBestMatch(variants);
   }

   private Response.ResponseBuilder addVariant(Response.ResponseBuilder builder) {
      if (builder != null && varyHeader != null) builder.header(HttpHeaderNames.VARY, varyHeader);
      return builder;
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

   public Response.ResponseBuilder ifMatch(EntityTag eTag)
   {
      boolean match = false;

      List<String> values = headers.getRequestHeaders().get(HttpHeaderNames.IF_MATCH);
      if (values == null || values.isEmpty())
      {
         return null;
      }

      for (EntityTag tag : convertEtag(values))
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

   public Response.ResponseBuilder ifNoneMatch(EntityTag eTag)
   {
      boolean match = false;

      List<String> values = headers.getRequestHeaders().get(HttpHeaderNames.IF_NONE_MATCH);
      if (values == null || values.isEmpty())
      {
         return null;
      }

      for (EntityTag tag : convertEtag(values))
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

   public Response.ResponseBuilder ifModifiedSince(Date lastModified)
   {
      String ifModifiedSince = headers.getRequestHeaders().getFirst(HttpHeaderNames.IF_MODIFIED_SINCE);
      if (ifModifiedSince == null)
      {
         return null;
      }

      Date date = DateUtil.parseDate(ifModifiedSince);

      if (date.getTime() >= lastModified.getTime())
      {
         return Response.notModified();
      }

      return null;
   }

   public Response.ResponseBuilder ifUnmodifiedSince(Date lastModified)
   {
      String ifUnmodifiedSince = headers.getRequestHeaders().getFirst(HttpHeaderNames.IF_UNMODIFIED_SINCE);
      if (ifUnmodifiedSince == null)
      {
         return null;
      }

      Date date = DateUtil.parseDate(ifUnmodifiedSince);

      if (date.getTime() >= lastModified.getTime())
      {
         return null;
      }

      return Response.status(HttpResponseCodes.SC_PRECONDITION_FAILED).lastModified(lastModified);
   }

   private boolean hasIfMatchHeader()
   {
      return headers.getRequestHeaders().getFirst(HttpHeaderNames.IF_MATCH) != null;
   }

   private boolean hasIfNoneMatchHeader()
   {
      return headers.getRequestHeaders().getFirst(HttpHeaderNames.IF_NONE_MATCH) != null;
   }

   @Override
   public Response.ResponseBuilder evaluatePreconditions(EntityTag eTag)
   {
      if (eTag == null) throw new IllegalArgumentException("eTag param null");
      Response.ResponseBuilder builder;

      builder = ifMatch(eTag);
      if (builder == null)
      {
        builder = ifNoneMatch(eTag);
      }

      builder = addVariant(builder);
      return builder;
   }

   @Override
   public Response.ResponseBuilder evaluatePreconditions(Date lastModified)
   {
      if (lastModified == null) throw new IllegalArgumentException("lastModified param null");
      Response.ResponseBuilder builder;

      builder = ifModifiedSince(lastModified);
      if (builder == null)
      {
         builder = ifUnmodifiedSince(lastModified);
      }

      builder = addVariant(builder);
      return builder;
   }

   @Override
   public Response.ResponseBuilder evaluatePreconditions(Date lastModified, EntityTag eTag)
   {
      if (lastModified == null) throw new IllegalArgumentException("lastModified param null");
      if (eTag == null) throw new IllegalArgumentException("eTag param null");
      Response.ResponseBuilder builder;

      if (hasIfMatchHeader()) {
         builder = ifMatch(eTag);
      }
      else
      {
         builder = ifUnmodifiedSince(lastModified);
         if (builder != null) builder.tag(eTag);
      }
      if (builder != null) return addVariant(builder);


      if (hasIfNoneMatchHeader())
      {
         builder = ifNoneMatch(eTag);
      }
      else if ("GET".equals(httpMethod) || "HEAD".equals(httpMethod))
      {
         builder = ifModifiedSince(lastModified);
         if (builder != null) builder.tag(eTag);
      }
      return addVariant(builder);

   }

   @Override
   public Response.ResponseBuilder evaluatePreconditions()
   {
      List<String> ifMatch = headers.getRequestHeaders().get(HttpHeaderNames.IF_MATCH);
      if (ifMatch == null || ifMatch.isEmpty())
      {
         return null;
      }

      return Response.status(HttpResponseCodes.SC_PRECONDITION_FAILED);
   }

}
