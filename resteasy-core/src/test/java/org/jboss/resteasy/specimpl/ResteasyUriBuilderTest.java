/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2023 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.specimpl;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.core.UriBuilder;

import org.jboss.resteasy.specimpl.ResteasyUriBuilderImpl.PathSegments;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyUriBuilderTest {
    private static final String PATH_SEGMENT_PARSER_ERROR = "ResteasyUriBuilderImpl pathSegment parsing incorrect";

    @Test
    public void pathSegmentParserTest() {
        final ResteasyUriBuilderImpl builder = new ResteasyUriBuilderImpl();
        PathSegments pathComponents;

        {
            pathComponents = builder.pathSegmentParser("/x/y/{path}?");
            Assertions.assertEquals(pathComponents.path, "/x/y/{path}", PATH_SEGMENT_PARSER_ERROR);
            Assertions.assertNull(pathComponents.query);
            Assertions.assertNull(pathComponents.fragment);
        }
        {
            pathComponents = builder.pathSegmentParser("/x/y/{path}?name={qval}");
            Assertions.assertEquals(pathComponents.path, "/x/y/{path}", () -> PATH_SEGMENT_PARSER_ERROR);
            Assertions.assertEquals(pathComponents.query, "name={qval}", () -> PATH_SEGMENT_PARSER_ERROR);
            Assertions.assertNull(pathComponents.fragment);
        }
        {
            pathComponents = builder.pathSegmentParser("/?DBquery#DBfragment");
            Assertions.assertEquals(pathComponents.path, "/", () -> PATH_SEGMENT_PARSER_ERROR);
            Assertions.assertEquals(pathComponents.query, "DBquery", () -> PATH_SEGMENT_PARSER_ERROR);
            Assertions.assertEquals(pathComponents.fragment, "DBfragment", () -> PATH_SEGMENT_PARSER_ERROR);
        }
        {
            pathComponents = builder.pathSegmentParser("/a/b/c=GB?objectClass?one");
            Assertions.assertEquals(pathComponents.path, "/a/b/c=GB", () -> PATH_SEGMENT_PARSER_ERROR);
            Assertions.assertEquals(pathComponents.query, "objectClass?one", () -> PATH_SEGMENT_PARSER_ERROR);
            Assertions.assertNull(pathComponents.fragment);
        }
        {
            pathComponents = builder.pathSegmentParser("/a/b/{string:[0-9 ?]+}");
            Assertions.assertEquals(pathComponents.path, "/a/b/{string:[0-9 ?]+}", () -> PATH_SEGMENT_PARSER_ERROR);
            Assertions.assertNull(pathComponents.query);
            Assertions.assertNull(pathComponents.fragment);
        }
        {
            pathComponents = builder.pathSegmentParser("/a/b/{string:[0-9 ?]+}/c?a=x&b=ye/s?#");
            Assertions.assertEquals(pathComponents.path, "/a/b/{string:[0-9 ?]+}/c", () -> PATH_SEGMENT_PARSER_ERROR);
            Assertions.assertEquals(pathComponents.query, "a=x&b=ye/s?", () -> PATH_SEGMENT_PARSER_ERROR);
            Assertions.assertNull(pathComponents.fragment);
        }
        {
            pathComponents = builder.pathSegmentParser("/a/b/{string:[0-9 ?]+}/c?a=x&b=ye/s?#hello");
            Assertions.assertEquals(pathComponents.path, "/a/b/{string:[0-9 ?]+}/c", () -> PATH_SEGMENT_PARSER_ERROR);
            Assertions.assertEquals(pathComponents.query, "a=x&b=ye/s?", () -> PATH_SEGMENT_PARSER_ERROR);
        }
    }

    @Test
    void resolveUriTemplate() {
        final UriBuilder builder = UriBuilder.fromUri("https://{hostname}:{port}/");
        // We have to use a HashMap or a map that supports null values as resolveTemplates() checks the map for a null key
        final Map<String, Object> map = new HashMap<>();
        map.put("hostname", "localhost");
        map.put("port", 8443);
        Assertions.assertEquals("https://localhost:8443/", builder.resolveTemplates(map).build().toString());
    }

    @Test
    void resolveUriTemplateParamNamePrefix() {
        final UriBuilder builder = UriBuilder.fromUri("https://localhost/{namespace}/{name}");
        final Map<String, Object> map = Map.of("namespace", "ns", "name", "abc");
        Assertions.assertEquals("https://localhost/ns/abc", builder.resolveTemplates(map).build().toString());
    }

    @Test
    void resolveUriTemplateWithRegexParamNamePrefixMatches() {
        // "name" is a prefix-match target inside the literal "{namespace}" segment; the regex on "name" must
        // still be enforced and not be skipped due to the earlier, unrelated "{namespace}" match.
        final UriBuilder builder = UriBuilder.fromUri("https://localhost/{namespace}/{name:[a-z]+}");
        final Map<String, Object> map = Map.of("namespace", "ns", "name", "abc");
        Assertions.assertEquals("https://localhost/ns/abc", builder.resolveTemplates(map).build().toString());
    }

    @Test
    void resolveUriTemplateWithRegexParamNamePrefixThrows() {
        final UriBuilder builder = UriBuilder.fromUri("https://localhost/{namespace}/{name:[a-z]+}");
        final Map<String, Object> map = Map.of("namespace", "ns", "name", "ABC123");
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.resolveTemplates(map).build());
    }

    @Test
    void resolveUriTemplateWithNestedBracesInRegexMatches() {
        // The regex itself contains braces, e.g. a quantifier like {0,10}. The closing brace of the
        // template parameter must not be confused with the braces inside the regex.
        final UriBuilder builder = UriBuilder.fromUri("https://localhost/{b:B{0,10}}");
        final Map<String, Object> map = Map.of("b", "BBB");
        Assertions.assertEquals("https://localhost/BBB", builder.resolveTemplates(map).build().toString());
    }

    @Test
    void resolveUriTemplateWithNestedBracesInRegexThrows() {
        final UriBuilder builder = UriBuilder.fromUri("https://localhost/{b:B{0,10}}");
        final Map<String, Object> map = Map.of("b", "C");
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.resolveTemplates(map).build());
    }

    @Test
    void resolveUriTemplateWithBlankRegexThrows() {
        final UriBuilder builder = UriBuilder.fromUri("https://localhost/{id: }");
        final Map<String, Object> map = Map.of("id", "");
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.resolveTemplates(map).build());
    }

    @Test
    void resolveUriTemplateRegexMismatchThrows() {
        final UriBuilder builder = UriBuilder.fromUri("https://localhost/{id:[0-9]+}");
        final Map<String, Object> map = Map.of("id", "abc");
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.resolveTemplates(map).build());
    }

    @Test
    void resolveUriTemplateNullValueWithRegexThrows() {
        final UriBuilder builder = UriBuilder.fromUri("https://localhost/{id:[0-9]+}");
        // Map.of() does not permit null values, so a HashMap is required here.
        final Map<String, Object> map = new HashMap<>();
        map.put("id", null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.resolveTemplates(map).build());
    }

    @Test
    void resolveUriTemplateNonStringValueWithRegex() {
        final UriBuilder builder = UriBuilder.fromUri("https://localhost/{port:[0-9]+}");
        final Map<String, Object> map = Map.of("port", 8080);
        Assertions.assertEquals("https://localhost/8080", builder.resolveTemplates(map).build().toString());
    }

    @Test
    void resolveUriTemplateMultipleRegexParams() {
        final UriBuilder builder = UriBuilder.fromUri("https://localhost/{a:[0-9]+}/{b:[a-z]+}");
        final Map<String, Object> map = Map.of("a", "123", "b", "abc");
        Assertions.assertEquals("https://localhost/123/abc", builder.resolveTemplates(map).build().toString());
    }

    @Test
    void resolveUriTemplateMultipleRegexParamsSecondInvalidThrows() {
        final UriBuilder builder = UriBuilder.fromUri("https://localhost/{a:[0-9]+}/{b:[a-z]+}");
        final Map<String, Object> map = Map.of("a", "123", "b", "ABC");
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.resolveTemplates(map).build());
    }
}
