package org.jboss.resteasy.test.providers.multipart;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.ReflectPermission;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.activation.DataHandler;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedOutput;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.multipart.resource.MimeMultipartProviderClient;
import org.jboss.resteasy.test.providers.multipart.resource.MimeMultipartProviderCustomer;
import org.jboss.resteasy.test.providers.multipart.resource.MimeMultipartProviderResource;
import org.jboss.resteasy.utils.PermissionUtil;
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
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MimeMultipartProviderTest {

    private static Logger logger = Logger.getLogger(MimeMultipartProviderTest.class);
    private static final String TEST_URI = generateURL("/mime");
    static Client client;

    private static final String ERR_NUMBER = "The number of enclosed bodypart objects doesn't match to the expectation";

    @BeforeAll
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    static final String testFilePath = TestUtil.getResourcePath(MimeMultipartProviderTest.class,
            "HeaderFlushedOutputStreamTestData.txt");

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(MimeMultipartProviderTest.class.getSimpleName());
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                // Can be removed when WFLY-17061 is resolved
                PermissionUtil.addModuleFilePermission("org.eclipse.angus.activation", "org.eclipse.angus.mail"),
                // Can be removed when WFLY-17061 is resolved
                new RuntimePermission("getClassLoader"),
                // Can be removed when WFLY-17061 is resolved
                new RuntimePermission("accessDeclaredMembers"),
                new ReflectPermission("suppressAccessChecks")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, MimeMultipartProviderResource.class,
                MimeMultipartProviderCustomer.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, MimeMultipartProviderTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails MultipartFormDataOutput entity in put request with data from file is used
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPutForm() throws Exception {
        // prepare file
        File file = new File(testFilePath);
        Assertions.assertTrue(file.exists(), "File " + testFilePath + " doesn't exists");

        MultipartFormDataOutput mpfdo = new MultipartFormDataOutput();
        mpfdo.addFormData("part1", "This is Value 1", MediaType.TEXT_PLAIN_TYPE);
        mpfdo.addFormData("part2", "This is Value 2", MediaType.TEXT_PLAIN_TYPE);
        mpfdo.addFormData("data.txt", file, MediaType.TEXT_PLAIN_TYPE);

        Response response = client.target(TEST_URI).request()
                .put(Entity.entity(mpfdo, MediaType.MULTIPART_FORM_DATA_TYPE));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String responseBody = response.readEntity(String.class);
        Assertions.assertEquals(responseBody, "Count: 3", ERR_NUMBER);
    }

    /**
     * @tpTestDetails MultipartOutput entity in put request with data from file is used
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPut() throws Exception {
        // prepare file
        File file = new File(testFilePath);
        Assertions.assertTrue(file.exists(), "File " + testFilePath + " doesn't exists");

        MultipartOutput mpo = new MultipartOutput();
        mpo.addPart("This is Value 1", MediaType.TEXT_PLAIN_TYPE);
        mpo.addPart("This is Value 2", MediaType.TEXT_PLAIN_TYPE);
        mpo.addPart(file, MediaType.TEXT_PLAIN_TYPE);

        Response response = client.target(TEST_URI).request()
                .put(Entity.entity(mpo, MediaType.MULTIPART_FORM_DATA_TYPE));

        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String responseBody = response.readEntity(String.class);
        Assertions.assertEquals(responseBody, "Count: 3", ERR_NUMBER);
    }

    /**
     * @tpTestDetails MultipartFormDataOutput entity in put request with created by jxb marshaller is used
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testForm() throws Exception {
        testMultipart(TEST_URI + "/form");
        testMultipart(TEST_URI + "/form/map");
        testMultipart(TEST_URI + "/form/class");
        testMultipart(TEST_URI + "/multi");
        testMultipart(TEST_URI + "/multi/list");
    }

    private void testMultipart(String uri) throws Exception {
        MultipartFormDataOutput mpfdo = new MultipartFormDataOutput();
        mpfdo.addFormData("bill", createCustomerData("bill"), MediaType.APPLICATION_XML_TYPE);
        mpfdo.addFormData("monica", createCustomerData("monica"), MediaType.APPLICATION_XML_TYPE);

        Response response = client.target(uri).request()
                .put(Entity.entity(mpfdo, MediaType.MULTIPART_FORM_DATA_TYPE));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails MultipartOutput entity in put request with manually created jaxb objects is used
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMultipartOutput() throws Exception {
        MimeMultipartProviderClient proxy = ProxyBuilder
                .builder(MimeMultipartProviderClient.class, client.target(generateURL(""))).build();
        MultipartOutput output = new MultipartOutput();
        output.addPart(new MimeMultipartProviderCustomer("bill"), MediaType.APPLICATION_XML_TYPE);
        output.addPart(new MimeMultipartProviderCustomer("monica"), MediaType.APPLICATION_XML_TYPE);
        proxy.putMixed(output);
    }

    /**
     * @tpTestDetails MultipartFormDataOutput entity in put request with manually created jaxb objects is used
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMultipartFormDataOutput() throws Exception {
        MimeMultipartProviderClient proxy = ProxyBuilder
                .builder(MimeMultipartProviderClient.class, client.target(generateURL(""))).build();
        MultipartFormDataOutput output = new MultipartFormDataOutput();
        output.addFormData("bill", new MimeMultipartProviderCustomer("bill"), MediaType.APPLICATION_XML_TYPE);
        output.addFormData("monica", new MimeMultipartProviderCustomer("monica"), MediaType.APPLICATION_XML_TYPE);
        proxy.putFormData(output);
    }

    /**
     * @tpTestDetails MultipartRelatedOutput entity in put request with manually generated xml is used
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMultipartRelatedOutput() throws Exception {
        MimeMultipartProviderClient proxy = ProxyBuilder
                .builder(MimeMultipartProviderClient.class, client.target(generateURL(""))).build();
        MultipartRelatedOutput output = new MultipartRelatedOutput();
        output.setStartInfo("text/xml");

        Map<String, String> mediaTypeParameters = new LinkedHashMap<String, String>();
        mediaTypeParameters.put("charset", StandardCharsets.UTF_8.name());
        mediaTypeParameters.put("type", "text/xml");
        output
                .addPart(
                        "<m:data xmlns:m='http://example.org/stuff'>"
                                + "<m:photo><xop:Include xmlns:xop='http://www.w3.org/2004/08/xop/include' href='cid:http://example.org/me.png'/></m:photo>"
                                + "<m:sig><xop:Include xmlns:xop='http://www.w3.org/2004/08/xop/include' href='cid:http://example.org/my.hsh'/></m:sig>"
                                + "</m:data>",
                        new MediaType("application",
                                "xop+xml", mediaTypeParameters),
                        "<mymessage.xml@example.org>", "8bit");
        output.addPart("// binary octets for png",
                new MediaType("image", "png"), "<http://example.org/me.png>",
                "binary");
        output.addPart("// binary octets for signature", new MediaType(
                "application", "pkcs7-signature"),
                "<http://example.org/me.hsh>", "binary");
        proxy.putRelated(output);
    }

    /**
     * @tpTestDetails List is send in put request with the @PartType annotation
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMultipartList() throws Exception {
        MimeMultipartProviderClient proxy = ProxyBuilder
                .builder(MimeMultipartProviderClient.class, client.target(generateURL(""))).build();
        ArrayList<MimeMultipartProviderCustomer> mimeMultipartProviderCustomers = new ArrayList<MimeMultipartProviderCustomer>();
        mimeMultipartProviderCustomers.add(new MimeMultipartProviderCustomer("bill"));
        mimeMultipartProviderCustomers.add(new MimeMultipartProviderCustomer("monica"));
        proxy.putMixedList(mimeMultipartProviderCustomers);
    }

    /**
     * @tpTestDetails Map is send in put request with the @PartType annotation
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMultipartMap() throws Exception {
        MimeMultipartProviderClient proxy = ProxyBuilder
                .builder(MimeMultipartProviderClient.class, client.target(generateURL(""))).build();
        LinkedHashMap<String, MimeMultipartProviderCustomer> customers = new LinkedHashMap<String, MimeMultipartProviderCustomer>();
        customers.put("bill", new MimeMultipartProviderCustomer("bill"));
        customers.put("monica", new MimeMultipartProviderCustomer("monica"));
        proxy.putFormDataMap(customers);
    }

    /**
     * @tpTestDetails Custom Form type in put request with @MultipartForm annotation
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMultipartForm() throws Exception {
        MimeMultipartProviderClient proxy = ProxyBuilder
                .builder(MimeMultipartProviderClient.class, client.target(generateURL(""))).build();
        MimeMultipartProviderResource.Form form = new MimeMultipartProviderResource.Form(
                new MimeMultipartProviderCustomer("bill"), new MimeMultipartProviderCustomer("monica"));
        proxy.putFormDataMap(form);
    }

    /**
     * @tpTestDetails Custom jaxb object in put request with @XopWithMultipartRelated
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXop() throws Exception {
        MimeMultipartProviderClient proxy = ProxyBuilder
                .builder(MimeMultipartProviderClient.class, client.target(generateURL(""))).build();
        MimeMultipartProviderResource.Xop xop = new MimeMultipartProviderResource.Xop(
                new MimeMultipartProviderCustomer("bill\u00E9"), new MimeMultipartProviderCustomer("monica"),
                "Hello Xop World!".getBytes(StandardCharsets.UTF_8), new DataHandler(
                        new ByteArrayDataSource("Hello Xop World!"
                                .getBytes(StandardCharsets.UTF_8),
                                MediaType.APPLICATION_OCTET_STREAM)));
        proxy.putXop(xop);
    }

    private String createCustomerData(String name) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(MimeMultipartProviderCustomer.class);
        StringWriter writer = new StringWriter();
        context.createMarshaller().marshal(new MimeMultipartProviderCustomer(name), writer);
        String data = writer.toString();
        return data;
    }

    /**
     * @tpTestDetails Client sends get request for InputStream from the server
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGet() throws Exception {
        Response response = client.target(TEST_URI).request().get();
        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        BufferedInputStream in = new BufferedInputStream(response.readEntity(InputStream.class));
        String contentType = response.getStringHeaders().getFirst("content-type");
        ByteArrayDataSource ds = new ByteArrayDataSource(in, contentType);
        MimeMultipart mimeMultipart = new MimeMultipart(ds);
        Assertions.assertEquals(mimeMultipart.getCount(), 2, ERR_NUMBER);
        response.close();
    }

    /**
     * @tpTestDetails Client sends post request with "multipart/form-data" and boundary definition
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFile() throws Exception {
        Response response = client.target(TEST_URI + "/file/test").request()
                .post(Entity.entity(form,
                        "multipart/form-data; boundary=---------------------------52524491016334132001492192799"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }

    private static final String form = "-----------------------------52524491016334132001492192799\r\n"
            + "Content-Disposition: form-data; name=\"submit-name\"\r\n"
            + "\r\n"
            + "Bill\r\n"
            + "-----------------------------52524491016334132001492192799\r\n"
            + "Content-Disposition: form-data; name=\"files\"; filename=\"stuff.txt\"\r\n"
            + "Content-Type: text/plain\r\n"
            + "\r\n"
            + "hello world\r\n"
            + "\r\n"
            + "-----------------------------52524491016334132001492192799--";
}
