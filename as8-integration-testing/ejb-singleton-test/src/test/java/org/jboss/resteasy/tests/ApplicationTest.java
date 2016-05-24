package org.jboss.resteasy.tests;

import junit.framework.Assert;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApplicationTest
{

   @Test
   public void testSingleton() throws Exception
   {
      Client client = ClientBuilder.newClient();
      String value = null;
      WebTarget base = client.target("http://localhost:8080/ejb-singleton-test/root");
      value = base.path("sub").request().get(String.class);
      Assert.assertEquals("hello", value);
      value = base.path("injected").request().get(String.class);
      Assert.assertEquals("true", value);
      value = base.path("intfsub").request().get(String.class);
      System.out.println(value);

      Response response = base.path("exception").request().get();
      Assert.assertEquals(201, response.getStatus());



      client.close();
   }

}
