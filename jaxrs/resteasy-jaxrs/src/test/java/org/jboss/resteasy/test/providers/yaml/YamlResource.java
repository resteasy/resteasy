package org.jboss.resteasy.test.providers.yaml;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Date;

/**
 * Yaml test resource.
 *
 * @author Martin Algesten
 */
@Path("/yaml")
public class YamlResource
{


   public static MyObject createMyObject()
   {

      MyObject obj = new MyObject();

      obj.setSomeText("This is some sample text");
      obj.setDate(new Date(123456789));
      obj.getNested().setMoreText("This is some more sample text");

      return obj;
   }


   @GET
   @Produces("text/x-yaml")
   public MyObject getMyObject()
   {
      return createMyObject();
   }


   @POST
   @Consumes("text/x-yaml")
   @Produces("text/x-yaml")
   public MyObject setMyObject(MyObject obj)
   {
      return obj;
   }


}
