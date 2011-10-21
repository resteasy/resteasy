package org.jboss.resteasy.test.application;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.Assert;
import org.junit.Test;

/**
 * RESTEASY-381
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApplicationTest
{
   @Test
   public void testCount() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/my/application/count");
      String count = request.getTarget(String.class);
      Assert.assertEquals("1", count);
   }

   /**
    *
    * RESTEASY-518
    *
    * @throws Exception
    */
   @Test
   public void testNullJaxb() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/my/null");
//      request.header("Content-Length", "0");
      request.header("Content-Type", "application/xml");
      ClientResponse res = request.post();
      Assert.assertEquals(204, res.getStatus());
   }

   /**
    *
    * RESTEASY-582
    *
    * @throws Exception
    */
   @Test
   public void testBadMediaTypeNoSubtype() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/my/application/count");
      request.accept("text");
      final ClientResponse response = request.get();
      Assert.assertEquals(400, response.getStatus());
   }

   /**
    *
    * RESTEASY-582
    *
    * @throws Exception
    */
   @Test
   public void testBadMediaTypeNonNumericQualityValue() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/my/application/count");
      request.accept("text/plain; q=bad");
      final ClientResponse response = request.get();
      Assert.assertEquals(400, response.getStatus());
   }
}
