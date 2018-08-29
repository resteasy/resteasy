package org.jboss.resteasy.test.smoke;

import org.jboss.logging.Logger;

import javax.ws.rs.Path;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class LocatingResource
{

   private static final Logger LOG = Logger.getLogger(LocatingResource.class);

   @Path("locating")
   public SimpleResource getLocating()
   {
      LOG.info("LOCATING...");
      return new SimpleResource();
   }


   @Path("subresource")
   public SimpleSubresource getSubresource()
   {
      LOG.info("Subresource");
      return new SimpleSubresource();
   }


   @Path("notlocating")
   public SimpleResource getNotLocating()
   {
      LOG.info("NOT LOCATING... i.e. returning null");
      return null;
   }

}
