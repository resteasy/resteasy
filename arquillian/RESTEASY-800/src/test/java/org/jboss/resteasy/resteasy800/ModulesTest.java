package org.jboss.resteasy.resteasy800;

import static org.junit.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.resteasy800.TestApplication;
import org.jboss.resteasy.resteasy800.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 5, 2013
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ModulesTest
{
   private static final Logger log = LoggerFactory.getLogger(ModulesTest.class);

   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-800.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addAsWebInfResource("web.xml");
      System.out.println(war.toString(true));
      return war;
   }

   @Test
   public void testModules() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-800/test");
      ClientResponse<String> response = request.get(String.class);
      log.info("status: " + response.getStatus());
      log.info("entity: " + response.getEntity());
      assertEquals(200, response.getStatus());
   }
}
