package org.jboss.resteasy.test.spring.deployment.resource;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.stereotype.Controller;


/**
 * User: rsearls
 * Date: 2/20/17
 */
@Controller
@Path("numbers")
public class NumbersResource {

   @GET
   @Produces("application/json")
   public JsonArray numbers()
   {
      JsonArrayBuilder array = Json.createArrayBuilder();
      for (int i = 0; i < 10; i++)
      {
         array.add(String.valueOf(System.currentTimeMillis()));
      }
      return array.build();

   }

}

