package org.jboss.resteasy.examples.contacts.client;

import javax.annotation.Resource;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.examples.contacts.core.Contacts;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
// Load the beans to configure, here the embedded jetty
@ContextConfiguration(locations = { "/test-config.xml" })
public class TestClient
{
   private static final String USER_EMAIL = "olivier@yahoo.com";
   
   // JSR 250 annotation injecting the servletContainer bean. Similar to the
   // Spring @Autowired annotation
   @Resource
   private Server servletContainer;

   @Resource
   private ContactClient client;

   @Test
   public void testGetContacts()
   {
      Assert.assertTrue(servletContainer.isStarted());
      ClientResponse<Contacts> contacts = client.getContacts();
      Assert.assertNotNull(contacts);
      Assert.assertEquals(3, contacts.getEntity().getContacts().size());
      Assert.assertEquals(USER_EMAIL, contacts.getEntity().getContacts()
            .iterator().next().getEmail());
   }
}
