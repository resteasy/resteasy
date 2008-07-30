/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.test.providers.form;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;

/**
 * A FormResource.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Path("/form")
@ProduceMime("application/x-www-form-urlencoded")
@ConsumeMime("application/x-www-form-urlencoded")
public class FormResource
{

   @POST
   public FormValueHolder postObject(FormValueHolder value) 
   {
      
      return value;
   }
}
