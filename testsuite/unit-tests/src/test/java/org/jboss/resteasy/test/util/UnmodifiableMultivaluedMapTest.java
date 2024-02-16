/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.test.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.specimpl.UnmodifiableMultivaluedMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for UnmodifiableMultivaluedMap
 * @tpSince RESTEasy
 * @author Nicolas NESMON
 */
public class UnmodifiableMultivaluedMapTest {

    @Test
    public void testEagerlyCreatedNotModifiable() {
        MultivaluedMap<String, String> modifiableMultivaluedMap = new MultivaluedHashMap<>();
        modifiableMultivaluedMap.addAll("Hello", "Bonjour");

        UnmodifiableMultivaluedMap<String, String> unmodifiableMultivaluedMap = new UnmodifiableMultivaluedMap<>(
                modifiableMultivaluedMap);

        doTest(unmodifiableMultivaluedMap);
    }

    @Test
    public void testLazilyCreatedNotModifiable() {
        MultivaluedMap<String, String> modifiableMultivaluedMap = new MultivaluedHashMap<>();
        modifiableMultivaluedMap.addAll("Hello", "Bonjour");

        UnmodifiableMultivaluedMap<String, String> unmodifiableMultivaluedMap = new UnmodifiableMultivaluedMap<>(
                modifiableMultivaluedMap, false);

        doTest(unmodifiableMultivaluedMap);
    }

    private void doTest(UnmodifiableMultivaluedMap<String, String> unmodifiableMultivaluedMap) {
        try {
            unmodifiableMultivaluedMap.add("Forbidden", "Interdit");
            Assertions.fail("Add() must not be supported on an unmodifiable multi valued map");
        } catch (UnsupportedOperationException e) {
        }

        try {
            unmodifiableMultivaluedMap.addAll("Forbidden", Arrays.asList("Interdit"));
            Assertions.fail("addAll() must not be supported on an unmodifiable multi valued Map");
        } catch (UnsupportedOperationException e) {
        }

        try {
            unmodifiableMultivaluedMap.addAll("Forbidden", "Interdit");
            Assertions.fail("addAll() must not be supported on an unmodifiable multi valued Map");
        } catch (UnsupportedOperationException e) {
        }

        try {
            unmodifiableMultivaluedMap.addFirst("Forbidden", "Interdit");
            Assertions.fail("addFirst() must not be supported on an unmodifiable multi valued Map");
        } catch (UnsupportedOperationException e) {
        }

        try {
            unmodifiableMultivaluedMap.clear();
            Assertions.fail("clear() must not be supported on an unmodifiable multi valued Map");
        } catch (UnsupportedOperationException e) {
        }

        try {
            unmodifiableMultivaluedMap.remove("Hello");
            Assertions.fail("remove() must not be supported on an unmodifiable multi valued Map");
        } catch (UnsupportedOperationException e) {
        }

        try {
            unmodifiableMultivaluedMap.put("Forbidden", Arrays.asList("Interdit"));
            Assertions.fail("put() must not be supported on an unmodifiable multi valued Map");
        } catch (UnsupportedOperationException e) {
        }

        try {
            unmodifiableMultivaluedMap.putSingle("Forbidden", "Interdit");
            Assertions.fail("putSingle() must not be supported on an unmodifiable multi valued Map");
        } catch (UnsupportedOperationException e) {
        }

        try {
            unmodifiableMultivaluedMap.putAll(Collections.singletonMap("Forbidden", Arrays.asList("Interdit")));
            Assertions.fail("putAll() must not be supported on an unmodifiable multi valued Map");
        } catch (UnsupportedOperationException e) {
        }

        try {
            unmodifiableMultivaluedMap.keySet().add("Forbidden");
            Assertions.fail("keySet() must return an unmodifiable Set");
        } catch (UnsupportedOperationException e) {
        }

        try {
            unmodifiableMultivaluedMap.values().add(Arrays.asList("Interdit"));
            Assertions.fail("values() must return an unmodifiable Collection");
        } catch (UnsupportedOperationException e) {
        }

        try {
            unmodifiableMultivaluedMap.get("Hello").add("Interdit");
            Assertions.fail("get() must return an unmodifiable List");
        } catch (UnsupportedOperationException e) {
        }

        try {
            unmodifiableMultivaluedMap.entrySet().clear();
            Assertions.fail("entrySet() must return an unmodifiable Set");
        } catch (UnsupportedOperationException e) {
        }

        try {
            for (Entry<String, List<String>> entry : unmodifiableMultivaluedMap.entrySet()) {
                entry.setValue(Arrays.asList("Interdit"));
            }
            Assertions.fail("entry must be unmodifiable");
        } catch (UnsupportedOperationException e) {
        }

        try {
            for (Entry<String, List<String>> entry : unmodifiableMultivaluedMap.entrySet()) {
                entry.getValue().add("Interdit");
            }
            Assertions.fail("entry must be unmodifiable");
        } catch (UnsupportedOperationException e) {
        }
    }

}
