package org.jboss.resteasy.test.injection.resource;

import javax.annotation.PostConstruct;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/ejb")
public class PostConstructInjectionEJBInterceptorResource {

   @Size(max=3)
   private String s = "ab";

   @Path("get")
   @GET
   public String get() {
      return s;
   }

   @AroundInvoke
   @PostConstruct
   public Object postConstruct(InvocationContext ctx) throws Exception {
      s = "abcd";
      return ctx.proceed();
   }
}