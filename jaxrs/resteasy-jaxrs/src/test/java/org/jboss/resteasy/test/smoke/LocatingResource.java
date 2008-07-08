package org.jboss.resteasy.test.smoke;

import javax.ws.rs.Path;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class LocatingResource
{
   @Path("locating")
   public SimpleResource getLocating()
   {
      System.out.println("LOCATING...");
      return new SimpleResource();
   }
   
   @Path("notlocating")
   public SimpleResource getNotLocating()
   {
      System.out.println("NOT LOCATING... i.e. returning null");
      return null;
   }
   
}
