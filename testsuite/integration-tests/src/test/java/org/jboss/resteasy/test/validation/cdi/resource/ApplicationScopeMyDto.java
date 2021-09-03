package org.jboss.resteasy.test.validation.cdi.resource;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ApplicationScopeMyDto {

   @NotNull
   @Size(min = 1)
   private String test;

   @NotNull
   @Size(min = 1)
   private String path;

   public String getTest() {
      return test;
   }

   public void setTest(String test) {
      this.test = test;
   }

   public String getPath() {
      return path;
   }

   public void setPath(String path) {
      this.path = path;
   }
}
