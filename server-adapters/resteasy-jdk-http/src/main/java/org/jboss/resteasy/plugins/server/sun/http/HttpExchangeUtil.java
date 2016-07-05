package org.jboss.resteasy.plugins.server.sun.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsServer;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.util.CookieParser;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.MediaTypeHelper;
import org.jboss.resteasy.util.PathHelper;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpExchangeUtil
{
   public static ResteasyUriInfo extractUriInfo(HttpExchange exchange)
   {
      String host = exchange.getLocalAddress().getHostName();
      if (exchange.getLocalAddress().getPort() != 80
              && exchange.getLocalAddress().getPort() != 443)
      {
         host += ":" + exchange.getLocalAddress().getPort();
      }
      String uri = exchange.getRequestURI().toString();

      String protocol = exchange.getHttpContext().getServer() instanceof HttpsServer ? "https" : "http";

      URI absoluteURI = URI.create(protocol + "://" + host + uri);

      String contextPath = exchange.getHttpContext().getPath();
      String path = PathHelper.getEncodedPathInfo(absoluteURI.getRawPath(), contextPath);
      if (!path.startsWith("/"))
      {
         path = "/" + path;
      }

      URI baseURI = absoluteURI;
      if (!path.trim().equals(""))
      {
         String tmpContextPath = contextPath;
         if (!tmpContextPath.endsWith("/")) tmpContextPath += "/";
         baseURI = UriBuilder.fromUri(absoluteURI).replacePath(tmpContextPath).replaceQuery(null).build();
      }
      else
      {
         baseURI = UriBuilder.fromUri(absoluteURI).replaceQuery(null).build();
      }
      URI relativeURI = UriBuilder.fromUri(path).replaceQuery(absoluteURI.getRawQuery()).build();
      //System.out.println("path: " + path);
      //System.out.println("query string: " + request.getQueryString());
      ResteasyUriInfo uriInfo = new ResteasyUriInfo(baseURI, relativeURI);
      return uriInfo;
   }

   public static ResteasyHttpHeaders extractHttpHeaders(HttpExchange request)
   {

      MultivaluedMap<String, String> requestHeaders = extractRequestHeaders(request);
      ResteasyHttpHeaders headers = new ResteasyHttpHeaders(requestHeaders);

      Map<String, Cookie> cookies = extractCookies(requestHeaders);
      headers.setCookies(cookies);
      // test parsing should throw an exception on error
      headers.testParsing();
      return headers;

   }

   static Map<String, Cookie> extractCookies(MultivaluedMap<String, String> headers)
   {
      Map<String, Cookie> cookies = new HashMap<String, Cookie>();
      List<String> cookieHeaders = headers.get("Cookie");
      if (cookieHeaders == null) return cookies;

      for (String cookieHeader : cookieHeaders)
      {
         for (Cookie cookie : CookieParser.parseCookies(cookieHeader))
         {
            cookies.put(cookie.getName(), cookie);
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

   public static MultivaluedMap<String, String> extractRequestHeaders(HttpExchange request)
   {
      Headers<String> requestHeaders = new Headers<String>();

      for (Map.Entry<String, List<String>> header : request.getRequestHeaders().entrySet())
      {
         for (String val : header.getValue())
         {
            requestHeaders.add(header.getKey(), val);
         }
      }
      return requestHeaders;
   }
}
