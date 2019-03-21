package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.annotations.jaxrs.CookieParam;
import org.jboss.resteasy.annotations.jaxrs.FormParam;
import org.jboss.resteasy.annotations.jaxrs.HeaderParam;
import org.jboss.resteasy.annotations.jaxrs.MatrixParam;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class RESTEasyParamBeanCdi
{
   private String cookieParam2;
   @CookieParam
   private String cookieParam3;

   private String formParam2;
   @FormParam
   private String formParam3;

   private String headerParam2;
   @HeaderParam
   private String headerParam3;

   private String matrixParam2;
   @MatrixParam
   private String matrixParam3;

   @PathParam
   private String pathParam3;
   private String pathParam4;

   private String queryParam2;
   @QueryParam
   private String queryParam3;


   public String getCookieParam2()
   {
      return cookieParam2;
   }

   @CookieParam
   public void setCookieParam2(String cookieParam2)
   {
      this.cookieParam2 = cookieParam2;
   }

   public String getCookieParam3() {
      return cookieParam3;
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

   public String getFormParam3()
   {
      return formParam3;
   }

   public String getPathParam3() {
      return pathParam3;
   }

   public String getPathParam4()
   {
      return pathParam4;
   }

   @PathParam
   public void setPathParam4(String pathParam4)
   {
      this.pathParam4 = pathParam4;
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

   public String getHeaderParam3() {
      return headerParam3;
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

   public String getMatrixParam3() {
      return matrixParam3;
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

   public String getQueryParam3() {
      return queryParam3;
   }

}
