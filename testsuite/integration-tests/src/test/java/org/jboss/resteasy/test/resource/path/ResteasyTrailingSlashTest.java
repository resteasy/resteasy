package org.jboss.resteasy.test.resource.path;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.resource.path.resource.ResteasyTrailingSlashResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Check for slash in URL
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ResteasyTrailingSlashTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResteasyTrailingSlashTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ResteasyTrailingSlashResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResteasyTrailingSlashTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client should accept also URL ended by slash
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testTrailingSlash() throws Exception {
        Client client = ClientBuilder.newClient();
        String val = client.target(generateURL("/test/"))
                .request().get(String.class);
        assertEquals("hello world", val, "Wrong response");
        client.close();
    }
}
