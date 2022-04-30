package org.jboss.resteasy.grpc.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestWrapper;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletResponseWrapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.grpc.i18n.Messages;
import org.jboss.resteasy.grpc.util.DateUtils;
import org.jboss.resteasy.grpc.util.IteratorEnumeration;
import org.jboss.resteasy.grpc.util.LocaleUtils;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;

public class HttpServletRequestImpl implements HttpServletRequest {

   public static final String GRPC_RETURN_RESPONSE = "grpc-return-response";

   private ServletResponse servletResponse;
   private String contextPath;
   private UriInfo uriInfo;
   private String method;
   private ServletInputStream sis;
   private Map<String, List<String>> headers;
   private Cookie[] cookies;
   private ServletContext servletContext;
   private volatile boolean asyncStarted;
   private volatile AsyncContext asyncContext;
   private boolean gotInputStream = false;
   private boolean gotReader = false;
   
   // servlet info
   private String characterEncoding;
   private String clientAddr;
   private String clientHost;
   private int    clientPort;
   private String contentType;
   
   private Map<String, Object> attributes = new HashMap<String, Object>();
   private Map<String, String[]> parameters = new HashMap<String, String[]>();

   public HttpServletRequestImpl(ServletResponse servletResponse, ServletContext servletContext, String contextPath,
         String uri, String method, ServletInputStream sis, String retn, Map<String, List<String>> headers,
         Cookie[] cookies) throws URISyntaxException {
      this.servletResponse = servletResponse;
      this.servletContext = servletContext;
      this.contextPath = contextPath;
      this.uriInfo = new ResteasyUriInfo(uri, contextPath);
      this.method = method;
      this.sis = sis;
      this.headers = headers;
      List<String> acceptList = new ArrayList<String>();
      acceptList.add("application/grpc-jaxrs");
      acceptList.add("*/*;grpc-jaxrs=true");
      headers.put("Accept", acceptList);
      List<String> contentTypeList = new ArrayList<String>();
      contentTypeList.add("*/*;grpc-jaxrs=true");
      headers.put("Content-Type", contentTypeList);
      //      headers.get("Accept").add("application/grpc-jaxrs");
      //      headers.get("Content-Type").add("application/grpc-jaxrs");
      if ("com.google.protobuf.Any".equals(retn)) {
         acceptList = new ArrayList<String>();
         acceptList.add("true");
         headers.put(GRPC_RETURN_RESPONSE, acceptList);
      }
      this.cookies = cookies;
   }

   @Override
   public Object getAttribute(String name) {
      return attributes.get(name);
   }

   @Override
   public Enumeration<String> getAttributeNames() {
      return Collections.enumeration(attributes.keySet());
   }

   @Override
   public String getCharacterEncoding() {
      return characterEncoding;
   }

   @Override
   public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
      this.characterEncoding = env;
   }

   @Override
   public int getContentLength() {
      return -1;
   }

   @Override
   public long getContentLengthLong() {
      return -1;
   }

   @Override
   public String getContentType() {
      return "*/*;grpc-jaxrs=true";
   }

   @Override
   public ServletInputStream getInputStream() throws IOException {
      if (gotReader) {
         throw new IllegalStateException("Reader already returned");
      }
      gotInputStream = true;
      return sis;
   }

   @Override
   public String getParameter(String name) {
      if (parameters.containsKey(name)) {
         return parameters.get(name)[0];
      }
      return null;
   }

   @Override
   public Enumeration<String> getParameterNames() {
      return Collections.enumeration(parameters.keySet());
   }

   @Override
   public String[] getParameterValues(String name) {
      return parameters.values().toArray(new String[parameters.size()]);
   }

   @Override
   public Map<String, String[]> getParameterMap() {
      return parameters;
   }

   @Override
   public String getProtocol() {
      return "HTTP/2.0";
   }

   @Override
   public String getScheme() {
      return uriInfo.getBaseUri().getScheme();
   }

   @Override
   public String getServerName() {
      return uriInfo.getBaseUri().getHost();
   }

   @Override
   public int getServerPort() {
      return uriInfo.getBaseUri().getPort();
   }

   @Override
   public BufferedReader getReader() throws IOException {
      if (gotInputStream) {
         throw new IllegalStateException("InputStream already returned");
      }
      gotReader = true;
      return new BufferedReader(new InputStreamReader(this.sis));
   }

   @Override
   public String getRemoteAddr() {
      return clientAddr;
   }
   
   public void setRemoteAddr(String addr) {
      clientAddr = addr;
   }

   @Override
   public String getRemoteHost() {
      return clientHost;
   }
   
   public void setRemoteHost(String host) {
      clientHost = host;
   }

   @Override
   public void setAttribute(String name, Object o) {
      attributes.put(name, o);
   }

   @Override
   public void removeAttribute(String name) {
      attributes.remove(name);
   }

   @Override
   public Locale getLocale() {
       return getLocales().nextElement();
   }

   @Override
   public Enumeration<Locale> getLocales() {
       final List<String> acceptLanguage = headers.get(HttpHeaders.ACCEPT_LANGUAGE);
       List<Locale> ret = LocaleUtils.getLocalesFromHeader(acceptLanguage);
       if(ret.isEmpty()) {
           return new IteratorEnumeration<>(Collections.singletonList(Locale.getDefault()).iterator());
       }
       return new IteratorEnumeration<>(ret.iterator());
   }

   @Override
   public boolean isSecure() {
      throw new ProcessingException("isSecure() is not implemented");
   }

   @Override
   public RequestDispatcher getRequestDispatcher(String path) {
       if (path == null) {
           return null;
       }
       String realPath;
       if (path.startsWith("/")) {
           realPath = path;
       } else {
           String current = uriInfo.relativize(uriInfo.getBaseUri()).toString();
           int lastSlash = current.lastIndexOf("/");
           if (lastSlash != -1) {
               current = current.substring(0, lastSlash + 1);
           }
           realPath = current + path;
       }
       return servletContext.getRequestDispatcher(realPath);
   }

   @Override
   public String getRealPath(String path) {
       return servletContext.getRealPath(path);
   }

   @Override
   public int getRemotePort() {
      return clientPort;
   }

   public void setRemotePort(int port) {
      clientPort = port;
   }
   
   @Override
   public String getLocalName() {
      throw new ProcessingException("getLocalName() is not implemented");
   }

   @Override
   public String getLocalAddr() {
      throw new ProcessingException("getLocalAddr() is not implemented");
   }

   @Override
   public int getLocalPort() {
      throw new ProcessingException("getLocalPort() is not implemented");
   }

   @Override
   public ServletContext getServletContext() {
      return servletContext;
   }

   @Override
   public AsyncContext startAsync() throws IllegalStateException {
      if (asyncStarted) {
         throw Messages.MESSAGES.asyncProcessingAlreadyStarted();
      }
      asyncStarted = true;
      return asyncContext = new AsyncContextImpl(this, servletResponse);
   }

   @Override
   public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
         throws IllegalStateException {
      if (this != servletRequest) {
         if (!(servletRequest instanceof ServletRequestWrapper)) {
            throw Messages.MESSAGES.requestWasNotOriginalOrWrapper(servletRequest);
         }
      }
      if (this.servletResponse != servletResponse) {
         if (!(servletResponse instanceof ServletResponseWrapper)) {
            throw Messages.MESSAGES.responseWasNotOriginalOrWrapper(servletResponse);
         }
      }
      if (asyncStarted) {
         throw Messages.MESSAGES.asyncAlreadyStarted();
      }
      return asyncContext = new AsyncContextImpl(servletRequest, servletResponse);
   }

   @Override
   public boolean isAsyncStarted() {
      return asyncStarted;
   }

   @Override
   public boolean isAsyncSupported() {
      return true;
   }

   @Override
   public AsyncContext getAsyncContext() {
      if (!isAsyncStarted()) {
         throw Messages.MESSAGES.asyncNotStarted();
      }
      return asyncContext;
   }

   @Override
   public DispatcherType getDispatcherType() {
      return DispatcherType.REQUEST;
   }

   @Override
   public String getAuthType() {
      throw new ProcessingException("getAuthType() is not implemented");
   }

   @Override
   public Cookie[] getCookies() {
      return cookies;
   }

   @Override
   public long getDateHeader(String name) {
       String header = headers.get(name).get(0);
       if (header == null) {
           return -1;
       }
       Date date = DateUtils.parseDate(header);
       if (date == null) {
           throw Messages.MESSAGES.headerCannotBeConvertedToDate(header);
       }
       return date.getTime();
   }

   @Override
   public String getHeader(String name) {
      List<String> list = headers.get(name);
      if (list == null) {
         return null;
      }
      return list.get(0);
   }

   @Override
   public Enumeration<String> getHeaders(String name) {
      return Collections.enumeration(headers.get(name));
   }

   @Override
   public Enumeration<String> getHeaderNames() {
      return Collections.enumeration(headers.keySet());
   }

   @Override
   public int getIntHeader(String name) {
      String header = getHeader(name);
      if (header == null) {
         return -1;
      }
      return Integer.parseInt(header);
   }

   @Override
   public String getMethod() {
      return method;
   }

   @Override
   public String getPathInfo() {
      throw new ProcessingException("getPathInfo() is not implemented");
   }

   @Override
   public String getPathTranslated() {
      throw new ProcessingException("getPathTranslated() is not implemented");
   }

   @Override
   public String getContextPath() {
      return contextPath;
   }

   @Override
   public String getQueryString() {
      return uriInfo.getRequestUri().getQuery();
   }

   @Override
   public String getRemoteUser() {
      throw new ProcessingException("getRemoteUser() is not implemented");
   }

   @Override
   public boolean isUserInRole(String role) {
      throw new ProcessingException("isUserInRole() is not implemented");
   }

   @Override
   public Principal getUserPrincipal() {
      throw new ProcessingException("getUserPrincipal() is not implemented");
   }

   @Override
   public String getRequestedSessionId() {
      throw new ProcessingException("getRequestedSessionId() is not implemented");
   }

   @Override
   public String getRequestURI() {
      return uriInfo.getAbsolutePath().toString();
   }

   @Override
   public StringBuffer getRequestURL() {
      return new StringBuffer(uriInfo.getAbsolutePath().toString());
   }

   @Override
   public String getServletPath() {
      throw new ProcessingException("getServletPath() is not implemented");
   }

   @Override
   public HttpSession getSession(boolean create) {
      throw new ProcessingException("getSession() is not implemented");
   }

   @Override
   public HttpSession getSession() {
      throw new ProcessingException("getSession() is not implemented");
   }

   @Override
   public String changeSessionId() {
      throw new ProcessingException("changeSessionId() is not implemented");
   }

   @Override
   public boolean isRequestedSessionIdValid() {
      throw new ProcessingException("isRequestedSessionIdValid() is not implemented");
   }

   @Override
   public boolean isRequestedSessionIdFromCookie() {
      throw new ProcessingException("isRequestedSessionIdFromCookie() is not implemented");
   }

   @Override
   public boolean isRequestedSessionIdFromURL() {
      throw new ProcessingException("isRequestedSessionIdFromURL() is not implemented");
   }

   @Override
   public boolean isRequestedSessionIdFromUrl() {
      throw new ProcessingException("isRequestedSessionIdFromUrl() is not implemented");
   }

   @Override
   public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
      throw new ProcessingException("authenticate() is not implemented");
   }

   @Override
   public void login(String username, String password) throws ServletException {
      throw new ProcessingException("login() is not implemented");
   }

   @Override
   public void logout() throws ServletException {
      throw new ProcessingException("logout() is not implemented");
   }

   @Override
   public Collection<Part> getParts() throws IOException, ServletException {
      throw new ProcessingException("getParts() is not implemented");
   }

   @Override
   public Part getPart(String name) throws IOException, ServletException {
      throw new ProcessingException("getPart() is not implemented");
   }

   @Override
   public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
      throw new ProcessingException("upgrade() is not implemented");
   }

}
