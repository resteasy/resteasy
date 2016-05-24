package org.jboss.resteasy.tests;

import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApplicationTest
{
   static Client client;
   static WebTarget baseTarget;

   @BeforeClass
   public static void initClient()
   {
      client = ClientBuilder.newClient();
      baseTarget = client.target("http://localhost:8080/cdilocator-test");
   }

   @AfterClass
   public static void closeClient()
   {
      client.close();
   }

   @Test
   public void genericTypeTest() throws Exception
   {
      String result = baseTarget.path("test").queryParam("foo", "yo").request().get(String.class);
       System.out.println(result);
   }

    @Test
    public void locatorTest() throws Exception
    {
        String result = baseTarget.path("test/lookup").queryParam("foo", "yo").request().get(String.class);
        System.out.println(result);
    }

}
