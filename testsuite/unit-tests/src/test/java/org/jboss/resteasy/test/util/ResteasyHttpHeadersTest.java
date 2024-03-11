/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.test.util;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for UnmodifiableMultivaluedMap
 * @tpSince RESTEasy
 * @author Nicolas NESMON
 */
public class ResteasyHttpHeadersTest {

    @Test
    public void testNotModifiable() {
        MultivaluedMap<String, String> modifiableMultivaluedMap = new MultivaluedHashMap<>();
        modifiableMultivaluedMap.addAll("Hello", "Bonjour");

        ResteasyHttpHeaders httpHeaders = new ResteasyHttpHeaders(modifiableMultivaluedMap);
        try {
            httpHeaders.getRequestHeader("Hello").add("Interdit");
            Assertions.fail("getRequestHeader() must return a read-only List");
        } catch (UnsupportedOperationException e) {
        }
        try {
            httpHeaders.getRequestHeaders().clear();
            Assertions.fail("getRequestHeaders() must return a read-only Map");
        } catch (UnsupportedOperationException e) {
        }
    }

}
