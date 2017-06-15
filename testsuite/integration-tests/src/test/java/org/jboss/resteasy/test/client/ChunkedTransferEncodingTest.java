package org.jboss.resteasy.test.client;

import java.io.File;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.test.client.resource.ChunkedTransferEncodingResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test facility for sending requests in chunked format
 * @tpSince RESTEasy 3.1.4
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ChunkedTransferEncodingTest {
   
    static ResteasyClient client;
    static final String testFilePath;

    static {
        testFilePath = TestUtil.getResourcePath(ChunkedTransferEncodingTest.class, "ChunkedTransferEncodingTestFile");
    }
    
    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ChunkedTransferEncodingTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ChunkedTransferEncodingResource.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ChunkedTransferEncodingTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test setting chunked on ResteasyWebTarget
     * @tpSince RESTEasy 3.1.4
     */    
    @Test
    public void testTarget() throws Exception {
       doTestTarget(Boolean.TRUE, "chunked");
       doTestTarget(Boolean.FALSE, "null");
       doTestTarget(null, "null");
    }
    
    public void doTestTarget(Boolean b, String expected) throws Exception
    {
       ResteasyWebTarget target = client.target(generateURL("/test"));
       if (b == Boolean.TRUE || b == Boolean.FALSE ) {
          target.setChunked(b.booleanValue());
       }
       Invocation.Builder request = target.request();
       File file = new File(testFilePath);
       Response response = request.post(Entity.entity(file, "text/plain"));
       String header = response.readEntity(String.class);
       Assert.assertEquals(200, response.getStatus());
       Assert.assertEquals(expected, header);
    }

    /**
     * @tpTestDetails Test setting chunked on ClientInvocationBuilder
     * @tpSince RESTEasy 3.1.4
     */ 
    @Test
    public void testRequest() throws Exception {
       doTestRequest(Boolean.TRUE, "chunked");
       doTestRequest(Boolean.FALSE, "null");
       doTestRequest(null, "null");
    }
    
    protected void doTestRequest(Boolean b, String expected) throws Exception
    {
       ResteasyWebTarget target = client.target(generateURL("/test"));
       ClientInvocationBuilder request = (ClientInvocationBuilder) target.request();
       if (b != null){
          request.setChunked(b);
       }
       File file = new File(testFilePath);
       Response response = request.post(Entity.entity(file, "text/plain"));
       String header = response.readEntity(String.class);
       Assert.assertEquals(200, response.getStatus());
       Assert.assertEquals(expected, header);
    }
}
