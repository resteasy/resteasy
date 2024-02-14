package org.jboss.resteasy.test.form;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.form.resource.FormUrlEncodedCharsetResource;
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
 * @tpSubChapter Form tests
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for JBEAP-4693
 * @tpSince RESTEasy 3.0.17
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class FormUrlEncodedCharsetTest {
    protected static MediaType testMediaType8 = MediaType.APPLICATION_FORM_URLENCODED_TYPE
            .withCharset(StandardCharsets.UTF_8.displayName());
    protected static MediaType testMediaType16 = MediaType.APPLICATION_FORM_URLENCODED_TYPE
            .withCharset(StandardCharsets.UTF_16.displayName());
    protected static String alephBetGimel = "אבג";

    private static Client client;
    private static WebTarget target;

    protected static final Logger logger = Logger.getLogger(FormUrlEncodedCharsetTest.class.getName());

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(FormUrlEncodedCharsetTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, FormUrlEncodedCharsetResource.class);
    }

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
        target = client.target(PortProviderUtil.generateURL("/test", FormUrlEncodedCharsetTest.class.getSimpleName()));
    }

    @AfterAll
    public static void end() {
        client.close();
    }

    /**
     * @tpTestDetails Test for default charset.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testFormDefault() throws UnsupportedEncodingException {
        Form form = new Form();
        form.param("name", alephBetGimel);
        Entity<Form> entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        Response response = target.request().post(entity);
        String result = response.readEntity(String.class);
        logger.info("result: " + result);
        Assertions.assertEquals(result, alephBetGimel, "EAP is unable to encode default charset");
    }

    /**
     * @tpTestDetails Test for UTF8 charset.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testFormUTF8() throws UnsupportedEncodingException {
        Form form = new Form();
        form.param("name", alephBetGimel);
        Entity<Form> entity = Entity.entity(form, testMediaType8);
        Response response = target.request().post(entity);
        String result = response.readEntity(String.class);
        logger.info("result: " + result);
        Assertions.assertEquals(result, alephBetGimel, "EAP is unable to encode UTF8 charset");
    }

    /**
     * @tpTestDetails Test for UTF16 charset.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testFormUTF16() throws UnsupportedEncodingException {
        Form form = new Form();
        form.param("name", alephBetGimel);
        Entity<Form> entity = Entity.entity(form, testMediaType16);
        Response response = target.request().post(entity);
        String result = response.readEntity(String.class);
        logger.info("result: " + result);
        Assertions.assertEquals(result, alephBetGimel, "EAP is unable to encode UTF16 charset");
    }
}
