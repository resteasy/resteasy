package org.jboss.resteasy.test.providers.jaxb.regression.resteasy175;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

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
      Response response = ClientBuilder.newClient().target(generateURL("/kundenverwaltung/kunden")).request().get();
      Assert.assertEquals(200, response.getStatus());
      response.close();
   }

}
