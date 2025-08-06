package org.jboss.resteasy.test.wadl;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class DeploymentTest {

    private static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchiveWithApplication(DeploymentTest.class.getSimpleName(),
                WadlTestApplication.class);
        war.addPackages(true, "org.jboss.resteasy.wadl");
        TestUtil.finishContainerPrepare(war, null, ExtendedResource.class, ListType.class);
        return war;
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, DeploymentTest.class.getSimpleName());
    }

    //////////////////////////////////////////////////////////////////////////////
    @BeforeAll
    public static void beforeClass() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() {
        client.close();
    }

    @Test
    public void testBasic() {
        {
            ResteasyWebTarget target = client.target(generateURL("/application.xml"));
            Response response = target.request().get();
            int status = response.getStatus();
            Assertions.assertEquals(200, status);

            // get Application
            org.jboss.resteasy.wadl.jaxb.Application application = response
                    .readEntity(org.jboss.resteasy.wadl.jaxb.Application.class);
            assertNotNull(application);
        }

        {
            ResteasyWebTarget target = client.target(generateURL("/wadl-extended/xsd0.xsd"));
            Response response = target.request().get();
            int status = response.getStatus();
            Assertions.assertEquals(200, status);

            assertNotNull(response.readEntity(String.class));
        }
    }
}
