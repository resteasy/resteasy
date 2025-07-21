package org.jboss.resteasy.test.providers.multipart;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.multipart.resource.XOPMultipartProxy;
import org.jboss.resteasy.test.providers.multipart.resource.XOPMultipartProxyGetFileResponse;
import org.jboss.resteasy.test.providers.multipart.resource.XOPMultipartProxyPutFileRequest;
import org.jboss.resteasy.test.providers.multipart.resource.XOPMultipartProxyResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Multipart provider used to send and receive XOP messages. RESTEASY-2127.
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class XOPMultipartProxyTest {

    private static Client client;
    private static XOPMultipartProxy proxy;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(XOPMultipartProxyTest.class.getSimpleName());
        war.addClass(XOPMultipartProxyGetFileResponse.class);
        war.addClass(XOPMultipartProxyPutFileRequest.class);
        war.addClass(XOPMultipartProxy.class);
        return TestUtil.finishContainerPrepare(war, null, XOPMultipartProxyResource.class);
    }

    @BeforeAll
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
        ResteasyWebTarget target = (ResteasyWebTarget) client.target(generateURL(""));
        proxy = target.proxy(XOPMultipartProxy.class);
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, XOPMultipartProxyTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Receive XOP message
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testXOPGet() throws Exception {
        ResteasyWebTarget target = (ResteasyWebTarget) client.target(generateURL(""));
        XOPMultipartProxy test = target.proxy(XOPMultipartProxy.class);
        XOPMultipartProxyGetFileResponse fileResp = test.getFile("testXOPGet");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        fileResp.getData().writeTo(out);
        Assertions.assertEquals("testXOPGet", new String(out.toByteArray()));
    }

    /**
     * @tpTestDetails Send XOP message
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testXOPSend() throws Exception {
        File tmpFile = Files.createTempFile("pre", ".tmp").toFile();
        tmpFile.deleteOnExit();
        Writer writer = new FileWriter(tmpFile);
        writer.write("testXOPSend");
        writer.close();
        XOPMultipartProxyPutFileRequest req = new XOPMultipartProxyPutFileRequest();
        req.setContent(new DataHandler(new FileDataSource(tmpFile)));
        Response response = proxy.putFile(req);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("testXOPSend", response.readEntity(String.class));
    }
}
