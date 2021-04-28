package org.jboss.resteasy.springmvc.test.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.xml.bind.annotation.XmlRootElement;

@Path("/org/jboss/resteasy/springmvc/test")
public class TypeMappingResource
{
   @GET
   @Path("/noproduces")
   public TestBean get()
   {
      return new TestBean("name");
   }

   @XmlRootElement
   public static class TestBean
   {
      private String name;

      public TestBean()
      {

      }

      public TestBean(final String name)
      {
         this.name = name;
      }

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }

   }

}
