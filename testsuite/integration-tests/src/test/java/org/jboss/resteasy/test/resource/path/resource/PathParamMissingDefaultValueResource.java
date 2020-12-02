package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("resource")
public class PathParamMissingDefaultValueResource {

   @BeanParam
   PathParamMissingDefaultValueBeanParamEntity beanParam;

   @GET
   @Path("test")
   public String defaultParams() {
      return beanParam.toString();
   }
}
