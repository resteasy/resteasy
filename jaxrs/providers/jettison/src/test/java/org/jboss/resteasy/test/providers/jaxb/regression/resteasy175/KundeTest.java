package org.jboss.resteasy.test.providers.jaxb.regression.resteasy175;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class KundeTest extends BaseResourceTest
{
   @Before
   public void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(KundenverwaltungResource.class);
   }

   @Test
   public void testKunde() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/kundenverwaltung/kunden"));
      ClientResponse<?> response = request.get();
      Assert.assertEquals(200, response.getStatus());
      response.releaseConnection();
   }

}
