package org.jboss.resteasy.util;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServletRequestDelegate implements HttpServletRequest
{
   protected HttpServletRequest delegate;

   public boolean isUserInRole(String s)
   {
      return delegate.isUserInRole(s);
   }

   public Principal getUserPrincipal()
   {
      return delegate.getUserPrincipal();
   }

   public HttpServletRequestDelegate(HttpServletRequest delegate)
   {
      this.delegate = delegate;
   }

   public String getAuthType()
   {
      return delegate.getAuthType();
   }

   public int getLocalPort()
   {
      return delegate.getLocalPort();
   }

   public Cookie[] getCookies()
   {
      return delegate.getCookies();
   }

   public long getDateHeader(String s)
   {
      return delegate.getDateHeader(s);
   }

   public String getHeader(String s)
   {
      return delegate.getHeader(s);
   }

   public Enumeration<String> getHeaders(String s)
   {
      return delegate.getHeaders(s);
   }

   public Enumeration<String> getHeaderNames()
   {
      return delegate.getHeaderNames();
   }

   public int getIntHeader(String s)
   {
      return delegate.getIntHeader(s);
   }

   public String getMethod()
   {
      return delegate.getMethod();
   }

   public String getPathInfo()
   {
      return delegate.getPathInfo();
   }

   public String getPathTranslated()
   {
      return delegate.getPathTranslated();
   }

   public String getContextPath()
   {
      return delegate.getContextPath();
   }

   public String getQueryString()
   {
      return delegate.getQueryString();
   }

   public String getRemoteUser()
   {
      return delegate.getRemoteUser();
   }

   public String getRequestedSessionId()
   {
      return delegate.getRequestedSessionId();
   }

   public String getRequestURI()
   {
      return delegate.getRequestURI();
   }

   public StringBuffer getRequestURL()
   {
      return delegate.getRequestURL();
   }

   public String getServletPath()
   {
      return delegate.getServletPath();
   }

   public HttpSession getSession(boolean b)
   {
      return delegate.getSession(b);
   }

   public HttpSession getSession()
   {
      return delegate.getSession();
   }

   public boolean isRequestedSessionIdValid()
   {
      return delegate.isRequestedSessionIdValid();
   }

   public boolean isRequestedSessionIdFromCookie()
   {
      return delegate.isRequestedSessionIdFromCookie();
   }

   public boolean isRequestedSessionIdFromURL()
   {
      return delegate.isRequestedSessionIdFromURL();
   }

   @SuppressWarnings("deprecation")
   public boolean isRequestedSessionIdFromUrl()
   {
      return delegate.isRequestedSessionIdFromUrl();
   }

   public Object getAttribute(String s)
   {
      return delegate.getAttribute(s);
   }

   public Enumeration<String> getAttributeNames()
   {
      return delegate.getAttributeNames();
   }

   public String getCharacterEncoding()
   {
      return delegate.getCharacterEncoding();
   }

   public void setCharacterEncoding(String s)
           throws UnsupportedEncodingException
   {
      delegate.setCharacterEncoding(s);
   }

   public int getContentLength()
   {
      return delegate.getContentLength();
   }

   public String getContentType()
   {
      return delegate.getContentType();
   }

   public ServletInputStream getInputStream()
           throws IOException
   {
      return delegate.getInputStream();
   }

   public String getParameter(String s)
   {
      return delegate.getParameter(s);
   }

   public Enumeration<String> getParameterNames()
   {
      return delegate.getParameterNames();
   }

   public String[] getParameterValues(String s)
   {
      return delegate.getParameterValues(s);
   }

   public Map<String, String[]> getParameterMap()
   {
      return delegate.getParameterMap();
   }

   public String getProtocol()
   {
      return delegate.getProtocol();
   }

   public String getScheme()
   {
      return delegate.getScheme();
   }

   public String getServerName()
   {
      return delegate.getServerName();
   }

   public int getServerPort()
   {
      return delegate.getServerPort();
   }

   public BufferedReader getReader()
           throws IOException
   {
      return delegate.getReader();
   }

   public String getRemoteAddr()
   {
      return delegate.getRemoteAddr();
   }

   public String getRemoteHost()
   {
      return delegate.getRemoteHost();
   }

   public void setAttribute(String s, Object o)
   {
      delegate.setAttribute(s, o);
   }

   public void removeAttribute(String s)
   {
      delegate.removeAttribute(s);
   }

   public Locale getLocale()
   {
      return delegate.getLocale();
   }

   public Enumeration<Locale> getLocales()
   {
      return delegate.getLocales();
   }

   public boolean isSecure()
   {
      return delegate.isSecure();
   }

   public RequestDispatcher getRequestDispatcher(String s)
   {
      return delegate.getRequestDispatcher(s);
   }

   @SuppressWarnings("deprecation")
   public String getRealPath(String s)
   {
      return delegate.getRealPath(s);
   }

   public int getRemotePort()
   {
      return delegate.getRemotePort();
   }

   public String getLocalName()
   {
      return delegate.getLocalName();
   }

   public String getLocalAddr()
   {
      return delegate.getLocalAddr();
   }

   @Override
   public long getContentLengthLong()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public ServletContext getServletContext()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public AsyncContext startAsync() throws IllegalStateException
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
         throws IllegalStateException
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean isAsyncStarted()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean isAsyncSupported()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public AsyncContext getAsyncContext()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public DispatcherType getDispatcherType()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String changeSessionId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean authenticate(HttpServletResponse response) throws IOException, ServletException
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void login(String username, String password) throws ServletException
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void logout() throws ServletException
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public Collection<Part> getParts() throws IOException, ServletException
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Part getPart(String name) throws IOException, ServletException
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException
   {
      // TODO Auto-generated method stub
      return null;
   }
   
}
