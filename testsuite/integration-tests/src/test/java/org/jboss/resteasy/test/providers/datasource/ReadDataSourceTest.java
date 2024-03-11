package org.jboss.resteasy.test.providers.datasource;

import java.io.UnsupportedEncodingException;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.apache.http.entity.StringEntity;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.datasource.resource.ReadDataSourceResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter DataSource provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ReadDataSourceTest {

    static ResteasyClient client;

    @Deployment()
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ReadDataSourceTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ReadDataSourceResource.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ReadDataSourceTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests DataSourceProviders ability to read the same stream twice and verifies the results of both reads
     *                are equal
     * @tpInfo RESTEASY-1182
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDataSourceInputStream() throws Exception {
        WebTarget target = client.target(generateURL("/" + ReadDataSourceResource.PATH_UPLOAD));

        Response response = target.request().post(Entity.entity(createContent(), "text/plain"));

        final StringBuilder msg = new StringBuilder();
        final String entity = response.readEntity(String.class);
        if (entity != null) {
            msg.append("\n").append(entity);
        }
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus(), "Unexpected response: " + msg.toString());
    }

    private StringEntity createContent() throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 2500; i++) {
            stringBuilder.append(i).append(":\n");
        }
        return new StringEntity(stringBuilder.toString());
    }
}
