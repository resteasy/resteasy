package org.jboss.resteasy.test.spring.inmodule.resource;

public class SpringMvcHttpResponseCodesPerson {
   private String name;

   public SpringMvcHttpResponseCodesPerson() {

   }

   public SpringMvcHttpResponseCodesPerson(final String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
