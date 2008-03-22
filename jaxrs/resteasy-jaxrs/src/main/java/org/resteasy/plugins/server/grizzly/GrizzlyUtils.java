package org.resteasy.plugins.server.grizzly;

import com.sun.grizzly.tcp.http11.GrizzlyRequest;
import org.resteasy.Headers;
import org.resteasy.specimpl.HttpHeadersImpl;
import org.resteasy.specimpl.MultivaluedMapImpl;
import org.resteasy.util.HttpHeaderNames;
import org.resteasy.util.MediaTypeHelper;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GrizzlyUtils
{
   public static MultivaluedMapImpl<String, String> extractParameters(GrizzlyRequest request)
   {
      MultivaluedMapImpl<String, String> parameters = new MultivaluedMapImpl<String, String>();

      Enumeration parameterNames = request.getParameterNames();
      while (parameterNames.hasMoreElements())
      {
         String parameterName = (String) parameterNames.nextElement();
         for (String parameterValue : request.getParameterValues(parameterName))
         {
            parameters.add(parameterName, parameterValue);
         }
      }
      return parameters;
   }

   public static HttpHeaders extractHttpHeaders(GrizzlyRequest request)
   {
      HttpHeadersImpl headers = new HttpHeadersImpl();

      MultivaluedMapImpl<String, String> requestHeaders = extractRequestHeaders(request);
      headers.setRequestHeaders(requestHeaders);
      List<MediaType> acceptableMediaTypes = extractAccepts(requestHeaders);
      headers.setAcceptableMediaTypes(acceptableMediaTypes);
      headers.setLanguage(requestHeaders.getFirst(HttpHeaderNames.CONTENT_LANGUAGE));

      String contentType = request.getContentType();
      if (contentType != null) headers.setMediaType(MediaType.parse(contentType));

      Map<String, Cookie> cookies = extractCookies(request);
      headers.setCookies(cookies);
      return headers;

   }

   static Map<String, Cookie> extractCookies(GrizzlyRequest request)
   {
      Map<String, Cookie> cookies = new HashMap<String, Cookie>();
      if (request.getCookies() != null)
      {
         for (com.sun.grizzly.util.http.Cookie cookie : request.getCookies())
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

   public static MultivaluedMapImpl<String, String> extractRequestHeaders(GrizzlyRequest request)
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
