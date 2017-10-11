package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

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
