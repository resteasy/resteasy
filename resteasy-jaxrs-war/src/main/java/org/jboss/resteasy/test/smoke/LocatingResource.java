package org.jboss.resteasy.test.smoke;


import javax.ws.rs.Path;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LocatingResource
{
   @Path("locating")
   public SimpleResource getLocating()
   {
      System.out.println("LOCATING...");
      return new SimpleResource();
   }
}