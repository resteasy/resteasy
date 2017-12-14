package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.MediaTypeHelper;
import org.jboss.resteasy.util.PathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServletUtil
{
   public static ResteasyUriInfo extractUriInfo(HttpServletRequest request, String servletPrefix)
   {
       String contextPath = request.getContextPath();
       if (servletPrefix != null && servletPrefix.length() > 0 && !servletPrefix.equals("/"))
       {
           if (!contextPath.endsWith("/") && !servletPrefix.startsWith("/"))
               contextPath += "/";
           contextPath += servletPrefix;
       }
       return new ResteasyUriInfo(request.getRequestURL(), request.getQueryString(), contextPath);
   }

   public static ResteasyHttpHeaders extractHttpHeaders(HttpServletRequest request)
   {

      MultivaluedMap<String, String> requestHeaders = extractRequestHeaders(request);
      ResteasyHttpHeaders headers = new ResteasyHttpHeaders(requestHeaders);

      String contentType = request.getContentType();
      if (contentType != null) headers.getMutableHeaders().putSingle(HttpHeaders.CONTENT_TYPE, contentType);

      Map<String, Cookie> cookies = extractCookies(request);
      headers.setCookies(cookies);

      // test parsing should throw an exception on error
      headers.testParsing();

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

   public static List<MediaType> extractAccepts(MultivaluedMap<String, String> requestHeaders)
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

   public static List<String> extractLanguages(MultivaluedMap<String, String> requestHeaders)
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

   @SuppressWarnings("unchecked")
   public static MultivaluedMap<String, String> extractRequestHeaders(HttpServletRequest request)
   {
      Headers<String> requestHeaders = new Headers<String>();

      Enumeration<String> headerNames = request.getHeaderNames();
      while (headerNames.hasMoreElements())
      {
         String headerName = headerNames.nextElement();
         Enumeration<String> headerValues = request.getHeaders(headerName);
         while (headerValues.hasMoreElements())
         {
            String headerValue = headerValues.nextElement();
            requestHeaders.add(headerName, headerValue);
         }
      }
      return requestHeaders;
   }
}
