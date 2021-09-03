package org.jboss.resteasy.test.interceptor.resource;

import java.util.List;

import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("")
public class ReaderInterceptorContextResource {

   @POST
   @Path("post")
   public String n(String dummy, @HeaderParam("header") List<String> list) {
      StringBuffer sb = new StringBuffer();
      for (String s : list) {
         sb.append(s);
      }
      return sb.toString();
   }
}
