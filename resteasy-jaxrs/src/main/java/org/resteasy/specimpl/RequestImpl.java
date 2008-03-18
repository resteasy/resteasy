package org.resteasy.specimpl;

import org.resteasy.util.DateUtil;
import org.resteasy.util.HttpHeaderNames;
import org.resteasy.util.HttpResponseCodes;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
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
   private HttpHeaders headers;
   private VaryHeader vary;
   private String httpMethod;

   public RequestImpl(HttpHeaders headers, VaryHeader vary, String httpMethod)
   {
      this.headers = headers;
      this.vary = vary;
      this.httpMethod = httpMethod.toUpperCase();
   }


   public Variant selectVariant(List<Variant> variants) throws IllegalArgumentException
   {
      if (variants == null || variants.size() == 0) throw new IllegalArgumentException("Variant list must not be zero");

      List<MediaType> accepts = headers.getAcceptableMediaTypes();
      List<String> languages = convertString(headers.getRequestHeaders().get(HttpHeaderNames.ACCEPT_LANGUAGE));
      List<String> encodings = convertString(headers.getRequestHeaders().get(HttpHeaderNames.ACCEPT_ENCODING));


      return null;
   }

   public static Variant pickVariant(List<Variant> variants, List<MediaType> accepts, List<String> languages, List<String> encodings)
   {
      VariantListBuilderImpl builder = new VariantListBuilderImpl();

      if (accepts != null)
      {
         for (MediaType accept : accepts)
         {
            builder.mediaTypes(accept);
         }
      }

      for (String language : languages) builder.languages(language);
      for (String encoding : encodings) builder.encodings(encoding);

      List<Variant> acceptVariants = builder.add().build();
      return null;

   }


   public List<String> convertString(List<String> tags)
   {
      ArrayList<String> result = new ArrayList<String>();
      if (tags == null) return result;
      for (String tag : tags)
      {
         String[] split = tag.split(",");
         for (String etag : split)
         {
            result.add(etag.trim());
         }
      }
      return result;
   }

   public List<EntityTag> convertEtag(List<String> tags)
   {
      ArrayList<EntityTag> result = new ArrayList<EntityTag>();
      for (String tag : tags)
      {
         String[] split = tag.split(",");
         for (String etag : split)
         {
            result.add(EntityTag.parse(etag.trim()));
         }
      }
      return result;
   }

   public Response.ResponseBuilder ifMatch(List<EntityTag> ifMatch, EntityTag eTag)
   {
      boolean match = false;
      for (EntityTag tag : ifMatch)
      {
         if (tag.equals(eTag))
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
         if (tag.equals(eTag))
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
      List<String> ifMatch = headers.getRequestHeaders().get(HttpHeaderNames.IF_MATCH);
      if (ifMatch != null && ifMatch.size() > 0)
      {
         return ifMatch(convertEtag(ifMatch), eTag);
      }
      List<String> ifNoneMatch = headers.getRequestHeaders().get(HttpHeaderNames.IF_NONE_MATCH);
      if (ifNoneMatch != null && ifNoneMatch.size() > 0)
      {
         return ifNoneMatch(convertEtag(ifMatch), eTag);
      }
      return null;
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
      String ifModifiedSince = headers.getRequestHeaders().getFirst(HttpHeaderNames.IF_MODIFIED_SINCE);
      if (ifModifiedSince != null)
      {
         return ifModifiedSince(ifModifiedSince, lastModified);
      }
      String ifUnmodifiedSince = headers.getRequestHeaders().getFirst(HttpHeaderNames.IF_UNMODIFIED_SINCE);
      if (ifUnmodifiedSince != null)
      {
         return ifUnmodifiedSince(ifUnmodifiedSince, lastModified);
      }

      return null;
   }

   public Response.ResponseBuilder evaluatePreconditions(Date lastModified, EntityTag eTag)
   {
      Response.ResponseBuilder builder = evaluatePreconditions(lastModified);
      Response.ResponseBuilder builder2 = evaluatePreconditions(eTag);
      if (builder == null && builder2 == null) return null;
      if (builder != null && builder2 == null) return builder;
      if (builder == null && builder2 != null) return builder2;
      builder.tag(eTag);
      return builder;
   }
}
