package org.jboss.resteasy.test.injection.resource;

import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/ejb")
@Stateless
@Interceptors({PostConstructInjectionEJBInterceptor.class})
public class PostConstructInjectionEJBResource {

   @Size(max=3)
   private String t = "ab";

   @Path("get")
   @GET
   public String get() {
      return t;
   }

   public void setT(String t) {
      this.t = t;
   }
}