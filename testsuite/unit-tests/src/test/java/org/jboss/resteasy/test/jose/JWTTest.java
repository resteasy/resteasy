package org.jboss.resteasy.test.jose;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.jwt.JsonSerialization;
import org.jboss.resteasy.jwt.JsonWebToken;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;


/**
 * @tpSubChapter Jose tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for JWT
 * @tpSince RESTEasy 3.0.16
 */
public class JWTTest {
    protected static final Logger logger = LogManager.getLogger(JWTTest.class.getName());
    private static final String ERROR_MSG = "Wrong JsonWebToken conversion";

    /**
     * @tpTestDetails JsonWebToken test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJWT() throws Exception {
        JsonWebToken token = new JsonWebToken().id("123");
        String json = JsonSerialization.toString(token, true);
        logger.info(String.format("JSON: %s", json));

        Assert.assertThat(ERROR_MSG, json, containsString("jti"));
        Assert.assertThat(ERROR_MSG, json, containsString("123"));
        token = JsonSerialization.fromString(JsonWebToken.class, json);
        logger.info(String.format("id: %s", token.getId()));
        Assert.assertThat(ERROR_MSG, token.getId(), is("123"));
    }
}
