package org.jboss.resteasy.test.providers.jackson2.whitelist;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.providers.jackson.WhiteListPolymorphicTypeValidatorBuilder;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.jackson2.whitelist.model.AbstractVehicle;
import org.jboss.resteasy.test.providers.jackson2.whitelist.model.TestPolymorphicType;
import org.jboss.resteasy.test.providers.jackson2.whitelist.model.air.Aircraft;
import org.jboss.resteasy.test.providers.jackson2.whitelist.model.land.Automobile;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.5.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class WhiteListPolymorphicTypeValidatorTest {

    protected static final Logger logger = Logger.getLogger(WhiteListPolymorphicTypeValidatorTest.class.getName());

    static ResteasyClient client;

    @Deployment(name = "default")
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(WhiteListPolymorphicTypeValidatorTest.class.getSimpleName());
        war.addClass(WhiteListPolymorphicTypeValidatorTest.class);
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put("resteasy.jackson.deserialization.whitelist.allowIfBaseType.prefix",
                Automobile.class.getPackage().getName());
        contextParam.put("resteasy.jackson.deserialization.whitelist.allowIfSubType.prefix",
                Automobile.class.getPackage().getName());
        return TestUtil.finishContainerPrepare(war, contextParam, JaxRsActivator.class, TestRESTService.class,
                TestPolymorphicType.class, AbstractVehicle.class, Automobile.class, Aircraft.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, WhiteListPolymorphicTypeValidatorTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends POST request with polymorphic type enabled by configuration of
     *                {@link WhiteListPolymorphicTypeValidatorBuilder}.
     * @tpPassCrit The resource returns successfully, deserialization passed.
     * @tpSince RESTEasy 4.5.0
     */
    @Test
    public void testGood() throws Exception {
        String response = sendPost(new TestPolymorphicType(new Automobile()));
        logger.info("response: " + response);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.contains("Response code: " + HttpResponseCodes.SC_CREATED));
        Assertions.assertTrue(response.contains("Created"));
    }

    /**
     * @tpTestDetails Client sends POST request with polymorphic type not enabled by configuration of
     *                {@link WhiteListPolymorphicTypeValidatorBuilder}.
     * @tpPassCrit The resource returns HttpResponseCodes.SC_BAD_REQUEST, deserialization failed with 'PolymorphicTypeValidator
     *             denied resolution'.
     * @tpSince RESTEasy 4.5.0
     */
    @Test
    @RequiresModule(value = "org.jboss.resteasy.resteasy-core", minVersion = "6.2.8.Final", issueRef = "RESTEASY-3443")
    public void testBad() throws Exception {
        String response = sendPost(new TestPolymorphicType(new Aircraft()));
        logger.info("response: " + response);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.contains("Response code: " + HttpResponseCodes.SC_BAD_REQUEST));
        Assertions.assertTrue(response.contains("Not able to deserialize data provided"),
                "Expected response to contain \"Not able to deserialize data provided\" but was \"" + response + "\"");
    }

    private String createJSONString(TestPolymorphicType t) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(t);
    }

    private String sendPost(TestPolymorphicType t) throws Exception {

        logger.info("Creating JSON test data");
        String jsonData = createJSONString(t);

        logger.info("jsonData: " + jsonData);

        String urlString = generateURL("/test/post");
        logger.info("POST data to : " + urlString);
        URL url = new URL(urlString);
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST"); // PUT is another valid option
        http.setDoOutput(true);

        byte[] out = jsonData.getBytes(StandardCharsets.UTF_8);

        http.setFixedLengthStreamingMode(out.length);
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        }

        InputStream is = null;
        if (http.getResponseCode() != 200) {
            is = http.getErrorStream();
        } else {
            /* error from server */
            is = http.getInputStream();
        }

        String result = is == null ? ""
                : new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
        String response = String.format("Response code: %s response message: %s  %s", http.getResponseCode(),
                http.getResponseMessage(), result);

        logger.info("Response: " + response);

        return response;
    }

}
