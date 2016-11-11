package org.jboss.resteasy.test.jose;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.jboss.resteasy.jose.jws.Algorithm;
import org.jboss.resteasy.jose.jws.JWSBuilder;
import org.jboss.resteasy.jose.jws.JWSHeader;
import org.jboss.resteasy.jose.jws.JWSInput;
import org.jboss.resteasy.jose.jws.crypto.HMACProvider;
import org.jboss.resteasy.jose.jws.crypto.RSAProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.ws.rs.core.MediaType;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import static org.hamcrest.CoreMatchers.containsString;

/**
 * @tpSubChapter Jose tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for JWS
 * @tpSince RESTEasy 3.0.16
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JWSTest {
    protected static final Logger logger = LogManager.getLogger(JWSTest.class.getName());
    private static final String HEADER_ERROR_MSG = "Wrong algorithm header";
    private static final String RESPONSE_ERROR_MSG = "Response contains wrong content";
    private static final String VERIFY_ERROR_MSG = "Wrong verification";

    /**
     * @tpTestDetails Header serialization test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testHeaderSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
        JWSHeader header = new JWSHeader(Algorithm.HS256, null, null);
        String val = header.toString();
        logger.info(val);
        Assert.assertThat(HEADER_ERROR_MSG, val, containsString("alg"));
        Assert.assertThat(HEADER_ERROR_MSG, val, containsString("HS256"));
        header = mapper.readValue(val, JWSHeader.class);
        val = mapper.writeValueAsString(header);
        logger.info(val);
        Assert.assertThat(HEADER_ERROR_MSG, val, containsString("alg"));
        Assert.assertThat(HEADER_ERROR_MSG, val, containsString("HS256"));
    }

    /**
     * @tpTestDetails Basic RSA test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRSA() throws Exception {
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();

        String encoded = new JWSBuilder()
                .content("Hello World".getBytes(StandardCharsets.UTF_8))
                .rsa256(keyPair.getPrivate());

        logger.info(encoded);

        JWSInput input = new JWSInput(encoded, ResteasyProviderFactory.getInstance());
        String msg = (String) input.readContent(String.class, null, null, MediaType.TEXT_PLAIN_TYPE);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "Hello World", msg);
        Assert.assertTrue(VERIFY_ERROR_MSG, RSAProvider.verify(input, keyPair.getPublic()));

    }

    /**
     * @tpTestDetails RSA with content type test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRSAWithContentType() throws Exception {
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();

        String encoded = new JWSBuilder()
                .contentType(MediaType.TEXT_PLAIN_TYPE)
                .content("Hello World", MediaType.TEXT_PLAIN_TYPE)
                .rsa256(keyPair.getPrivate());

        logger.info(encoded);

        JWSInput input = new JWSInput(encoded, ResteasyProviderFactory.getInstance());
        logger.info(input.getHeader());
        String msg = input.readContent(String.class);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "Hello World", msg);
        Assert.assertTrue(VERIFY_ERROR_MSG, RSAProvider.verify(input, keyPair.getPublic()));

    }

    /**
     * @tpTestDetails HMAC with ContentType test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testHMACWithContentType() throws Exception {
        SecretKey key = KeyGenerator.getInstance(HMACProvider.getJavaAlgorithm(Algorithm.HS256)).generateKey();

        String encoded = new JWSBuilder()
                .contentType(MediaType.TEXT_PLAIN_TYPE)
                .content("Hello World", MediaType.TEXT_PLAIN_TYPE)
                .hmac256(key);

        logger.info(encoded);

        JWSInput input = new JWSInput(encoded, ResteasyProviderFactory.getInstance());
        logger.info(input.getHeader());
        String msg = input.readContent(String.class);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "Hello World", msg);
        Assert.assertTrue(VERIFY_ERROR_MSG, HMACProvider.verify(input, key));
    }
}
