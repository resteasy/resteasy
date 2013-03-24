package org.jboss.resteasy.resteasy802;

import static org.junit.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.GetRestful;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 5, 2013
 */
@RunWith(Arquillian.class)
public class GetRestfulTest
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-802.war")
            .addClasses(JaxRsActivator.class, TestResource.class, TestResourceImpl.class)
            ;
      System.out.println(war.toString(true));
      return war;
   }

   @Test
   public void testGetRestful() throws Exception
   {
	   GetRestful.getSubResourceClass(this.getClass());
	   GetRestful.getSubResourceClasses(this.getClass());
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-802/rest/test");
      ClientResponse<String> response = request.get(String.class);
      System.out.println("status: " + response.getStatus());
      System.out.println("entity: " + response.getEntity());
      assertEquals(200, response.getStatus());
   }
}