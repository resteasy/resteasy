package org.jboss.resteasy.specimpl;

import org.jboss.resteasy.core.request.ServerDrivenNegotiation;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.DateUtil;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.jboss.resteasy.resteasy_jaxrs.i18n.Messages.MESSAGES;
import static org.jboss.resteasy.util.HttpHeaderNames.*;
import static org.jboss.resteasy.util.HttpResponseCodes.SC_PRECONDITION_FAILED;

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
   private HttpResponse response;

   public RequestImpl(HttpRequest request, HttpResponse response)
   {
      this.headers = request.getHttpHeaders();
      this.httpMethod = request.getHttpMethod().toUpperCase();
      this.request = request;
      this.response = response;
   }

   public String getMethod()
   {
      return httpMethod;
   }

   private boolean isRfc7232preconditions() {
      ResteasyConfiguration context = ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
      return context != null && Boolean.parseBoolean(context.getParameter("resteasy.rfc7232preconditions"));
   }

   public MultivaluedMap<String, String> getFormParameters()
   {
      return request.getDecodedFormParameters();
   }

   public Variant selectVariant(List<Variant> variants) throws IllegalArgumentException
   {
      if (variants == null || variants.size() == 0) throw new IllegalArgumentException(MESSAGES.variantListMustNotBeZero());
      
      ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
      MultivaluedMap<String, String> requestHeaders = headers.getRequestHeaders();
      negotiation.setAcceptHeaders(requestHeaders.get(ACCEPT));
      negotiation.setAcceptCharsetHeaders(requestHeaders.get(ACCEPT_CHARSET));
      negotiation.setAcceptEncodingHeaders(requestHeaders.get(ACCEPT_ENCODING));
      negotiation.setAcceptLanguageHeaders(requestHeaders.get(ACCEPT_LANGUAGE));

      varyHeader = ResponseBuilderImpl.createVaryHeader(variants);
      response.getOutputHeaders().add(VARY, varyHeader);
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
      return Response.status(SC_PRECONDITION_FAILED).tag(eTag);

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

         return Response.status(SC_PRECONDITION_FAILED).tag(eTag);
      }
      return null;
   }


   public Response.ResponseBuilder evaluatePreconditions(EntityTag eTag)
   {
      if (eTag == null) throw new IllegalArgumentException(MESSAGES.eTagParamNull());
      Response.ResponseBuilder builder = null;
      List<String> ifMatch = headers.getRequestHeaders().get(IF_MATCH);
      if (ifMatch != null && ifMatch.size() > 0)
      {
         builder = ifMatch(convertEtag(ifMatch), eTag);
      }
      if (builder == null)
      {
         List<String> ifNoneMatch = headers.getRequestHeaders().get(IF_NONE_MATCH);
         if (ifNoneMatch != null && ifNoneMatch.size() > 0)
         {
            builder = ifNoneMatch(convertEtag(ifNoneMatch), eTag);
         }
      }
      if (builder != null)
      {
         builder.tag(eTag);
      }
      if (builder != null && varyHeader != null) builder.header(VARY, varyHeader);
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
      return Response.status(SC_PRECONDITION_FAILED).lastModified(lastModified);

   }

   public Response.ResponseBuilder evaluatePreconditions(Date lastModified)
   {
      if (lastModified == null) throw new IllegalArgumentException(MESSAGES.lastModifiedParamNull());
      Response.ResponseBuilder builder = null;
      MultivaluedMap<String, String> headers = this.headers.getRequestHeaders();
      String ifModifiedSince = headers.getFirst(IF_MODIFIED_SINCE);
      if (ifModifiedSince != null && (!isRfc7232preconditions() || (!headers.containsKey(IF_NONE_MATCH))))
      {
         builder = ifModifiedSince(ifModifiedSince, lastModified);
      }
      if (builder == null)
      {
         String ifUnmodifiedSince = headers.getFirst(IF_UNMODIFIED_SINCE);
         if (ifUnmodifiedSince != null && (!isRfc7232preconditions() || (!headers.containsKey(IF_MATCH))))
         {
            builder = ifUnmodifiedSince(ifUnmodifiedSince, lastModified);
         }
      }
      if (builder != null && varyHeader != null) builder.header(VARY, varyHeader);

      return builder;
   }

   public Response.ResponseBuilder evaluatePreconditions(Date lastModified, EntityTag eTag)
   {
      if (lastModified == null) throw new IllegalArgumentException(MESSAGES.lastModifiedParamNull());
      if (eTag == null) throw new IllegalArgumentException(MESSAGES.eTagParamNull());
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
      if (rtn != null && varyHeader != null) rtn.header(VARY, varyHeader);
      return rtn;
   }

   public Response.ResponseBuilder evaluatePreconditions()
   {
      List<String> ifMatch = headers.getRequestHeaders().get(IF_MATCH);
      if (ifMatch == null || ifMatch.size() == 0)
      {
         return null;
      }

      return Response.status(SC_PRECONDITION_FAILED);
   }


}
