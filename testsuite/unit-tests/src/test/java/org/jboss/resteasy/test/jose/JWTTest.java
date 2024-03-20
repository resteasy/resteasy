package org.jboss.resteasy.test.jose;

import org.jboss.logging.Logger;
import org.jboss.resteasy.jwt.JsonSerialization;
import org.jboss.resteasy.jwt.JsonWebToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Jose tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for JWT
 * @tpSince RESTEasy 3.0.16
 */
public class JWTTest {
    protected static final Logger logger = Logger.getLogger(JWTTest.class.getName());
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

        Assertions.assertTrue(json.contains("jti"), ERROR_MSG);
        Assertions.assertTrue(json.contains("123"), ERROR_MSG);
        token = JsonSerialization.fromString(JsonWebToken.class, json);
        logger.info(String.format("id: %s", token.getId()));
        Assertions.assertEquals(token.getId(), "123", ERROR_MSG);
    }
}
