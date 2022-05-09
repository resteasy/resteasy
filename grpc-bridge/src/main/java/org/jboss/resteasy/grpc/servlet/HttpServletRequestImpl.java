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

import org.jboss.resteasy.grpc.i18n.Messages;
import org.jboss.resteasy.grpc.util.DateUtils;
import org.jboss.resteasy.grpc.util.IteratorEnumeration;
import org.jboss.resteasy.grpc.util.LocaleUtils;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;

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
import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;

public class HttpServletRequestImpl implements HttpServletRequest {

   public static final String GRPC_RETURN_RESPONSE = "grpc-return-response";

   private ServletResponse servletResponse;
   private String uri;
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

   private Map<String, Object> attributes = new HashMap<String, Object>();
   private Map<String, String[]> parameters = new HashMap<String, String[]>();

   public HttpServletRequestImpl(final ServletResponse servletResponse, final ServletContext servletContext,
         final String uri, final String method, final ServletInputStream sis, final String retn, final Map<String, List<String>> headers,
         final Cookie[] cookies) throws URISyntaxException {
      this.servletResponse = servletResponse;
      this.servletContext = servletContext;
      this.uri = uri;
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
      return getUriInfo().getBaseUri().getScheme();
   }

   @Override
   public String getServerName() {
      return getUriInfo().getBaseUri().getHost();
   }

   @Override
   public int getServerPort() {
      return getUriInfo().getBaseUri().getPort();
   }

   @Override
   public BufferedReader getReader() throws IOException {
      if (gotInputStream) {
         throw new IllegalStateException(Messages.MESSAGES.inputStreamAlreadyReturned());
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
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("isSecure()"));
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
         String current = getUriInfo().relativize(getUriInfo().getBaseUri()).toString();
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
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("getLocalName()"));
   }

   @Override
   public String getLocalAddr() {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("getLocalAddr()"));
   }

   @Override
   public int getLocalPort() {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("getLocalPort()"));
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
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("getAuthType()"));
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
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("getPathInfo()"));
   }

   @Override
   public String getPathTranslated() {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("getPathTranslated()"));
   }

   @Override
   public String getContextPath() {
      return servletContext.getContextPath();
   }

   @Override
   public String getQueryString() {
      return getUriInfo().getRequestUri().getQuery();
   }

   @Override
   public String getRemoteUser() {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("getRemoteUser()"));
   }

   @Override
   public boolean isUserInRole(String role) {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("isUserInRole()"));
   }

   @Override
   public Principal getUserPrincipal() {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("getUserPrincipal()"));
   }

   @Override
   public String getRequestedSessionId() {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("getRequestedSessionId()"));
   }

   @Override
   public String getRequestURI() {
      return getUriInfo().getAbsolutePath().toString();
   }

   @Override
   public StringBuffer getRequestURL() {
      return new StringBuffer(getUriInfo().getAbsolutePath().toString());
   }

   @Override
   public String getServletPath() {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("getServletPath()"));
   }

   @Override
   public HttpSession getSession(boolean create) {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("getSession()"));
   }

   @Override
   public HttpSession getSession() {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("getSession()"));
   }

   @Override
   public String changeSessionId() {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("changeSessionId()"));
   }

   @Override
   public boolean isRequestedSessionIdValid() {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("isRequestedSessionIdValid()"));
   }

   @Override
   public boolean isRequestedSessionIdFromCookie() {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("isRequestedSessionIdFromCookie()"));
   }

   @Override
   public boolean isRequestedSessionIdFromURL() {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("isRequestedSessionIdFromURL()"));
   }

   @Override
   public boolean isRequestedSessionIdFromUrl() {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("isRequestedSessionIdFromUrl()"));
   }

   @Override
   public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("authenticate()"));
   }

   @Override
   public void login(String username, String password) throws ServletException {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("login()"));
   }

   @Override
   public void logout() throws ServletException {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("logout()"));
   }

   @Override
   public Collection<Part> getParts() throws IOException, ServletException {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("getParts()"));
   }

   @Override
   public Part getPart(String name) throws IOException, ServletException {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("getPart()"));
   }

   @Override
   public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
      throw new NotSupportedException(Messages.MESSAGES.isNotImplemented("upgrade()"));
   }

   private UriInfo getUriInfo() {
      if (uriInfo == null) {
         synchronized (this) {
            if (uriInfo == null) {
               uriInfo = new ResteasyUriInfo(uri, servletContext.getContextPath());
            }
         }
      }
      return uriInfo;
   }
}
