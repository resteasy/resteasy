package org.jboss.resteasy.plugins.server.grizzly;

import com.sun.grizzly.tcp.http11.GrizzlyRequest;
import com.sun.grizzly.tcp.http11.GrizzlyResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.specimpl.PathSegmentImpl;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.PathSegment;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
abstract public class AbstractGrizzlyDispatcher
{
   protected Dispatcher dispatcher;
   protected String contextPath;

   public AbstractGrizzlyDispatcher(ResteasyProviderFactory providerFactory, String contextPath)
   {
      this.contextPath = contextPath;
      dispatcher = new SynchronousDispatcher(providerFactory);
      if (contextPath == null) throw new RuntimeException("contextPath cannot be null");
      if (!contextPath.startsWith("/")) contextPath = "/" + contextPath;
   }

   public Dispatcher getDispatcher()
   {
      return dispatcher;
   }

   protected String getPathInfo(GrizzlyRequest request)
   {
      String requestURIString = request.getRequestURI();
      URI requestURI = URI.create(requestURIString);

      String path = requestURI.getPath();

      if (contextPath.equals("/")) return path;

      if (path.startsWith(contextPath))
      {
         path = path.substring(contextPath.length());
         return path;
      }
      else
      {
         throw new IllegalStateException("Request path not in servlet context. path: " + path + " contextPath: " + contextPath);
      }
   }

   /**
    * Hack to get around grizzly bug.  Scheme is sometimes null, i don't know why
    *
    * @param request
    * @return
    */
   protected StringBuffer getRequestURL(GrizzlyRequest request)
   {

      StringBuffer url = new StringBuffer();
      String scheme = request.getScheme();
      if (scheme == null) scheme = "http"; // this is a bug in Grizzly
      int port = request.getServerPort();
      if (port < 0)
         port = 80; // Work around java.net.URL bug

      url.append(scheme);
      url.append("://");
      url.append(request.getServerName());
      if ((scheme.equals("http") && (port != 80))
              || (scheme.equals("https") && (port != 443)))
      {
         url.append(':');
         url.append(port);
      }
      url.append(request.getRequestURI());

      return (url);

   }

   protected void invokeJaxrs(GrizzlyRequest request, GrizzlyResponse response)
           throws IOException
   {
      HttpHeaders headers = GrizzlyUtils.extractHttpHeaders(request);
      @SuppressWarnings("unused")
      MultivaluedMapImpl<String, String> parameters = GrizzlyUtils.extractParameters(request);
      String path = getPathInfo(request);
      //System.out.println("path: " + path);
      URI absolutePath = null;
      try
      {
         URL absolute = new URL(getRequestURL(request).toString());
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
      UriInfoImpl uriInfo = new UriInfoImpl(absolutePath, path, request.getQueryString(), pathSegments);

      HttpRequest in;
      try
      {
         in = new GrizzlyHttpRequest(headers, request.getInputStream(), uriInfo, request.getMethod().toUpperCase());
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      HttpResponse theResponse = createHttpResponse(response);

      try
      {
         dispatcher.invoke(in, theResponse);
      }
      catch (Exception e)
      {
         response.sendError(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
      }
   }

   protected HttpResponse createHttpResponse(GrizzlyResponse response)
   {
      HttpResponse theResponse = new GrizzlyHttpResponse(response, dispatcher.getProviderFactory());
      return theResponse;
   }
}
