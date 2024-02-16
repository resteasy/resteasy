package org.jboss.resteasy.test.interception;

import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.interception.jaxrs.ContainerResponseContextImpl;
import org.jboss.resteasy.plugins.interceptors.MessageSanitizerContainerResponseFilter;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.specimpl.BuiltResponseEntityNotBacked;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Providers - MessageSanitizerContainerResponseFilter
 * @tpChapter Unit tests
 * @tpTestCaseDetails Regression test for RESTEASY-2033
 * @tpSince RESTEasy 4.0.0
 */
public class MessageSanitizerMediaTypeTest {

    private static final String input = "<html &lt;\"abc\" 'xyz'&gt;/>";
    private static final String output = "&lt;html &amp;lt;&quot;abc&quot; &#x27;xyz&#x27;&amp;gt;&#x2F;&gt;";

    public static class TestContainerResponseContext extends ContainerResponseContextImpl {
        public TestContainerResponseContext(final BuiltResponse builtResponse) {
            super(null, null, builtResponse, null, null, null, null);
        }
    }

    @Test
    public void testMessageSanitizerText() throws Exception {
        doTestMessageSanitizerMediaType("text/html");
    }

    @Test
    public void testMessageSanitizerMediaType() throws Exception {
        doTestMessageSanitizerMediaType(MediaType.TEXT_HTML_TYPE);
    }

    void doTestMessageSanitizerMediaType(Object mediaType) throws Exception {
        Headers<Object> headers = new Headers<Object>();
        headers.add("Content-Type", mediaType);
        BuiltResponseEntityNotBacked response = new BuiltResponseEntityNotBacked(
                HttpResponseCodes.SC_BAD_REQUEST, "", headers, input, null);
        ContainerResponseContext responseContext = new TestContainerResponseContext(response);
        MessageSanitizerContainerResponseFilter filter = new MessageSanitizerContainerResponseFilter();
        filter.filter(null, responseContext);
        Assertions.assertEquals(output, responseContext.getEntity());
    }
}