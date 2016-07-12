package org.jboss.resteasy.test.providers.multipart;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.test.providers.multipart.resource.HeaderFlushedOutputStreamBean;
import org.jboss.resteasy.test.providers.multipart.resource.HeaderFlushedOutputStreamService;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-190
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class HeaderFlushedOutputStreamTest {
    static Client client;

    @BeforeClass
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
    }

    static final String testFilePath;

    static {
        testFilePath = TestUtil.getResourcePath(HeaderFlushedOutputStreamTest.class,
                "HeaderFlushedOutputStreamTestData.txt");
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(HeaderFlushedOutputStreamTest.class.getSimpleName());
        war.addClass(HeaderFlushedOutputStreamBean.class);
        return TestUtil.finishContainerPrepare(war, null, HeaderFlushedOutputStreamService.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, HeaderFlushedOutputStreamTest.class.getSimpleName());
    }

    private static final String TEST_URI = generateURL("/mime");

    /**
     * @tpTestDetails Loopback to examine form-data
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPost() throws Exception {
        // prepare file
        File file = new File(testFilePath);
        Assert.assertTrue("File " + testFilePath + " doesn't exists", file.exists());
        // test logic
        MultipartOutput mpo = new MultipartOutput();
        mpo.addPart("This is Value 1", MediaType.TEXT_PLAIN_TYPE);
        mpo.addPart("This is Value 2", MediaType.TEXT_PLAIN_TYPE);
        mpo.addPart(file, MediaType.TEXT_PLAIN_TYPE);
        Response response = client.target(TEST_URI).request().post(Entity.entity(mpo, MediaType.MULTIPART_FORM_DATA_TYPE));
        BufferedInputStream in = new BufferedInputStream(response.readEntity(InputStream.class));
        String contentType = response.getHeaderString("content-type");
        ByteArrayDataSource ds = new ByteArrayDataSource(in, contentType);
        MimeMultipart mimeMultipart = new MimeMultipart(ds);
        Assert.assertEquals("Wrong count of parts of response", mimeMultipart.getCount(), 3);
        response.close();
    }


    /**
     * @tpTestDetails Test post method
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostForm() throws Exception {
        // prepare file
        File file = new File(testFilePath);
        Assert.assertTrue("File " + testFilePath + " doesn't exists", file.exists());
        // test logic
        MultipartFormDataOutput mpfdo = new MultipartFormDataOutput();
        mpfdo.addFormData("part1", "This is Value 1", MediaType.TEXT_PLAIN_TYPE);
        mpfdo.addFormData("part2", "This is Value 2", MediaType.TEXT_PLAIN_TYPE);
        mpfdo.addFormData("data.txt", file, MediaType.TEXT_PLAIN_TYPE);

        Response response = client.target(TEST_URI).request().post(Entity.entity(mpfdo, MediaType.MULTIPART_FORM_DATA_TYPE));
        BufferedInputStream in = new BufferedInputStream(response.readEntity(InputStream.class));

        String contentType = response.getHeaderString("content-type");
        ByteArrayDataSource ds = new ByteArrayDataSource(in, contentType);
        MimeMultipart mimeMultipart = new MimeMultipart(ds);
        Assert.assertEquals("Wrong count of parts of response", mimeMultipart.getCount(), 3);
        response.close();
    }

    /**
     * @tpTestDetails Test get method
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGet() throws Exception {

        Response response = client.target(TEST_URI).request().get();


        Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        BufferedInputStream in = new BufferedInputStream(response.readEntity(InputStream.class));
        String contentType = response.getHeaderString("content-type");
        ByteArrayDataSource ds = new ByteArrayDataSource(in, contentType);
        MimeMultipart mimeMultipart = new MimeMultipart(ds);
        Assert.assertEquals("Wrong count of parts of response", mimeMultipart.getCount(), 1);

        BodyPart part = mimeMultipart.getBodyPart(0);
        InputStream is = part.getInputStream();

        Assert.assertEquals("Wrong count of parts of response", 3, part.getSize());

        char[] output = new char[3];
        output[0] = (char) is.read();
        output[1] = (char) is.read();
        output[2] = (char) is.read();
        String str = new String(output);
        Assert.assertEquals("Wrong content of first part of response", "bla", str);
    }
}
