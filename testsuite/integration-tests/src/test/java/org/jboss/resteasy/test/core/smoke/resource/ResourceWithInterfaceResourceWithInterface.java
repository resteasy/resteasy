package org.jboss.resteasy.test.core.smoke.resource;


import org.jboss.logging.Logger;

import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

public class ResourceWithInterfaceResourceWithInterface implements ResourceWithInterfaceSimpleClient {
   private static Logger logger = Logger.getLogger(ResourceWithInterfaceResourceWithInterface.class);

   public String getWild() {
      return "Wild";
   }

   public String getBasic() {
      logger.info("getBasic()");
      return "basic";
   }

   public void putBasic(String body) {
      logger.info(body);
   }

   public String getQueryParam(@QueryParam("param") String param) {
      return param;
   }

   public String getMatrixParam(@MatrixParam("param") String param) {
      return param;
   }

   public int getUriParam(@PathParam("param") int param) {
      return param;
   }


}
