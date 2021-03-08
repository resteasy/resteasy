package org.jboss.resteasy.test;

import org.jboss.resteasy.plugins.server.reactor.netty.ReactorNettyJaxrsServer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests if supplying port <code>0</code> works correctly. When using port <code>0</code> the container should take the
 * first available port and return it in {@link ReactorNettyJaxrsServer#getPort()}.
 *
 * @author Sebastian Łaskawiec
 * @since 4.0
 * @see https://issues.jboss.org/browse/RESTEASY-1429
 */
public class PortAssigningTest
{
   @Test
   public void testZeroPort() throws Exception {
      //given
      ReactorNettyJaxrsServer server = new ReactorNettyJaxrsServer();
      server.setPort(0);
      server.getDeployment();

      //when
      server.start();
      int ip = server.getPort();
      server.stop();

      //then
      Assert.assertTrue(ip != 0);
   }

}
