package org.jboss.resteasy.test.resource.param;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MatrixPathParamTest {

    @Deployment
    public static Archive<?> deploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(MatrixPathParamTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, TestResourceServer.class, TestSubResourceServer.class);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(MatrixPathParamTest.class.getSimpleName());
    }

    @Path("/")
    public static class TestResourceServer {
        @Path("matrix1")
        public TestSubResourceServer getM1(@MatrixParam("m1") String m1) {
            return new TestSubResourceServer(m1);
        }
    }

    public static class TestSubResourceServer {
        protected String m1;

        TestSubResourceServer(final String m1) {
            this.m1 = m1;
        }

        @GET
        @Path("matrix2")
        public String getM2(@MatrixParam("m2") String m2) {
            return m1 + m2;
        }
    }

    @Path("/")
    public interface TestInterfaceClient {
        @Path("matrix1")
        TestSubInterfaceClient getM1(@MatrixParam("m1") String m1);
    }

    public interface TestSubInterfaceClient {
        @GET
        @Path("matrix2")
        String getM2(@MatrixParam("m2") String m2);
    }

    @Test
    public void testSingleAcceptHeader() throws Exception {
        Client client = ClientBuilder.newClient();
        ResteasyWebTarget target = (ResteasyWebTarget) client.target(generateBaseUrl());
        TestInterfaceClient proxy = target.proxy(TestInterfaceClient.class);

        String result = proxy.getM1("a").getM2("b");
        Assertions.assertEquals("ab", result);
        client.close();
    }

}
