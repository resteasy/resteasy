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
import org.junit.Assert;
import org.junit.Test;

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
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathComponents.path, "/x/y/{path}");
            Assert.assertNull(pathComponents.query);
            Assert.assertNull(pathComponents.fragment);
        }
        {
            pathComponents = builder.pathSegmentParser("/x/y/{path}?name={qval}");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathComponents.path, "/x/y/{path}");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathComponents.query, "name={qval}");
            Assert.assertNull(pathComponents.fragment);
        }
        {
            pathComponents = builder.pathSegmentParser("/?DBquery#DBfragment");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathComponents.path, "/");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathComponents.query, "DBquery");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathComponents.fragment, "DBfragment");
        }
        {
            pathComponents = builder.pathSegmentParser("/a/b/c=GB?objectClass?one");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathComponents.path, "/a/b/c=GB");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathComponents.query, "objectClass?one");
            Assert.assertNull(pathComponents.fragment);
        }
        {
            pathComponents = builder.pathSegmentParser("/a/b/{string:[0-9 ?]+}");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathComponents.path, "/a/b/{string:[0-9 ?]+}");
            Assert.assertNull(pathComponents.query);
            Assert.assertNull(pathComponents.fragment);
        }
        {
            pathComponents = builder.pathSegmentParser("/a/b/{string:[0-9 ?]+}/c?a=x&b=ye/s?#");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathComponents.path, "/a/b/{string:[0-9 ?]+}/c");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathComponents.query, "a=x&b=ye/s?");
            Assert.assertNull(pathComponents.fragment);
        }
        {
            pathComponents = builder.pathSegmentParser("/a/b/{string:[0-9 ?]+}/c?a=x&b=ye/s?#hello");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathComponents.path, "/a/b/{string:[0-9 ?]+}/c");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathComponents.query, "a=x&b=ye/s?");
            Assert.assertEquals(PATH_SEGMENT_PARSER_ERROR, pathComponents.fragment, "hello");
        }
    }
}
