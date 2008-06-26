package org.resteasy.plugins.server.servlet;

import org.resteasy.specimpl.UriInfoImpl;
import org.resteasy.specimpl.UriBuilderImpl;
import org.resteasy.specimpl.PathSegmentImpl;
import org.resteasy.specimpl.HttpHeadersImpl;
import org.resteasy.specimpl.MultivaluedMapImpl;
import org.resteasy.util.PathHelper;
import org.resteasy.util.HttpHeaderNames;
import org.resteasy.util.MediaTypeHelper;
import org.resteasy.Headers;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Cookie;
import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServletUtil
{
   public static UriInfoImpl extractUriInfo(HttpServletRequest request)
   {
      String path = PathHelper.getEncodedPathInfo(request.getRequestURI(), request.getContextPath());
      URI absolutePath = null;
      try
      {
         URL absolute = new URL(request.getRequestURL().toString());

         UriBuilderImpl builder = new UriBuilderImpl();
         builder.scheme(absolute.getProtocol());
         builder.host(absolute.getHost());
         builder.port(absolute.getPort());
         builder.path(absolute.getPath());
         builder.replaceQueryParams(absolute.getQuery());
         absolutePath = builder.build();
      }
      catch (MalformedURLException e)
      {
         throw new RuntimeException(e);
      }

      List<PathSegment> pathSegments = PathSegmentImpl.parseSegments(path);
      UriInfoImpl uriInfo = new UriInfoImpl(absolutePath, path, request.getQueryString(), pathSegments);
      return uriInfo;
   }

   public static HttpHeaders extractHttpHeaders(HttpServletRequest request)
   {
      HttpHeadersImpl headers = new HttpHeadersImpl();

      MultivaluedMapImpl<String, String> requestHeaders = extractRequestHeaders(request);
      headers.setRequestHeaders(requestHeaders);
      List<MediaType> acceptableMediaTypes = extractAccepts(requestHeaders);
      List<String> acceptableLanguages = extractLanguages(requestHeaders);
      headers.setAcceptableMediaTypes(acceptableMediaTypes);
      headers.setAcceptableLanguages(acceptableLanguages);
      headers.setLanguage(requestHeaders.getFirst(HttpHeaderNames.CONTENT_LANGUAGE));

      String contentType = request.getContentType();
      if (contentType != null) headers.setMediaType(MediaType.valueOf(contentType));

      Map<String, Cookie> cookies = extractCookies(request);
      headers.setCookies(cookies);
      return headers;

   }

   static Map<String, Cookie> extractCookies(HttpServletRequest request)
   {
      Map<String, Cookie> cookies = new HashMap<String, Cookie>();
      if (request.getCookies() != null)
      {
         for (javax.servlet.http.Cookie cookie : request.getCookies())
         {
            cookies.put(cookie.getName(), new Cookie(cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getDomain(), cookie.getVersion()));

         }
      }
      return cookies;
   }

   public static List<MediaType> extractAccepts(MultivaluedMapImpl<String, String> requestHeaders)
   {
      List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
      List<String> accepts = requestHeaders.get(HttpHeaderNames.ACCEPT);
      if (accepts == null) return acceptableMediaTypes;

      for (String accept : accepts)
      {
         acceptableMediaTypes.addAll(MediaTypeHelper.parseHeader(accept));
      }
      return acceptableMediaTypes;
   }

   public static List<String> extractLanguages(MultivaluedMapImpl<String, String> requestHeaders)
   {
      List<String> acceptable = new ArrayList<String>();
      List<String> accepts = requestHeaders.get(HttpHeaderNames.ACCEPT_LANGUAGE);
      if (accepts == null) return acceptable;

      for (String accept : accepts)
      {
         String[] splits = accept.split(",");
         for (String split : splits) acceptable.add(split.trim());
      }
      return acceptable;
   }

   public static MultivaluedMapImpl<String, String> extractRequestHeaders(HttpServletRequest request)
   {
      Headers<String> requestHeaders = new Headers<String>();

      Enumeration headerNames = request.getHeaderNames();
      while (headerNames.hasMoreElements())
      {
         String headerName = (String) headerNames.nextElement();
         Enumeration headerValues = request.getHeaders(headerName);
         while (headerValues.hasMoreElements())
         {
            String headerValue = (String) headerValues.nextElement();
            //System.out.println("ADDING HEADER: " + headerName + " value: " + headerValue);
            requestHeaders.add(headerName, headerValue);
         }
      }
      return requestHeaders;
   }
}
