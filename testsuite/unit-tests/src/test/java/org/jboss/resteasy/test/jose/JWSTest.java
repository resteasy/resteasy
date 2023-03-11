package org.jboss.resteasy.test.jose;

import static org.hamcrest.CoreMatchers.containsString;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import jakarta.ws.rs.core.MediaType;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.jboss.logging.Logger;
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @tpSubChapter Jose tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for JWS
 * @tpSince RESTEasy 3.0.16
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JWSTest {
    protected static final Logger logger = Logger.getLogger(JWSTest.class.getName());
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
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        JWSHeader header = new JWSHeader(Algorithm.HS256, null, null);
        String val = header.toString();
        logger.info(val);
        MatcherAssert.assertThat(HEADER_ERROR_MSG, val, containsString("alg"));
        MatcherAssert.assertThat(HEADER_ERROR_MSG, val, containsString("HS256"));
        header = mapper.readValue(val, JWSHeader.class);
        val = mapper.writeValueAsString(header);
        logger.info(val);
        MatcherAssert.assertThat(HEADER_ERROR_MSG, val, containsString("alg"));
        MatcherAssert.assertThat(HEADER_ERROR_MSG, val, containsString("HS256"));
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
     * SseSmokeTest
     *
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

    @Test
    public void testJWT() throws Exception {
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        StringWriter writer = new StringWriter();
        try (PemWriter pemWriter = new PemWriter(writer)) {
            pemWriter.writeObject(new PemObject("RSA PUBLIC KEY", keyPair.getPublic().getEncoded()));
            pemWriter.flush();
        }
        logger.info(writer.toString());

        String content = "{\"sub\": \"1234567890\",\"name\": \"John Doe\",\"iat\": 1516239022}";
        String encoded = new JWSBuilder()
                .type("JWT")
                .content(content.getBytes(StandardCharsets.UTF_8))
                .rsa256(keyPair.getPrivate());

        logger.info(encoded);
        MatcherAssert.assertThat(encoded, CoreMatchers.not(CoreMatchers.containsString("=")));

        JWSInput input = new JWSInput(encoded, ResteasyProviderFactory.getInstance());
        String msg = (String) input.readContent(String.class, null, null, MediaType.TEXT_PLAIN_TYPE);
        Assert.assertEquals(RESPONSE_ERROR_MSG, content, msg);
        Assert.assertTrue(VERIFY_ERROR_MSG, RSAProvider.verify(input, keyPair.getPublic()));
    }
}
