package org.jboss.resteasy.core;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

/**
 * ResteasyHttpServletRequestWrapper is introduced to support the use of
 * RequestDispatcher.forward() and RequestDispatcher.include(), which need
 * to be able to retrieve the orginal HttpServletRequest.
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Apr 18, 2014
 */
public class ResteasyHttpServletRequestWrapper extends ServletRequestWrapper implements HttpServletRequest
{
   private HttpServletRequest request;
   private HttpServletRequest proxy;
   
   public ResteasyHttpServletRequestWrapper(HttpServletRequest request, HttpServletRequest proxy)
   {
      super(proxy);
      this.request = request;
      this.proxy = proxy;
   }
   
   public HttpServletRequest getHttpServletRequest()
   {
      return request;
   }
   
   @Override
   public ServletRequest getRequest()
   {
      return request;
   }

   @Override
   public String getAuthType()
   {
      return proxy.getAuthType();
   }

   @Override
   public Cookie[] getCookies()
   {
      return proxy.getCookies();
   }

   @Override
   public long getDateHeader(String name)
   {
      return proxy.getDateHeader(name);
   }

   @Override
   public String getHeader(String name)
   {
      return proxy.getHeader(name);
   }

   @SuppressWarnings("unchecked")
   @Override
   public Enumeration<String> getHeaders(String name)
   {
      return proxy.getHeaders(name);
   }

   @SuppressWarnings("unchecked")
   @Override
   public Enumeration<String> getHeaderNames()
   {
      return proxy.getHeaderNames();
   }

   @Override
   public int getIntHeader(String name)
   {
      return proxy.getIntHeader(name);
   }

   @Override
   public String getMethod()
   {
      return proxy.getMethod();
   }

   @Override
   public String getPathInfo()
   {
      return proxy.getPathInfo();
   }

   @Override
   public String getPathTranslated()
   {
      return proxy.getPathTranslated();
   }

   @Override
   public String getContextPath()
   {
      return proxy.getContextPath();
   }

   @Override
   public String getQueryString()
   {
      return proxy.getQueryString();
   }

   @Override
   public String getRemoteUser()
   {
      return proxy.getRemoteUser();
   }

   @Override
   public boolean isUserInRole(String role)
   {
      return proxy.isUserInRole(role);
   }

   @Override
   public Principal getUserPrincipal()
   {
      return proxy.getUserPrincipal();
   }

   @Override
   public String getRequestedSessionId()
   {
      return proxy.getRequestedSessionId();
   }

   @Override
   public String getRequestURI()
   {
      return proxy.getRequestURI();
   }

   @Override
   public StringBuffer getRequestURL()
   {
      return proxy.getRequestURL();
   }

   @Override
   public String getServletPath()
   {
      return proxy.getServletPath();
   }

   @Override
   public HttpSession getSession(boolean create)
   {
      return proxy.getSession(create);
   }

   @Override
   public HttpSession getSession()
   {
      return proxy.getSession();
   }

   @Override
   public boolean isRequestedSessionIdValid()
   {
      return proxy.isRequestedSessionIdValid();
   }

   @Override
   public boolean isRequestedSessionIdFromCookie()
   {
      return proxy.isRequestedSessionIdFromCookie();
   }

   @Override
   public boolean isRequestedSessionIdFromURL()
   {
      return proxy.isRequestedSessionIdFromURL();
   }

   /**
   *
   * @deprecated      As of Version 2.1 of the Java Servlet
   *            API, use {@link #isRequestedSessionIdFromURL}
   *            instead.
   *
   */
   @Override
   public boolean isRequestedSessionIdFromUrl()
   {
      return proxy.isRequestedSessionIdFromUrl();
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
