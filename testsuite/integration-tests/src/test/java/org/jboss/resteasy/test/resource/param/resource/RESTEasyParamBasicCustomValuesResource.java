package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.CookieParam;
import org.jboss.resteasy.annotations.jaxrs.FormParam;
import org.jboss.resteasy.annotations.jaxrs.HeaderParam;
import org.jboss.resteasy.annotations.jaxrs.MatrixParam;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/custom/values")
public class RESTEasyParamBasicCustomValuesResource
{

   protected static final Logger logger = Logger.getLogger(RESTEasyParamBasicCustomValuesResource.class.getName());

   private String cookieParam0Updated;
   @CookieParam("cookieParam1")
   private String cookieParam1Updated;
   private String cookieParam2Updated;

   private String formParam0Updated;
   @FormParam("formParam1")
   private String formParam1Updated;
   private String formParam2Updated;

   private String headerParam0Updated;
   @HeaderParam("headerParam1")
   private String headerParam1Updated;
   private String headerParam2Updated;

   private String matrixParam0Updated;
   @MatrixParam("matrixParam1")
   private String matrixParam1Updated;
   private String matrixParam2Updated;

   private String pathParam0Updated;
   @PathParam("pathParam1")
   private String pathParam1Updated;
   private String pathParam2Updated;

   private String queryParam0Updated;
   @QueryParam("queryParam1")
   private String queryParam1Updated;
   private String queryParam2Updated;


   public RESTEasyParamBasicCustomValuesResource(
         @CookieParam("cookieParam0") String cookieParam0Updated,
         @FormParam("formParam0") String formParam0Updated,
         @HeaderParam("headerParam0") String headerParam0Updated,
         @MatrixParam("matrixParam0") String matrixParam0Updated,
         @PathParam("pathParam0") String pathParam0Updated,
         @QueryParam("queryParam0") String queryParam0Updated
         )
   {
      this.cookieParam0Updated = cookieParam0Updated;
      this.formParam0Updated = formParam0Updated;
      this.headerParam0Updated = headerParam0Updated;
      this.matrixParam0Updated = matrixParam0Updated;
      this.pathParam0Updated = pathParam0Updated;
      this.queryParam0Updated = queryParam0Updated;
   }
   
   public String getCookieParam2Updated()
   {
      return cookieParam2Updated;
   }
   
   @CookieParam("cookieParam2")
   public void setCookieParam2Updated(String cookieParam2Updated)
   {
      this.cookieParam2Updated = cookieParam2Updated;
   }

   public String getFormParam2Updated()
   {
      return formParam2Updated;
   }
   
   @FormParam("formParam2")
   public void setFormParam2Updated(String formParam2Updated)
   {
      this.formParam2Updated = formParam2Updated;
   }
   
   public String getPathParam2Updated()
   {
      return pathParam2Updated;
   }
   
   public String getHeaderParam2Updated()
   {
      return headerParam2Updated;
   }
   
   @HeaderParam("headerParam2")
   public void setHeaderParam2Updated(String headerParam2Updated)
   {
      this.headerParam2Updated = headerParam2Updated;
   }
   
   public String getMatrixParam2Updated()
   {
      return matrixParam2Updated;
   }
   
   @MatrixParam("matrixParam2")
   public void setMatrixParam2Updated(String matrixParam2Updated)
   {
      this.matrixParam2Updated = matrixParam2Updated;
   }
   
   @PathParam("pathParam2")
   public void setPathParam2Updated(String pathParam2Updated)
   {
      this.pathParam2Updated = pathParam2Updated;
   }

   public String getQueryParam2Updated()
   {
      return queryParam2Updated;
   }
   
   @QueryParam("queryParam2")
   public void setQueryParam2Updated(String queryParam2Updated)
   {
      this.queryParam2Updated = queryParam2Updated;
   }

   @POST
   @Path("a/{pathParam0}/{pathParam1}/{pathParam2}/{pathParam3}")
   public Response post(
           @CookieParam("cookieParam3") String cookieParam3Updated,
           @FormParam("formParam3") String formParam3Updated,
           @HeaderParam("headerParam3") String headerParam3Updated,
           @MatrixParam("matrixParam3") String matrixParam3Updated,
           @PathParam("pathParam3") String pathParam3Updated,
           @QueryParam("queryParam3") String queryParam3Updated)
   {

      StringBuilder details = new StringBuilder();
      details.append("cookieParam0: "+cookieParam0Updated+"\n");
      details.append("cookieParam1: "+cookieParam1Updated+"\n");
      details.append("cookieParam2: "+cookieParam2Updated+"\n");
      details.append("cookieParam3: "+cookieParam3Updated+"\n");

      details.append("formParam0: "+formParam0Updated+"\n");
      details.append("formParam1: "+formParam1Updated+"\n");
      details.append("formParam2: "+formParam2Updated+"\n");
      details.append("formParam3: "+formParam3Updated+"\n");

      details.append("headerParam0: "+headerParam0Updated+"\n");
      details.append("headerParam1: "+headerParam1Updated+"\n");
      details.append("headerParam2: "+headerParam2Updated+"\n");
      details.append("headerParam3: "+headerParam3Updated+"\n");

      details.append("matrixParam0: "+matrixParam0Updated+"\n");
      details.append("matrixParam1: "+matrixParam1Updated+"\n");
      details.append("matrixParam2: "+matrixParam2Updated+"\n");
      details.append("matrixParam3: "+matrixParam3Updated+"\n");

      details.append("pathParam0: "+pathParam0Updated+"\n");
      details.append("pathParam1: "+pathParam1Updated+"\n");
      details.append("pathParam2: "+pathParam2Updated+"\n");
      details.append("pathParam3: "+pathParam3Updated+"\n");

      details.append("queryParam0: "+queryParam0Updated+"\n");
      details.append("queryParam1: "+queryParam1Updated+"\n");
      details.append("queryParam2: "+queryParam2Updated+"\n");
      details.append("queryParam3: "+queryParam3Updated+"\n");

      logger.info(details);

      if(!"cookieParam0".equals(cookieParam0Updated)
              || !"cookieParam1".equals(cookieParam1Updated)
              || !"cookieParam2".equals(cookieParam2Updated)
              || !"cookieParam3".equals(cookieParam3Updated)) {
         logger.error("cookie error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      if(!"formParam0".equals(formParam0Updated)
              || !"formParam1".equals(formParam1Updated)
              || !"formParam2".equals(formParam2Updated)
              || !"formParam3".equals(formParam3Updated)) {
         logger.error("form error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      if(!"headerParam0".equals(headerParam0Updated)
              || !"headerParam1".equals(headerParam1Updated)
              || !"headerParam2".equals(headerParam2Updated)
              || !"headerParam3".equals(headerParam3Updated)) {
         logger.error("header error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      if(!"matrixParam0".equals(matrixParam0Updated)
              || !"matrixParam1".equals(matrixParam1Updated)
              || !"matrixParam2".equals(matrixParam2Updated)
              || !"matrixParam3".equals(matrixParam3Updated)) {
         logger.error("matrix error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      if(!"pathParam0".equals(pathParam0Updated)
              || !"pathParam1".equals(pathParam1Updated)
              || !"pathParam2".equals(pathParam2Updated)
              || !"pathParam3".equals(pathParam3Updated)) {
         logger.error("path error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      if(!"queryParam0".equals(queryParam0Updated)
              || !"queryParam1".equals(queryParam1Updated)
              || !"queryParam2".equals(queryParam2Updated)
              || !"queryParam3".equals(queryParam3Updated)) {
         logger.error("query error");
         return Response.status(Response.Status.BAD_REQUEST).build();
      }

      return Response.ok().build();
   }
}
