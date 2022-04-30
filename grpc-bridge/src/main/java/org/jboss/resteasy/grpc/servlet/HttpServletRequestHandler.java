package org.jboss.resteasy.grpc.servlet;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpServletRequestHandler implements InvocationHandler {
   private String contextPath;
   private String path;
   private InputStream is;
   private Map<String, Object> attributes = new HashMap<String, Object>();
   private Map<String, String> headers = new HashMap<String, String>();   

   public HttpServletRequestHandler(String contextPath, String path, String method, InputStream message, Map<String, String> headers) {
      this.contextPath = contextPath;
      this.path = path;
      this.is = message;
      this.headers = headers;
      headers.put("Accept", "application/grpc-jaxrs");
      headers.put("Content-Type", "application/grpc-jaxrs");
   }

   @Override
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if ("getContextPath".equals(method.getName())) {
         return contextPath;
      }
      if ("getMethod".equals(method.getName())) {
         return method;
      }
      if ("getHeaderNames".equals(method.getName())) {
         return Collections.enumeration(headers.keySet());
      }
      if ("getHeader".equals(method.getName())) {
         return headers.get(args[0]);
      }
      if ("getHeaders".equals(method.getName())) {
         return Collections.enumeration(Arrays.asList(headers.get(args[0])));
      }
      if ("getContentType".equals(method.getName())) {
         return "application/grpc-jaxrs";
      }
      if ("getRequestURL".equals(method.getName())) {
         return new StringBuffer("http://localhost:8081/" + path);
      }
      if ("getInputStream".equals(method.getName())) {
         return is;
      }
      if ("isAsyncStarted".contentEquals(method.getName())) {
         return false;
      }
      if ("setAttribute".equals(method.getName())) {
         attributes.put((String) args[0], args[1]);
         return null;
      }
      if ("getAttribute".equals(method.getName())) {
         return attributes.get(args[0]);
      }
      //public abstract java.lang.String javax.servlet.http.HttpServletRequest.getContextPath()
      return null;
   }
}
