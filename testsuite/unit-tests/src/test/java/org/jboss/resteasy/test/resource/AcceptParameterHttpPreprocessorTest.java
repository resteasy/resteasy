package org.jboss.resteasy.test.resource;

import org.jboss.resteasy.core.AcceptParameterHttpPreprocessor;
import org.jboss.resteasy.core.interception.PreMatchContainerRequestContext;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.spi.HttpRequest;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


/**
 * @tpSubChapter Resource tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Tests for content negotiation through a query parameter, instead of the Accept Header.
 * This is used when context-param resteasy.media.type.param.mapping is enabled in web.xml.
 * @tpSince RESTEasy 3.0.16
 */
public class AcceptParameterHttpPreprocessorTest {

    /**
     * @tpTestDetails Basic query parameter with xml type
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void simple() throws Exception {

        String acceptParamName = "accept";
        AcceptParameterHttpPreprocessor processor = new AcceptParameterHttpPreprocessor(acceptParamName);

        String type = MediaType.TEXT_XML;
        HttpRequest request = MockHttpRequest.get("foo?" + acceptParamName + "=" + type);

        MediaType mediaType = MediaType.valueOf(type);

        PreMatchContainerRequestContext context = new PreMatchContainerRequestContext(request, null, null);
        processor.filter(context);

        List<MediaType> list = request.getHttpHeaders().getAcceptableMediaTypes();

        Assert.assertEquals("Incorrect acceptable list size", 1, list.size());
        Assert.assertEquals("Incorrect media type extracted", mediaType, list.get(0));
    }

    /**
     * @tpTestDetails Query parameter with multiple types
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void aLittleMoreComplicated() throws Exception {

        String acceptParamName = "bar";
        AcceptParameterHttpPreprocessor processor = new AcceptParameterHttpPreprocessor(acceptParamName);

        List<MediaType> expected = Arrays.asList(MediaType.TEXT_XML_TYPE, MediaType.TEXT_PLAIN_TYPE, MediaType.TEXT_HTML_TYPE, MediaType.APPLICATION_XHTML_XML_TYPE);

        MockHttpRequest request = MockHttpRequest.get("foo?" + acceptParamName + "=" + expected.get(0) + "," + expected.get(1));
        PreMatchContainerRequestContext context = new PreMatchContainerRequestContext(request, null, null);

        request.accept(expected.get(2));
        request.accept(expected.get(3));

        processor.filter(context);

        List<MediaType> actual = request.getHttpHeaders().getAcceptableMediaTypes();

        for (MediaType expect : expected) {
            Assert.assertTrue(actual.contains(expect));
        }

    }

    /**
     * @tpTestDetails Request without query parameter
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void withoutParam() throws Exception {

        String acceptParamName = "baz";
        AcceptParameterHttpPreprocessor processor = new AcceptParameterHttpPreprocessor(acceptParamName);

        List<MediaType> expected = Arrays.asList(MediaType.TEXT_PLAIN_TYPE, MediaType.TEXT_HTML_TYPE);

        MockHttpRequest request = MockHttpRequest.get("foo");
        PreMatchContainerRequestContext context = new PreMatchContainerRequestContext(request, null, null);
        request.accept(expected.get(0));
        request.accept(expected.get(1));

        processor.filter(context);

        List<MediaType> actual = request.getHttpHeaders().getAcceptableMediaTypes();

        for (MediaType expect : expected) {
            Assert.assertTrue(actual.contains(expect));
        }
    }

    /**
     * @tpTestDetails Request with complex query parameter
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void complex() throws Exception {

        String acceptParamName = "bar";
        AcceptParameterHttpPreprocessor processor = new AcceptParameterHttpPreprocessor(acceptParamName);

        List<MediaType> expected = Arrays.asList(
                MediaType.valueOf("application/xhtml+xml"),
                MediaType.valueOf("text/html"),
                MediaType.valueOf("application/xml;q=0.9"),
                MediaType.valueOf("*/*;q=0.8")
        );

        String param1 = URLEncoder.encode("application/xml;q=0.9,application/xhtml+xml,*/*;q=0.8", StandardCharsets.UTF_8.name());
        String param2 = URLEncoder.encode("text/html", StandardCharsets.UTF_8.name());
        HttpRequest request = MockHttpRequest.get(
                "foo?" + acceptParamName + "=" + param1 + "&" +
                        acceptParamName + "=" + param2);

        PreMatchContainerRequestContext context = new PreMatchContainerRequestContext(request, null, null);
        processor.filter(context);

        List<MediaType> actual = request.getHttpHeaders().getAcceptableMediaTypes();

        Assert.assertEquals("Incorrect acceptable media type extracted", expected, actual);
    }
}
