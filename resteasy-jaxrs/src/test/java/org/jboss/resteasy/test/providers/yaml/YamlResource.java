package org.jboss.resteasy.test.providers.yaml;

import java.util.Date;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;

/**
 * Yaml test resource.
 *
 * @author Martin Algesten
 */
@Path("/yaml")
public class YamlResource
{
   
   
   public static MyObject createMyObject() {

      MyObject obj = new MyObject();
      
      obj.setSomeText("This is some sample text");
      obj.setDate( new Date(123456789) );
      obj.getNested().setMoreText("This is some more sample text");
    
      return obj;
   }
   
   
   @GET
   @ProduceMime("text/x-yaml")
   public MyObject getMyObject() {
      return createMyObject();
   }
   
 

   @POST
   @ConsumeMime("text/x-yaml")
   @ProduceMime("text/x-yaml")
   public MyObject setMyObject( MyObject obj ) {
      return obj;
   }
   

}
