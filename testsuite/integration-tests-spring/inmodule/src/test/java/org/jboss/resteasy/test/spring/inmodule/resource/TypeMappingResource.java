package org.jboss.resteasy.test.spring.inmodule.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlRootElement;

@Path("/test")
public class TypeMappingResource {
   @GET
   @Path("/noproduces")
   public TestBean get() {
      return new TestBean("name");
   }

   @XmlRootElement
   public static class TestBean {
      private String name;

      public TestBean() {

      }

      public TestBean(final String name) {
         this.name = name;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

   }

}
