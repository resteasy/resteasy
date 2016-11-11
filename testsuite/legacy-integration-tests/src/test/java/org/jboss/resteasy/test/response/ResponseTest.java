package org.jboss.resteasy.test.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.response.resource.ResponseAnnotatedClass;
import org.jboss.resteasy.test.response.resource.ResponseDateReaderWriter;
import org.jboss.resteasy.test.response.resource.ResponseResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResponseTest {

    protected static final Logger logger = LogManager.getLogger(VariantsTest.class.getName());

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    static Client client;

    @BeforeClass
    public static void setup() throws Exception {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResponseTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ResponseResource.class);
    }

    @AfterClass
    public static void cleanup() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResponseTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends HEAD request. The resource returns response with entity but the resulting response
     * must not contain entity as per HEAD method HTTP1.1 spec.
     * @tpPassCrit Response doesn't contain entity
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testHead() {
        Response response = client.target(generateURL("/head")).request().head();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        thrown.expect(ProcessingException.class);
        response.readEntity(String.class);
        response.close();
    }

    /**
     * @tpTestDetails Client sends HEAD request. The resource returns empty response.
     * @tpPassCrit The resulting response must not contain entity as per HEAD method HTTP1.1 spec.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmpty() {
        Response response = client.target(generateURL("/empty")).request().head();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertFalse(response.hasEntity());
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request. The resource creates response from RuntimeDelegate ResponseBuilder.
     * @tpPassCrit The response code status is changed to 200 (SUCCESS) and response contains the correct entity
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNoStatus() {
        Response response = client.target(generateURL("/entitybodyresponsetest")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("", "hello", response.readEntity(String.class));
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request. The resource creates response from RuntimeDelegate ResponseBuilder with
     * empty entity.
     * @tpPassCrit The response code status is 204 (NO CONTENT)
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNullEntityNoStatus() {
        Response response = client.target(generateURL("/nullEntityResponse")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
    }


    /**
     * @tpTestDetails Client sends POST request, The resource creates link and attaches it to the Response.
     * @tpPassCrit The response contains the link
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void hasLinkWhenLinkTest() {
        Response response = client.target(generateURL("/link")).request().post(Entity.text("path"));
        Assert.assertTrue(response.hasLink("path"));
        response.close();
    }


    protected String readLine(Reader reader) throws IOException {
        String line = null;
        BufferedReader buffered = new BufferedReader(reader);
        try {
            line = buffered.readLine();
        } catch (IOException e) {
            buffered.close();
            throw e;
        }
        return line;
    }

    protected <T> GenericType<T> generic(Class<T> clazz) {
        return new GenericType<T>(clazz);
    }

    /**
     * @tpTestDetails Client sends registers DateReaderWriter and sends get request. The resource sends the date back
     * in the response.
     * @tpPassCrit The ANNOTATIONS fields are set up correctly in the DateReaderWriter after processing the response and
     * the correct date is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void readEntityGenericTypeAnnotationTest() {
        Date date = Calendar.getInstance().getTime();
        String sDate = String.valueOf(date.getTime());
        Annotation[] annotations = ResponseAnnotatedClass.class.getAnnotations();
        int expected = ResponseDateReaderWriter.ANNOTATION_CONSUMES
                | ResponseDateReaderWriter.ANNOTATION_PROVIDER;

        AtomicInteger ai = new AtomicInteger();
        ResponseDateReaderWriter drw = new ResponseDateReaderWriter(ai);

        Response response = client.target(generateURL("/date")).register(drw).queryParam("date", sDate).request().get();
        response.bufferEntity();

        Date entity = response.readEntity(generic(Date.class), annotations);
        logger.info(entity.toString());
        Assert.assertTrue("The original date doesn't match to the returned entity", date.equals(entity));

        Assert.assertTrue("The AtomicInteger in the DateReaderWriter doesn't match with the original value",
                ai.get() == expected);

        String responseDate = response.readEntity(generic(String.class),
                annotations);
        Assert.assertTrue("The original string date doesn't match to generic entity extracted from the response",
                sDate.equals(responseDate));

        Assert.assertTrue("The AtomicInteger in the DateReaderWriter doesn't match with the original value",
                ai.get() == expected);
        response.close();

    }

    /**
     * @tpTestDetails Client sends GET request. The returned Response contains string entity, which is buffered and
     * read multiple times as generic entity
     * @tpPassCrit The response code status is changed to 200 (SUCCESS) and the entity can be read with generic Reader
     * class and can be stored directly as array of bytes
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void readEntityGenericTypeTest() throws Exception {
        Response response = client.target(generateURL("/entity")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.bufferEntity();
        String line;

        Reader reader = response.readEntity(new GenericType<Reader>(Reader.class));
        line = readLine(reader);
        Assert.assertTrue("The entity extracted with genetic Reader doesn't match the original entity",
                ResponseResource.ENTITY.equals(line));
        byte[] buffer = new byte[0];
        buffer = response.readEntity(generic(buffer.getClass()));
        Assert.assertNotNull(buffer);
        line = new String(buffer);
        Assert.assertTrue("The entity extracted with as array of bytes doesn't match the original entity",
                ResponseResource.ENTITY.equals(line));
        response.close();
    }
}
