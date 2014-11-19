package org.jboss.resteasy.test.resteasy1125;

import static org.junit.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.resteasy1125.*;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * RESTEASY-1125
 *
 * Nov 19, 2014
 */
@RunWith(Arquillian.class)
public class TestGenericResource
{

   String str = "<model></model>";

   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1125.war")
            .addClasses(TestApplication.class)
            .addClasses(Model.class)
            .addClasses(AbstractResource.class)
            .addClasses(OtherAbstractResource.class)
            .addClasses(TestResource.class)
            .addClasses(TestResource2.class)
            .addAsWebInfResource("1125/web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            ;
      System.out.println(war.toString(true));
      return war;
   }
   
   @Test
   public void testGenericInheritingResource() throws Exception
   {
       ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1125/test");
       request.body("application/xml", str);
       ClientResponse<?> response = request.post();
       System.out.println("status: " + response.getStatus());
       Assert.assertEquals(200, response.getStatus());
       String answer = response.getEntity(String.class);
       Assert.assertEquals("Success!", answer);
       System.out.println(answer);
   }

    @Test
    public void testGenericResource() throws Exception
    {
        ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-1125/test2");
        request.body("application/xml", str);
        ClientResponse<?> response = request.post();
        System.out.println("status: " + response.getStatus());
        Assert.assertEquals(200, response.getStatus());
        String answer = response.getEntity(String.class);
        Assert.assertEquals("Success!", answer);
        System.out.println(answer);
    }
}
