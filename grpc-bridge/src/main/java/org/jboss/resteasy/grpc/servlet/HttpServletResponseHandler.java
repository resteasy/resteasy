package org.jboss.resteasy.grpc.servlet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

public class HttpServletResponseHandler implements InvocationHandler {

   public static final String GRPC_RETURN_RESPONSE = "grpc-return-response";
   public static final String GRPC_ASYNC = "grpc-async";

   private AsyncMockServletOutputStream msos = new AsyncMockServletOutputStream();
   private MultivaluedMap<String, String> headers = new MultivaluedHashMap<String, String>();

   public HttpServletResponseHandler(String retn, String async) {
      if ("com.google.protobuf.Any".equals(retn)) {
         List<String> list = new ArrayList<String>();
         list.add("true");
         headers.put(GRPC_RETURN_RESPONSE, list);
      }
      if ("async".equals(async)) {
         List<String> list = new ArrayList<String>();
         list.add("true");
         headers.put(GRPC_ASYNC, list);
      }
   }

   @Override
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if ("isCommitted".equals(method.getName())) {
         return true;
      }
      if ("getOutputStream".equals(method.getName())) {
         return msos;
      }
      if ("getHeaders".equals(method.getName())) {
         return headers.get(args[0]);
      }
      if ("getHeader".equals(method.getName())) {
         List<String> list = headers.get(args[0]);
         if (list == null || list.size() == 0) {
            return null;
         }
         return list.get(0);
      }
      return null;
   }
}
