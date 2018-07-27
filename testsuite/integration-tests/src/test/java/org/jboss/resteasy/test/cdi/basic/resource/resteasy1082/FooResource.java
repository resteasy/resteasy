package org.jboss.resteasy.test.cdi.basic.resource.resteasy1082;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.enterprise.context.RequestScoped;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@RequestScoped
@Path("/foo")
public class FooResource {
   @Min(3L)
   private int k = 0;

   public FooResource() {
   }

   @GET
   @Produces({"application/json"})
   public List<String> getAll() {
      List<String> data = new ArrayList();

      for(int i = 0; i < 10; ++i) {
         data.add(UUID.randomUUID().toString());
      }

      return data;
   }
}

