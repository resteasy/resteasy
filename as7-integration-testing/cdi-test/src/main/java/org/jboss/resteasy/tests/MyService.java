package org.jboss.resteasy.tests;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class MyService
{
   @Inject
   MyConfigBean bean;

   /**
    * Tests to make sure that a CDI bean was injected and that the XFormat provider overrides the default XML provider
    *
    * @return
    */
   @GET
   @Produces("application/xml")
   public XFormat get()
   {
      if (bean != null) System.out.println("MyConfigBean version: " + bean.version());
      else throw new RuntimeException("CDI Bean Injection didn't work for test!");
      return new XFormat("foo");
   }
}
