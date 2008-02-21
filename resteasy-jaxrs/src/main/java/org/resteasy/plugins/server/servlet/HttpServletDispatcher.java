package org.resteasy.plugins.server.servlet;

import org.resteasy.Failure;
import org.resteasy.Headers;
import org.resteasy.ResourceMethod;
import org.resteasy.specimpl.HttpHeadersImpl;
import org.resteasy.specimpl.MultivaluedMapImpl;
import org.resteasy.specimpl.PathSegmentImpl;
import org.resteasy.specimpl.ResponseImpl;
import org.resteasy.specimpl.UriInfoImpl;
import org.resteasy.spi.HttpInput;
import org.resteasy.spi.Registry;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.HttpHeaderNames;
import org.resteasy.util.MediaTypeHelper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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
public class HttpServletDispatcher extends HttpServlet
{


   private ResteasyProviderFactory providerFactory;
   private Registry registry;

   public void init(ServletConfig servletConfig) throws ServletException
   {
      this.providerFactory = (ResteasyProviderFactory) servletConfig.getServletContext().getAttribute(ResteasyProviderFactory.class.getName());
      if (providerFactory == null)
      {
         providerFactory = new ResteasyProviderFactory();
      }


      this.registry = (Registry) servletConfig.getServletContext().getAttribute(Registry.class.getName());
      if (registry == null)
      {
         registry = new Registry(providerFactory);
      }
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return providerFactory;
   }

   public Registry getRegistry()
   {
      return registry;
   }

   public void setProviderFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
   }

   public void setRegistry(Registry registry)
   {
      this.registry = registry;
   }

   protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException
   {
      service(httpServletRequest.getMethod(), httpServletRequest, httpServletResponse);
   }

   /**
    * wrapper around service so we can test easily
    *
    * @param httpServletRequest
    * @param httpServletResponse
    * @throws ServletException
    * @throws IOException
    */
   public void invoke(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException
   {
      service(httpServletRequest, httpServletResponse);
   }

   public void service(String httpMethod, HttpServletRequest request, HttpServletResponse response)
   {
      HttpHeaders headers = extractHttpHeaders(request);
      MultivaluedMapImpl<String, String> parameters = extractParameters(request);
      String path = request.getPathInfo();
      URI absolutePath = null;
      try
      {
         URL absolute = new URL(request.getRequestURL().toString());
         absolutePath = absolute.toURI();
      }
      catch (MalformedURLException e)
      {
         throw new RuntimeException(e);
      }
      catch (URISyntaxException e)
      {
         throw new RuntimeException(e);
      }

      List<PathSegment> pathSegments = PathSegmentImpl.parseSegments(path);
      ResourceMethod invoker = null;
      try
      {
         invoker = registry.getResourceInvoker(httpMethod, pathSegments, headers.getMediaType(), headers.getAcceptableMediaTypes());
      }
      catch (Failure e)
      {
         try
         {
            response.sendError(e.getErrorCode());
         }
         catch (IOException e1)
         {
            throw new RuntimeException(e1);
         }
         e.printStackTrace();
         return;
      }
      if (invoker == null)
      {
         try
         {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
         return;
      }
      if (!invoker.getHttpMethods().contains(httpMethod))
      {
         try
         {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
         return;
      }


      HttpInput in;
      try
      {
         in = new HttpServletInputMessage(headers, request.getInputStream(), new UriInfoImpl(absolutePath, path, request.getQueryString(), pathSegments), parameters);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }


      try
      {
         ResponseImpl responseImpl = null;
         try
         {
            responseImpl = invoker.invoke(in);
         }
         catch (Failure e)
         {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            e.printStackTrace();
            return;
         }
         HttpServletResponseHeaders outputHeaders = new HttpServletResponseHeaders(response, providerFactory);
         if (responseImpl.getMetadata() != null && responseImpl.getMetadata().size() > 0)
         {
            outputHeaders.putAll(responseImpl.getMetadata());
         }
         for (NewCookie cookie : responseImpl.getNewCookies())
         {
            Cookie cook = new Cookie(cookie.getName(), cookie.getValue());
            cook.setMaxAge(cookie.getMaxAge());
            cook.setVersion(cookie.getVersion());
            if (cookie.getDomain() != null) cook.setDomain(cookie.getDomain());
            if (cookie.getPath() != null) cook.setPath(cookie.getPath());
            cook.setSecure(cookie.isSecure());
            if (cookie.getComment() != null) cook.setComment(cookie.getComment());
            response.addCookie(cook);
         }

         if (responseImpl.getEntity() != null)
         {
            MediaType rtnType = invoker.matchByType(in.getHttpHeaders().getAcceptableMediaTypes());
            if (rtnType == null)
            {
               rtnType = MediaType.parse("*/*");
            }
            MessageBodyWriter writer = providerFactory.createMessageBodyWriter(responseImpl.getEntity().getClass(), rtnType);
            if (writer == null)
            {
               throw new RuntimeException("Could not find MessageBodyWriter for response object of type: " + responseImpl.getEntity().getClass() + " of media type: " + rtnType);
            }
            try
            {
               long size = writer.getSize(responseImpl.getEntity());
               response.setContentLength((int) size);
               response.setContentType(rtnType.toString());
               writer.writeTo(responseImpl.getEntity(), rtnType, outputHeaders, response.getOutputStream());
            }
            catch (IOException e)
            {
               throw new RuntimeException(e);
            }
         }
         response.setStatus(responseImpl.getStatus());

      }
      catch (Exception e)
      {
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         e.printStackTrace();
         return;
      }
   }

   public static MultivaluedMapImpl<String, String> extractParameters(HttpServletRequest request)
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

   public static HttpHeaders extractHttpHeaders(HttpServletRequest request)
   {
      HttpHeadersImpl headers = new HttpHeadersImpl();

      MultivaluedMapImpl<String, String> requestHeaders = extractRequestHeaders(request);
      headers.setRequestHeaders(requestHeaders);
      List<MediaType> acceptableMediaTypes = extractAccepts(requestHeaders);
      headers.setAcceptableMediaTypes(acceptableMediaTypes);
      headers.setLanguage(requestHeaders.getFirst(HttpHeaderNames.CONTENT_LANGUAGE));

      String contentType = request.getContentType();
      if (contentType != null) headers.setMediaType(MediaType.parse(contentType));

      Map<String, javax.ws.rs.core.Cookie> cookies = extractCookies(request);
      headers.setCookies(cookies);
      return headers;

   }

   private static Map<String, javax.ws.rs.core.Cookie> extractCookies(HttpServletRequest request)
   {
      Map<String, javax.ws.rs.core.Cookie> cookies = new HashMap<String, javax.ws.rs.core.Cookie>();
      if (request.getCookies() != null)
      {
         for (Cookie cookie : request.getCookies())
         {
            cookies.put(cookie.getName(), new javax.ws.rs.core.Cookie(cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getDomain(), cookie.getVersion()));

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

   public static MultivaluedMapImpl<String, String> extractRequestHeaders(HttpServletRequest request)
   {
      Headers requestHeaders = new Headers();

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
