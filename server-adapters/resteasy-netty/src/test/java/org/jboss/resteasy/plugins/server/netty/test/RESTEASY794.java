package org.jboss.resteasy.plugins.server.netty.test;

import junit.framework.Assert;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.Collections;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * 11 12 2012
 *
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class RESTEASY794 {

    @Path("/")
    public static class HelloResource {

        @GET
        @Path("hello")
        public String sayHello() {
            return "Hello";
        }
    }

    /**
     * @param args
     */
    @Test
    public void testIt(String[] args) throws Exception {
        NettyJaxrsServer netty = new NettyJaxrsServer();
        ResteasyDeployment deployment = new ResteasyDeployment();

        deployment.setResourceClasses(Collections.singletonList(HelloResource.class.getName()));

        netty.setDeployment(deployment);
        netty.setPort(TestPortProvider.getPort());
        netty.setRootResourcePath("");
        netty.setSecurityDomain(null);
        netty.start();

        Thread.sleep(1000);

        try {
            netty.stop();
        } catch (Exception e) {
            if (e.getClass().equals(NullPointerException.class))
                Assert.fail();
        }

        {
           ClientRequest request = new ClientRequest(generateURL("/hello"));
           ClientResponse<String> response = request.get(String.class);
           org.junit.Assert.assertEquals(200, response.getStatus());
           org.junit.Assert.assertEquals("Hello", response.getEntity());
        }


    }

}
