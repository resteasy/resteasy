package org.jboss.resteasy.test.crypto;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.core.GenericType;

import org.jboss.logging.Logger;
import org.jboss.resteasy.security.smime.PKCS7SignatureInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Crypto
 * @tpChapter Unit tests
 * @tpTestCaseDetails Regression test for JBEAP-1795
 * @tpSince RESTEasy 3.0.16
 */
public class GenericTypeCryptoTest {
    protected static final Logger logger = Logger.getLogger(GenericTypeCryptoTest.class.getName());

    /**
     * @tpTestDetails Check GenerycType class on ArrayList
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGenericType() throws Exception {
        GenericType<List<String>> stringListType = new GenericType<List<String>>() {
        };
        logger.info("type: " + stringListType.getType());
        logger.info("raw type: " + stringListType.getRawType());
        PKCS7SignatureInput<List<String>> input = new PKCS7SignatureInput<>();
        input.setType(stringListType);
        Field field = PKCS7SignatureInput.class.getDeclaredField("entity");
        field.setAccessible(true);
        List<String> list = new ArrayList<>();
        list.add("abc");
        field.set(input, list);
        List<String> list2 = input.getEntity(stringListType, null);
        logger.info("list2: " + list2);
        Assertions.assertEquals(list, list2, "Wrong decryption and encryption of list");
    }
}
