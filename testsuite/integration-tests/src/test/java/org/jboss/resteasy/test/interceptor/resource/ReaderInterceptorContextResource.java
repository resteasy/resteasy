package org.jboss.resteasy.test.interceptor.resource;

import java.util.List;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

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
