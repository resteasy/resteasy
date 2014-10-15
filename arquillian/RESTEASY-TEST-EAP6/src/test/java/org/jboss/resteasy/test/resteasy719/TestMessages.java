package org.jboss.resteasy.test.resteasy719;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.providers.hibernatevalidator.i18n.Messages;
import org.jboss.resteasy.resteasy719.TestApplication;
import org.jboss.resteasy.resteasy719.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * RESTEASY-719
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Oct 14, 2014
 */
@RunWith(Arquillian.class)
public class TestMessages
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-719.war")
            .addClasses(TestApplication.class)
            .addClasses(TestResource.class)
            .addAsWebInfResource("719/web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void test_enUS() throws Exception
   {
      if (Messages.class.getClassLoader().getResourceAsStream("/org/jboss/resteasy/providers/hibernatevalidator/i18n/Messages.i18n_xx.properties") == null)
      {
         System.out.println("Resteasy build without i18n test property files. Skipping test.");
         return;
      }
      String language = "xx";
      String country = "YY";
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-719/setLocale/" + language +"/" + country);
      ClientResponse<?> response = request.post();
      System.out.println("status: " + response.getStatus());
      assertEquals(204, response.getStatus());
      request = new ClientRequest("http://localhost:8080/RESTEASY-719/testLocale/");
      response = request.post();
      System.out.println("status: " + response.getStatus());
      System.out.println("result: " + response.getEntity(String.class));
      assertEquals(200, response.getStatus());
      assertEquals("RESTEASY003000: aaa", response.getEntity(String.class));
   }
}
