package org.jboss.resteasy.tests.scanning;

import org.junit.Test;
import org.junit.Assert;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * TEST  RESTEASY-263 RESTEASY-274

 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ScanTest
{
   @Test
   public void testAll() throws Exception
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      ClientRequest request = new ClientRequest("http://localhost:8080/scanning-test/test/doit");
      ClientResponse response = request.get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("hello world", response.getEntity(String.class));
   }
}
