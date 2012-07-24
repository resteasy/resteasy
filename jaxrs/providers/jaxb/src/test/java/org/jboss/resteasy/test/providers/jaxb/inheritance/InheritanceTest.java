package org.jboss.resteasy.test.providers.jaxb.inheritance;

import junit.framework.Assert;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Before;
import org.junit.Test;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class InheritanceTest extends BaseResourceTest
{
   @Before
   public void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(ZooWS.class);
   }

   @Test
   public void testInheritance() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/zoo"));
      ClientResponse<?> response = request.get();
      Assert.assertEquals(200, response.getStatus());
      response.releaseConnection();
   }

}
