package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.logging.Logger;
import javax.ws.rs.core.Response;

public class RESTEasyParamBasicProxyResource implements RESTEasyParamBasicProxy
{

   protected static final Logger logger = Logger.getLogger(RESTEasyParamBasicProxyResource.class.getName());

   public Response post(
           String cookieParam3,
           String formParam3,
           String headerParam3,
           String matrixParam3,
           String pathParam3,
           String queryParam3)
   {

      StringBuilder details = new StringBuilder();
      details.append("cookieParam3: "+cookieParam3+"\n");
      details.append("formParam3: "+formParam3+"\n");
      details.append("headerParam3: "+headerParam3+"\n");
      details.append("matrixParam3: "+matrixParam3+"\n");
      details.append("pathParam3: "+pathParam3+"\n");
      details.append("queryParam3: "+queryParam3+"\n");

      logger.info(details);

      if(!"cookieParam3".equals(cookieParam3)) {
         logger.error("cookie error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      if(!"formParam3".equals(formParam3)) {
         logger.error("form error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      if(!"headerParam3".equals(headerParam3)) {
         logger.error("header error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      if(!"matrixParam3".equals(matrixParam3)) {
         logger.error("matrix error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      if(!"pathParam3".equals(pathParam3)) {
         logger.error("path error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      if(!"queryParam3".equals(queryParam3)) {
         logger.error("query error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      return Response.ok().build();
   }
}
