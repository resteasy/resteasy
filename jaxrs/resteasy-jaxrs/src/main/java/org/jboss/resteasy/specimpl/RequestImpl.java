package org.jboss.resteasy.specimpl;

import org.jboss.resteasy.plugins.server.servlet.ServletUtil;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.util.AcceptableVariant;
import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
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
   private HttpHeaders headers;
   private String varyHeader;
   private String httpMethod;
   private HttpRequest request;

   public RequestImpl(HttpRequest request)
   {
      this.headers = request.getHttpHeaders();
      this.httpMethod = request.getHttpMethod().toUpperCase();
      this.request = request;
   }

   public String getMethod()
   {
      return httpMethod;
   }

   public MultivaluedMap<String, String> getFormParameters()
   {
      return request.getDecodedFormParameters();
   }

   public Variant selectVariant(List<Variant> variants) throws IllegalArgumentException
   {
      if (variants == null || variants.size() == 0) throw new IllegalArgumentException("Variant list must not be zero");

      List<MediaType> accepts = headers.getAcceptableMediaTypes();
      List<String> languages = ServletUtil.extractLanguages(headers.getRequestHeaders());
      List<String> encodings = convertString(headers.getRequestHeaders().get(HttpHeaderNames.ACCEPT_ENCODING));


      varyHeader = ResponseBuilderImpl.createVaryHeader(variants);
      return pickVariant(variants, accepts, languages, encodings);
   }

   public static Variant pickVariant(List<Variant> has, List<MediaType> accepts, List<String> languages, List<String> encodings)
   {
      List<AcceptableVariant> wants = new ArrayList<AcceptableVariant>();

      int langSize = languages.size();
      int encodingSize = encodings.size();
      int typeSize = accepts.size();

      int i = 0;

      if (langSize > 0 || encodingSize > 0 || typeSize > 0)
      {
         do
         {
            MediaType type = null;
            if (i < typeSize) type = accepts.get(i);
            int j = 0;
            do
            {
               String encoding = null;
               if (j < encodingSize) encoding = encodings.get(j);
               int k = 0;
               do
               {
                  String language = null;
                  if (k < langSize) language = languages.get(k);
                  wants.add(new AcceptableVariant(type, language, encoding));
                  k++;
               } while (k < langSize);
               j++;
            } while (j < encodingSize);
            i++;
         } while (i < typeSize);
      }


      return AcceptableVariant.pick(has, wants);

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
      List<String> ifMatch = headers.getRequestHeaders().get(HttpHeaderNames.IF_MATCH);
      if (ifMatch != null && ifMatch.size() > 0)
      {
         builder = ifMatch(convertEtag(ifMatch), eTag);
      }
      if (builder == null)
      {
         List<String> ifNoneMatch = headers.getRequestHeaders().get(HttpHeaderNames.IF_NONE_MATCH);
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
      String ifModifiedSince = headers.getRequestHeaders().getFirst(HttpHeaderNames.IF_MODIFIED_SINCE);
      if (ifModifiedSince != null)
      {
         builder = ifModifiedSince(ifModifiedSince, lastModified);
      }
      if (builder == null)
      {
         //System.out.println("ifModified returned null");
         String ifUnmodifiedSince = headers.getRequestHeaders().getFirst(HttpHeaderNames.IF_UNMODIFIED_SINCE);
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
}
