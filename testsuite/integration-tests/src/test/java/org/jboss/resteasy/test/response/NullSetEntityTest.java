package org.jboss.resteasy.test.response;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.response.resource.NullSetEntityTestAnnotationFilter;
import org.jboss.resteasy.test.response.resource.NullSetEntityTestFilter;
import org.jboss.resteasy.test.response.resource.NullSetEntityTestResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-3089
 * @tpSince RESTEasy 6.1.0
 */

@RunWith(Arquillian.class)
@RunAsClient
public class NullSetEntityTest {

    static Client client;

    static final String SET_ENTITY_DEPLOYMENT = "SetEntity";
    static final String SET_ENTITY_ANNOTATION_DEPLOYMENT = "SetEntityAnnotation";

    @Deployment(name = SET_ENTITY_DEPLOYMENT)
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SET_ENTITY_DEPLOYMENT);
        return TestUtil.finishContainerPrepare(war, null, NullSetEntityTestResource.class, NullSetEntityTestFilter.class);
    }

    @Deployment(name = SET_ENTITY_ANNOTATION_DEPLOYMENT)
    public static Archive<?> deployAnnotation() {
        WebArchive war = TestUtil.prepareArchive(SET_ENTITY_ANNOTATION_DEPLOYMENT);
        return TestUtil.finishContainerPrepare(war, null, NullSetEntityTestResource.class, NullSetEntityTestAnnotationFilter.class);
    }

    private String generateURL(String path, String deployment) {
        return PortProviderUtil.generateURL(path, deployment);
    }

    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void close() {
        client.close();
        client = null;
    }

    @Test
    @OperateOnDeployment(SET_ENTITY_DEPLOYMENT)
    public void testNonNullResource(){
        WebTarget webTarget = client.target(generateURL("/test/nonNull", SET_ENTITY_DEPLOYMENT));
        checkWebTarget(webTarget, MediaType.TEXT_PLAIN_TYPE.withCharset("UTF-8"));
    }

    @Test
    @OperateOnDeployment(SET_ENTITY_DEPLOYMENT)
    public void testNullResource(){
        WebTarget webTarget = client.target(generateURL("/test/null", SET_ENTITY_DEPLOYMENT));
        checkWebTarget(webTarget, MediaType.APPLICATION_OCTET_STREAM_TYPE);
    }

    @Test
    @OperateOnDeployment(SET_ENTITY_DEPLOYMENT)
    public void testVoidResource(){
        WebTarget webTarget = client.target(generateURL("/test/void", SET_ENTITY_DEPLOYMENT));
        checkWebTarget(webTarget, MediaType.APPLICATION_OCTET_STREAM_TYPE);
    }

    @Test
    @OperateOnDeployment(SET_ENTITY_ANNOTATION_DEPLOYMENT)
    public void testNonNullAnnotationResource(){
        WebTarget webTarget = client.target(generateURL("/test/nonNull", SET_ENTITY_ANNOTATION_DEPLOYMENT));
        checkWebTarget(webTarget, MediaType.TEXT_PLAIN_TYPE);
    }

    @Test
    @OperateOnDeployment(SET_ENTITY_ANNOTATION_DEPLOYMENT)
    public void testNullAnnotationResource(){
        WebTarget webTarget = client.target(generateURL("/test/null", SET_ENTITY_ANNOTATION_DEPLOYMENT));
        checkWebTarget(webTarget, MediaType.TEXT_PLAIN_TYPE.withCharset("UTF-8"));
    }

    @Test
    @OperateOnDeployment(SET_ENTITY_ANNOTATION_DEPLOYMENT)
    public void testVoidAnnotationResource(){
        WebTarget webTarget = client.target(generateURL("/test/void", SET_ENTITY_ANNOTATION_DEPLOYMENT));
        checkWebTarget(webTarget, MediaType.TEXT_PLAIN_TYPE.withCharset("UTF-8"));
    }

    /**
     * @tpTestDetails Checks if the response filter correctly applied new entity in case the entity is null.
     * If the filter is not applied properly, the response contains null entity, null MediaType and status code 204 No Content
     * If the filter is applied properly, the response entity is "Hello World" with proper MediaType and status code 200 OK
     */
    private void checkWebTarget(WebTarget webTarget, MediaType mediaType){
        Response response = webTarget.request().get();
        String stringEntity = response.readEntity(String.class);
        MediaType responseMediaType = response.getMediaType();
        Response.StatusType statusInfo = response.getStatusInfo();

        Assert.assertEquals("Response entity doesn't match", "Hello World", stringEntity);
        Assert.assertEquals("Response media type doesn't match", mediaType, responseMediaType);
        Assert.assertEquals("Response status is incorrect", HttpResponseCodes.SC_OK, statusInfo.getStatusCode());
    }
}