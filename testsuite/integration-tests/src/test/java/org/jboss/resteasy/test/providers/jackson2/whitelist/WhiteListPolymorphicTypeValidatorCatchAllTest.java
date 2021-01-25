package org.jboss.resteasy.test.providers.jackson2.whitelist;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.ClientBuilder;
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


/**
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.5.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class WhiteListPolymorphicTypeValidatorCatchAllTest {

    protected static final Logger logger = Logger.getLogger(WhiteListPolymorphicTypeValidatorCatchAllTest.class.getName());

    static ResteasyClient client;

    @Deployment(name = "default")
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(WhiteListPolymorphicTypeValidatorCatchAllTest.class.getSimpleName());
        war.addClass(WhiteListPolymorphicTypeValidatorCatchAllTest.class);
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put("resteasy.jackson.deserialization.whitelist.allowIfSubType.prefix", "*");
        return TestUtil.finishContainerPrepare(war, contextParam, JaxRsActivator.class, TestRESTService.class,
                TestPolymorphicType.class, AbstractVehicle.class, Automobile.class, Aircraft.class);
    }

    @Before
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, WhiteListPolymorphicTypeValidatorCatchAllTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends POST request with polymorphic type enabled by configuration of {@link WhiteListPolymorphicTypeValidatorBuilder}.
     * @tpPassCrit The resource returns successfully, deserialization passed.
     * @tpSince RESTEasy 4.5.0
     */
    @Test
    public void testAutomobile() throws Exception {
        String response = sendPost(new TestPolymorphicType(new Automobile()));
        logger.info("response: " + response);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.contains("Response code: " + HttpResponseCodes.SC_CREATED));
        Assert.assertTrue(response.contains("Created"));
    }

    @Test
    public void testAircraft() throws Exception {
        String response = sendPost(new TestPolymorphicType(new Aircraft()));
        logger.info("response: " + response);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.contains("Response code: " + HttpResponseCodes.SC_CREATED));
        Assert.assertTrue(response.contains("Created"));
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

        String result = is == null ? "" : new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
        String response = String.format("Response code: %s response message: %s  %s", http.getResponseCode(), http.getResponseMessage(), result);

        logger.info("Response: " + response);

        return response;
    }

}
