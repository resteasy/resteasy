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
}
