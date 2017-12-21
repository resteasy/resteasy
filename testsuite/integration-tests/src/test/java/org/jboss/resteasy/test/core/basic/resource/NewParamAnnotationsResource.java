package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.NewCookieParam;
import org.jboss.resteasy.annotations.NewFormParam;
import org.jboss.resteasy.annotations.NewHeaderParam;
import org.jboss.resteasy.annotations.NewMatrixParam;
import org.jboss.resteasy.annotations.NewPathParam;
import org.jboss.resteasy.annotations.NewQueryParam;

@Path("/")
public class NewParamAnnotationsResource
{

   private String cookieParam0;
   @NewCookieParam
   private String cookieParam1;
   private String cookieParam2;

   private String formParam0;
   @NewFormParam
   private String formParam1;
   private String formParam2;

   private String headerParam0;
   @NewHeaderParam
   private String headerParam1;
   private String headerParam2;

   private String matrixParam0;
   @NewMatrixParam
   private String matrixParam1;
   private String matrixParam2;

   private String pathParam0;
   @NewPathParam
   private String pathParam1;
   private String pathParam2;

   private String queryParam0;
   @NewQueryParam
   private String queryParam1;
   private String queryParam2;


   public NewParamAnnotationsResource(
         @NewCookieParam String cookieParam0,
         @NewFormParam String formParam0,
         @NewHeaderParam String headerParam0,
         @NewMatrixParam String matrixParam0,
         @NewPathParam String pathParam0,
         @NewQueryParam String queryParam0
         )
   {
      this.cookieParam0 = cookieParam0;
      this.formParam0 = formParam0;
      this.headerParam0 = headerParam0;
      this.matrixParam0 = matrixParam0;
      this.pathParam0 = pathParam0;
      this.queryParam0 = queryParam0;
   }
   
   public String getCookieParam2()
   {
      return cookieParam2;
   }
   
   @NewCookieParam
   public void setCookieParam2(String cookieParam2)
   {
      this.cookieParam2 = cookieParam2;
   }

   public String getFormParam2()
   {
      return formParam2;
   }
   
   @NewFormParam
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
   
   @NewHeaderParam
   public void setHeaderParam2(String headerParam2)
   {
      this.headerParam2 = headerParam2;
   }
   
   public String getMatrixParam2()
   {
      return matrixParam2;
   }
   
   @NewMatrixParam
   public void setMatrixParam2(String matrixParam2)
   {
      this.matrixParam2 = matrixParam2;
   }
   
   @NewPathParam
   public void setPathParam2(String pathParam2)
   {
      this.pathParam2 = pathParam2;
   }

   public String getQueryParam2()
   {
      return queryParam2;
   }
   
   @NewQueryParam
   public void setQueryParam2(String queryParam2)
   {
      this.queryParam2 = queryParam2;
   }
   
   @POST
   @Path("{pathParam0}/{pathParam1}/{pathParam2}/{pathParam3}")
   public Response post(
         @NewCookieParam String cookieParam3,
         @NewFormParam String formParam3,
         @NewHeaderParam String headerParam3,
         @NewMatrixParam String matrixParam3,
         @NewPathParam String pathParam3, 
         @NewQueryParam String queryParam3)
   {
      System.err.println("cookieParam0: "+cookieParam0);
      System.err.println("cookieParam1: "+cookieParam1);
      System.err.println("cookieParam2: "+cookieParam2);
      System.err.println("cookieParam3: "+cookieParam3);

      System.err.println("formParam0: "+formParam0);
      System.err.println("formParam1: "+formParam1);
      System.err.println("formParam2: "+formParam2);
      System.err.println("formParam3: "+formParam3);

      System.err.println("headerParam0: "+headerParam0);
      System.err.println("headerParam1: "+headerParam1);
      System.err.println("headerParam2: "+headerParam2);
      System.err.println("headerParam3: "+headerParam3);

      System.err.println("matrixParam0: "+matrixParam0);
      System.err.println("matrixParam1: "+matrixParam1);
      System.err.println("matrixParam2: "+matrixParam2);
      System.err.println("matrixParam3: "+matrixParam3);

      System.err.println("pathParam0: "+pathParam0);
      System.err.println("pathParam1: "+pathParam1);
      System.err.println("pathParam2: "+pathParam2);
      System.err.println("pathParam3: "+pathParam3);

      System.err.println("queryParam0: "+queryParam0);
      System.err.println("queryParam1: "+queryParam1);
      System.err.println("queryParam2: "+queryParam2);
      System.err.println("queryParam3: "+queryParam3);

      if(      !"cookieParam0".equals(cookieParam0)
            || !"cookieParam1".equals(cookieParam1)
            || !"cookieParam2".equals(cookieParam2)
            || !"cookieParam3".equals(cookieParam3))
         return Response.status(Response.Status.BAD_REQUEST).build();

      if(      !"formParam0".equals(formParam0)
            || !"formParam1".equals(formParam1)
            || !"formParam2".equals(formParam2)
            || !"formParam3".equals(formParam3))
         return Response.status(Response.Status.BAD_REQUEST).build();

      if(      !"headerParam0".equals(headerParam0)
            || !"headerParam1".equals(headerParam1)
            || !"headerParam2".equals(headerParam2)
            || !"headerParam3".equals(headerParam3))
         return Response.status(Response.Status.BAD_REQUEST).build();

      if(      !"matrixParam0".equals(matrixParam0)
            || !"matrixParam1".equals(matrixParam1)
            || !"matrixParam2".equals(matrixParam2)
            || !"matrixParam3".equals(matrixParam3))
         return Response.status(Response.Status.BAD_REQUEST).build();

      if(      !"pathParam0".equals(pathParam0)
            || !"pathParam1".equals(pathParam1)
            || !"pathParam2".equals(pathParam2)
            || !"pathParam3".equals(pathParam3))
         return Response.status(Response.Status.BAD_REQUEST).build();

      if(      !"queryParam0".equals(queryParam0)
            || !"queryParam1".equals(queryParam1)
            || !"queryParam2".equals(queryParam2)
            || !"queryParam3".equals(queryParam3))
         return Response.status(Response.Status.BAD_REQUEST).build();

      return Response.ok().build();
   }
}
