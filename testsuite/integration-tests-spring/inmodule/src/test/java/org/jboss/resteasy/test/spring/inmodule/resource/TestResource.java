package org.jboss.resteasy.test.spring.inmodule.resource;

import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/")
public class TestResource {

   public static final String TEST_PATH = "test";

   public static final String TEST_RESPONSE = "test passed";

   public static final String LOAD_CLASS_PATH = "loadClass";

   public static final String CLASSNAME_PARAM = "className";

   @GET
   @Path(TEST_PATH)
   @Produces("text/plain")
   public String getBasic() {
      return TEST_RESPONSE;
   }

   @GET
   @Path(TestResource.LOAD_CLASS_PATH)
   @Produces("text/plain")
   public String loadClass(@QueryParam(CLASSNAME_PARAM) String className) {
      try {
         return getClass().getClassLoader().loadClass(className).getName();
      } catch (ClassNotFoundException e) {
         return null;
      }
   }
}
