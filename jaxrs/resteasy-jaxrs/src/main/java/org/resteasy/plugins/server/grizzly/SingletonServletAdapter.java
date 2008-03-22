package org.resteasy.plugins.server.grizzly;

import com.sun.grizzly.http.servlet.CookieWrapper;
import com.sun.grizzly.http.servlet.HttpServletRequestImpl;
import com.sun.grizzly.http.servlet.HttpServletResponseImpl;
import com.sun.grizzly.http.servlet.ServletConfigImpl;
import com.sun.grizzly.http.servlet.ServletContextImpl;
import com.sun.grizzly.tcp.http11.GrizzlyAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyRequest;
import com.sun.grizzly.tcp.http11.GrizzlyResponse;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Adapter class that can initiate a Servlet and execute it.
 *
 * @author Jeanfrancois Arcand
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SingletonServletAdapter extends GrizzlyAdapter
{
   private Servlet servletInstance = null;
   private ServletConfigImpl servletConfig;
   private String contextPath = "/";

   /**
    * The merged context initialization parameters for this Context.
    */
   private HashMap<String, String> parameters = new HashMap<String, String>();


   /**
    * Stupid Grizzly has setInitParameter as protected
    */
   private class ServletContextWrapper extends ServletContextImpl
   {
      private ServletContextWrapper()
      {
         setInitParameter(parameters);
      }
   }

   /**
    * Wrapper class to fix the multitude of bugs within Grizzly's servlet implementation
    */
   private class HttpRequestWrapper extends HttpServletRequestImpl
   {
      private HttpRequestWrapper(GrizzlyRequest grizzlyRequest)
              throws IOException
      {
         super(grizzlyRequest);
      }

      @Override
      public Cookie[] getCookies()
      {
         com.sun.grizzly.util.http.Cookie[] internalCookies = request.getCookies();
         if (internalCookies == null) return null;
         javax.servlet.http.Cookie[] cookies
                 = new javax.servlet.http.Cookie[internalCookies.length];
         for (int i = 0; i < internalCookies.length; i++)
         {
            com.sun.grizzly.util.http.Cookie cook = internalCookies[i];
            if (cook instanceof CookieWrapper) cookies[i] = ((CookieWrapper) internalCookies[i]).getWrappedCookie();
            else
            {
               cookies[i] = new Cookie(cook.getName(), cook.getValue());
               cookies[i].setComment(cook.getComment());
               if (cook.getDomain() != null) cookies[i].setDomain(cook.getDomain());
               cookies[i].setMaxAge(cook.getMaxAge());
               cookies[i].setPath(cook.getPath());
               cookies[i].setSecure(cook.getSecure());
               cookies[i].setVersion(cook.getVersion());
            }
         }
         return cookies;

      }

      public String getContextPath()
      {
         return contextPath;
      }

      public String getPathInfo()
      {
         if (request == null)
         {
            throw new IllegalStateException(
                    sm.getString("requestFacade.nullRequest"));
         }

         String requestURIString = request.getRequestURI();
         URI requestURI = URI.create(requestURIString);

         String path = requestURI.getPath();
         String contextPath = getContextPath();

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

      @Override
      public StringBuffer getRequestURL()
      {
         StringBuffer url = new StringBuffer();
         String scheme = "http";
         int port = getServerPort();
         if (port < 0)
            port = 80; // Work around java.net.URL bug

         url.append(scheme);
         url.append("://");
         url.append(getServerName());
         if ((scheme.equals("http") && (port != 80))
                 || (scheme.equals("https") && (port != 443)))
         {
            url.append(':');
            url.append(port);
         }
         url.append(getRequestURI());

         return (url);
      }
   }

   public class HttpResponseWrapper extends HttpServletResponseImpl
   {
      public HttpResponseWrapper(GrizzlyResponse response)
              throws IOException
      {
         super(response);
      }

      @Override
      public void sendError(int sc) throws IOException
      {
         sendError(sc, null);
      }

      @Override
      public void sendError(int sc, String msg) throws IOException
      {

         if (isCommitted())
            throw new IllegalStateException
                    ();

         response.setAppCommitted(true);

         if (response.isCommitted())
            throw new IllegalStateException("response is already committed");

         response.setError();

         response.getResponse().setStatus(sc);
         response.getResponse().setMessage(msg);

         // Clear any data content that has been buffered
         response.resetBuffer();

         // Cause the response to be finished (from the application perspective)
         //response.setSuspended(true);
      }
   }


   public SingletonServletAdapter()
   {
      super();
   }


   public SingletonServletAdapter(String publicDirectory)
   {
      super(publicDirectory);
      this.contextPath = publicDirectory;
   }

   public void init() throws ServletException
   {
      ServletContextWrapper servletCtx = new ServletContextWrapper();
      servletConfig = new ServletConfigImpl(servletCtx);
      servletInstance.init(servletConfig);
   }


   @Override
   public void service(GrizzlyRequest request, GrizzlyResponse response)
   {
      try
      {
         HttpServletRequestImpl httpRequest = new HttpRequestWrapper(request);
         HttpServletResponseImpl httpResponse = new HttpResponseWrapper(response);
         httpResponse.addHeader("server", "grizzly/1.7");
         servletInstance.service(httpRequest, httpResponse);
      }
      catch (Throwable ex)
      {
         logger.log(Level.SEVERE, "service exception:", ex);
      }
   }


   @Override
   public void afterService(GrizzlyRequest request, GrizzlyResponse response) throws Exception
   {
   }


   /**
    * Add a new servlet initialization parameter for this servlet.
    *
    * @param name  Name of this initialization parameter to add
    * @param value Value of this initialization parameter to add
    */
   public void addInitParameter(String name, String value)
   {
      parameters.put(name, value);
   }


   public Servlet getServletInstance()
   {
      return servletInstance;
   }


   public void setServletInstance(Servlet servletInstance)
   {
      this.servletInstance = servletInstance;
   }
}
