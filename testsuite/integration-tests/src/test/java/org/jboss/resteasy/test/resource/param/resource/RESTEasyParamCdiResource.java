package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.CookieParam;
import org.jboss.resteasy.annotations.jaxrs.FormParam;
import org.jboss.resteasy.annotations.jaxrs.HeaderParam;
import org.jboss.resteasy.annotations.jaxrs.MatrixParam;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
@RequestScoped
public class RESTEasyParamCdiResource
{
   private static Logger logger = Logger.getLogger(RESTEasyParamCdiResource.class);

   @CookieParam
   private String cookieParam1;
   private String cookieParam2;

   @FormParam
   private String formParam1;
   private String formParam2;

   @HeaderParam
   private String headerParam1;
   private String headerParam2;

   @MatrixParam
   private String matrixParam1;
   private String matrixParam2;

   @PathParam
   private String pathParam1;
   private String pathParam2;

   @QueryParam
   private String queryParam1;
   private String queryParam2;

   public RESTEasyParamCdiResource() {

   }
   
   public String getCookieParam2()
   {
      return cookieParam2;
   }
   
   @CookieParam
   public void setCookieParam2(String cookieParam2)
   {
      this.cookieParam2 = cookieParam2;
   }

   public String getFormParam2()
   {
      return formParam2;
   }
   
   @FormParam
   public void setFormParam2(String formParam2)
   {
      this.formParam2 = formParam2;
   }
   
   public String getPathParam2()
   {
      return pathParam2;
   }
   
   public String getHeaderParam2()
   {
      return headerParam2;
   }
   
   @HeaderParam
   public void setHeaderParam2(String headerParam2)
   {
      this.headerParam2 = headerParam2;
   }
   
   public String getMatrixParam2()
   {
      return matrixParam2;
   }
   
   @MatrixParam
   public void setMatrixParam2(String matrixParam2)
   {
      this.matrixParam2 = matrixParam2;
   }
   
   @PathParam
   public void setPathParam2(String pathParam2)
   {
      this.pathParam2 = pathParam2;
   }

   public String getQueryParam2()
   {
      return queryParam2;
   }
   
   @QueryParam
   public void setQueryParam2(String queryParam2)
   {
      this.queryParam2 = queryParam2;
   }

   @POST
   @Path("{pathParam0}/{pathParam1}/{pathParam2}/{pathParam3}")
   public Response post2(
           @CookieParam String cookieParam3,
           @FormParam String formParam3,
           @HeaderParam String headerParam3,
           @MatrixParam String matrixParam3,
           @PathParam String pathParam3,
           @QueryParam String queryParam3)
   {
      StringBuilder details = new StringBuilder();
      details.append("cookieParam1: "+cookieParam1+"\n");
      details.append("cookieParam2: "+cookieParam2+"\n");
      details.append("cookieParam3: "+cookieParam3+"\n");

      details.append("formParam1: "+formParam1+"\n");
      details.append("formParam2: "+formParam2+"\n");
      details.append("formParam3: "+formParam3+"\n");

      details.append("headerParam1: "+headerParam1+"\n");
      details.append("headerParam2: "+headerParam2+"\n");
      details.append("headerParam3: "+headerParam3+"\n");

      details.append("matrixParam1: "+matrixParam1+"\n");
      details.append("matrixParam2: "+matrixParam2+"\n");
      details.append("matrixParam3: "+matrixParam3+"\n");

      details.append("pathParam1: "+pathParam1+"\n");
      details.append("pathParam2: "+pathParam2+"\n");
      details.append("pathParam3: "+pathParam3+"\n");

      details.append("queryParam1: "+queryParam1+"\n");
      details.append("queryParam2: "+queryParam2+"\n");
      details.append("queryParam3: "+queryParam3+"\n");

      logger.info(details);

      if(     !queryParam3.equals(cookieParam1)
              || !queryParam3.equals(cookieParam2)
              || !queryParam3.equals(cookieParam3)) {
         logger.error("cookie error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      if(!queryParam3.equals(formParam1)
              || !queryParam3.equals(formParam2)
              || !queryParam3.equals(formParam3)) {
         logger.error("form error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      if(!queryParam3.equals(headerParam1)
              || !queryParam3.equals(headerParam2)
              || !queryParam3.equals(headerParam3)) {
         logger.error("header error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      if(!queryParam3.equals(matrixParam1)
              || !queryParam3.equals(matrixParam2)
              || !queryParam3.equals(matrixParam3)) {
         logger.error("matrix error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      if(!queryParam3.equals(pathParam1)
              || !queryParam3.equals(pathParam2)
              || !queryParam3.equals(pathParam3)) {
         logger.error("path error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      if(!queryParam3.equals(queryParam1)
              || !queryParam3.equals(queryParam2)
              || !queryParam3.equals(queryParam3)) {
         logger.error("query error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      return Response.ok(queryParam1).build();
   }
}
