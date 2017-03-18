package org.jboss.resteasy.test.resource.path;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.path.resource.ResourceMatchingAnotherResourceLocator;
import org.jboss.resteasy.test.resource.path.resource.ResourceMatchingAnotherSubResource;
import org.jboss.resteasy.test.resource.path.resource.ResourceMatchingErrorResource;
import org.jboss.resteasy.test.resource.path.resource.ResourceMatchingMainSubResource;
import org.jboss.resteasy.test.resource.path.resource.ResourceMatchingMediaWriter;
import org.jboss.resteasy.test.resource.path.resource.ResourceMatchingNoMediaResource;
import org.jboss.resteasy.test.resource.path.resource.ResourceMatchingStringBean;
import org.jboss.resteasy.test.resource.path.resource.ResourceMatchingStringBeanEntityProvider;
import org.jboss.resteasy.test.resource.path.resource.ResourceMatchingWeightResource;
import org.jboss.resteasy.test.resource.path.resource.ResourceMatchingYetAnotherSubresource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResourceMatchingTest {

    public static final String readFromStream(InputStream stream) throws IOException {
        InputStreamReader isr = new InputStreamReader(stream);
        return readFromReader(isr);
    }

    public static final String readFromReader(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String entity = br.readLine();
        br.close();
        return entity;
    }


    static Client client;

    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResourceMatchingTest.class.getSimpleName());
        war.addClasses(ResourceMatchingStringBean.class, ResourceMatchingAnotherResourceLocator.class);
        return TestUtil.finishContainerPrepare(war, null, ResourceMatchingYetAnotherSubresource.class, ResourceMatchingMainSubResource.class,
                ResourceMatchingAnotherSubResource.class, ResourceMatchingWeightResource.class, ResourceMatchingErrorResource.class,
                ResourceMatchingNoMediaResource.class, ResourceMatchingStringBeanEntityProvider.class, ResourceMatchingMediaWriter.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResourceMatchingTest.class.getSimpleName());
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Client sends GET request, the returned response Mediatype is APPLICATION_SVG_XML_TYPE as set up
     * by the custom MessageBodyWriter.
     * @tpPassCrit The correct mediatype and response code 200 (Success) is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMediaTypeFromProvider() {
        Response response = client.target(generateURL("/nomedia/list")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(MediaType.APPLICATION_SVG_XML_TYPE, response.getMediaType());
        response.close();
    }


    /**
     * @tpTestDetails Client sends GET request, the returned response Mediatype is APPLICATION_OCTET_STREAM_TYPE as
     * set up by the custom MessageBodyWriter.
     * @tpPassCrit The correct mediatype and response code 200 (Success) is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNoProduces() {
        Response response = client.target(generateURL("/nomedia/nothing")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(MediaType.APPLICATION_OCTET_STREAM_TYPE, response.getMediaType());
        response.close();
    }


    /**
     * @tpTestDetails Client sends GET request with request header of media type "text/*". The matching resource path
     * doesn't accept this mediatype. According to HTTP 1.1 specification:
     * "HTTP/1.1 servers are allowed to return responses which are not acceptable according to the accept headers
     * sent in the request."
     * @tpPassCrit The response code 406 (Not Acceptable) is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNonConcreteMatch() {
        Response response = client.target(generateURL("/error")).request("text/*").get();
        Assert.assertEquals(HttpResponseCodes.SC_NOT_ACCEPTABLE, response.getStatus());
        response.close();
    }


    /**
     * @tpTestDetails Client sends GET request, with header accept type "testi/*", the more specific resource is chosen
     * @tpPassCrit The response code 200 (Success) is returned and the correct resource is chosen
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testWildcard() {
        Response response = client.target(generateURL("/yas")).request("testi/*").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("test/text", response.readEntity(String.class));
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request, with header accept type "testiii/textiii" and "application/xml",
     * "application/xml" media type has in the resource higher probability to be chosen (indicates that by higher "qs"
     * than other resources)
     * @tpPassCrit The returned resource is the resource with media type "application/xml"
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testQS() {
        Response response = client.target(generateURL("/yas")).request("testiii/textiii", "application/xml").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("application/xml", response.readEntity(String.class));
        response.close();
    }

    /**
     * @tpTestDetails Client sends empty POST request, with specified header Content-Type "text/plain",
     * @tpPassCrit The correct subresource is chosen
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testOverride() {
        Response response = client.target(generateURL("/resource/subresource/sub")).request()
                .header("Content-Type", "text/plain").post(null);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("The incorrect subresource was chosen", ResourceMatchingAnotherSubResource.class.getSimpleName(), response.readEntity(String.class));
        response.close();
    }

    /**
     * @tpTestDetails Client sends OPTIONS request, which allows it to determine which HTTP methods it can issue without
     * initiating resource retrieval.
     * @tpPassCrit The response code 200 (Success) is returned and response contains GET option.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testOptions() {
        Response response = client.target(generateURL("/resource/subresource/something")).request().options();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String actual = response.readEntity(String.class);
        Assert.assertTrue("GET is not in the list of available methods for the specified resource", actual.contains("GET"));
        response.close();
    }

    /**
     * @tpTestDetails Client sends empty POST request with mediatype q parameter specified. It gives higher weight (q=0.9)
     * to a mediatype "application/*" then to mediatype "application/xml" (q=0.1).
     * @tpPassCrit The more specific mediatype will be chosen by the server ("application/xml")
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAvoidWildcard() {
        Response response = client.target(generateURL("/weight")).request("application/*;q=0.9", "application/xml;q=0.1")
                .post(null);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        MediaType mediaType = response.getMediaType();
        String actual = response.readEntity(String.class);
        Assert.assertEquals("application/xml", actual);
        Assert.assertEquals("MediaType of the response doesn't match the expected type", "application/xml;charset=UTF-8",
                mediaType.toString());
        response.close();
    }

}
