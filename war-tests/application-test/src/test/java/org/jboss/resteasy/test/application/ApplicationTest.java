package org.jboss.resteasy.test.application;

import org.jboss.resteasy.client.ClientRequest;
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
}
