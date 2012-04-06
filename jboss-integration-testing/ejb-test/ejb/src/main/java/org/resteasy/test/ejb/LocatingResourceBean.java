package org.resteasy.test.ejb;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Stateless
public class LocatingResourceBean implements LocatingResource
{
   @EJB
   SimpleResource simple;

   public SimpleResource getLocating()
   {
      return simple;
   }
}
