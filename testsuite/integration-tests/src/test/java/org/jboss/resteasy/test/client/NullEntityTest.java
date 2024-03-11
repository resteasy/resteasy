package org.jboss.resteasy.test.client;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.client.resource.NullEntityResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Regression for RESTEASY-1057
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class NullEntityTest extends ClientTestBase {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(NullEntityTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, NullEntityResource.class);
    }

    /**
     * @tpTestDetails Test to send null by post request.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostNull() {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target(generateURL("/null"));
        String response = target.request().post(null, String.class);
        Assertions.assertEquals("", response, "Wrong response");
        client.close();
    }

    /**
     * @tpTestDetails Test to send null via entity by post request.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEntity() {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target(generateURL("/entity"));
        String response = target.request().post(Entity.entity(null, MediaType.WILDCARD), String.class);
        Assertions.assertEquals("", response, "Wrong response");
        client.close();
    }

    /**
     * @tpTestDetails Test to send null via form
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testForm() {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target(generateURL("/form"));
        String response = target.request().post(Entity.form((Form) null), String.class);
        Assertions.assertEquals(null, response, "Wrong response");
        client.close();
    }

    /**
     * @tpTestDetails Test resource with "text/html" media type
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testHtml() {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target(generateURL("/html"));
        String response = target.request().post(Entity.html(null), String.class);
        Assertions.assertEquals("", response, "Wrong response");
        client.close();
    }

    /**
     * @tpTestDetails Test resource with "application/xhtml+xml" media type
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXhtml() {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target(generateURL("/xhtml"));
        String response = target.request().post(Entity.xhtml(null), String.class);
        Assertions.assertEquals("", response, "Wrong response");
        client.close();
    }

    /**
     * @tpTestDetails Test resource with "application/xml" media type
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXml() {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target(generateURL("/xml"));
        String response = target.request().post(Entity.xml(null), String.class);
        Assertions.assertEquals("", response, "Wrong response");
        client.close();
    }
}
