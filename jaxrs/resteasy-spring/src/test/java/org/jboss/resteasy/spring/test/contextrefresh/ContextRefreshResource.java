package org.jboss.resteasy.spring.test.contextrefresh;

import org.springframework.stereotype.Component;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * RESTEASY-632.
 * Test suggested by Holger Morch.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * Created Feb 12, 2012
 */
@Path("refresh")
@Component
public class ContextRefreshResource
{
   @Path("locator/{id}")
   @Produces("text/plain")
   public String locator()
   {
      return "locator";
   }
}
