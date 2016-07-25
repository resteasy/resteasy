package org.jboss.resteasy.test;

import org.jboss.resteasy.plugins.server.sun.http.SunHttpJaxrsServer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests if supplying port <code>0</code> works correctly. When using port <code>0</code> the container should take the
 * first available port and return it in {@link org.jboss.resteasy.plugins.server.sun.http.SunHttpJaxrsServer#getPort()}.
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
        SunHttpJaxrsServer server = new SunHttpJaxrsServer();
        server.setPort(0);

        //when
        server.start();
        int ip = server.getPort();
        server.stop();

        //then
        Assert.assertTrue(ip != 0);
    }

}
