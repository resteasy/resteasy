package org.jboss.resteasy.test.providers.multipart;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.providers.multipart.resource.EmptyInputStreamMultipartProviderMyBean;
import org.jboss.resteasy.test.providers.multipart.resource.EmptyInputStreamMultipartProviderResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-204.
 * POJO with empty InputStream field returned as "mutlipart/form-data" produces no headers in multipart
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class EmptyInputStreamMultipartProviderTest {

    protected final Logger logger = Logger.getLogger(EmptyInputStreamMultipartProviderTest.class.getName());

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(EmptyInputStreamMultipartProviderTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, EmptyInputStreamMultipartProviderResource.class, EmptyInputStreamMultipartProviderMyBean.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, EmptyInputStreamMultipartProviderTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Resource returning POJO with empty InputStream field, the response is checked to contain the header
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test() throws Exception {
    	Client client = ClientBuilder.newClient();
        WebTarget target = client.target(generateURL("/rest/zba"));
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String string = response.readEntity(String.class);
        logger.info(string);
        Assert.assertTrue("The response doesn't contain the expected header", string.indexOf("Content-Length") > -1);
        client.close();
    }

}
