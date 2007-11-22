package org.resteasy.test.smoke;

import org.junit.Assert;
import org.junit.Test;
import org.resteasy.HttpServletDispatcher;
import org.resteasy.mock.MockHttpServletRequest;
import org.resteasy.mock.MockHttpServletResponse;
import org.resteasy.plugins.providers.DefaultPlainText;
import org.resteasy.plugins.resourcefactory.POJOResourceFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ext.ProviderFactory;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestSmoke {

    @Test
    public void testNoDefaultsResource() throws Exception {
        HttpServletDispatcher dispatcher = new HttpServletDispatcher();
        ProviderFactory.setInstance(dispatcher.getProviderFactory());
        dispatcher.getProviderFactory().addMessageBodyReader(new DefaultPlainText());
        dispatcher.getProviderFactory().addMessageBodyWriter(new DefaultPlainText());

        POJOResourceFactory noDefaults = new POJOResourceFactory(SimpleResource.class);
        dispatcher.getRegistry().addResourceFactory(noDefaults);

        {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/basic");
            request.setPathInfo("/basic");
            MockHttpServletResponse response = new MockHttpServletResponse();

            dispatcher.invoke(request, response);


            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals("basic", response.getContentAsString());
        }
        {
            MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/basic");
            request.setPathInfo("/basic");
            request.setContent("basic".getBytes());
            request.setContentType("text/plain");
            MockHttpServletResponse response = new MockHttpServletResponse();

            dispatcher.invoke(request, response);


            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        }
        {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/queryParam");
            request.setPathInfo("/queryParam");
            request.addParameter("param", "hello world");
            MockHttpServletResponse response = new MockHttpServletResponse();

            dispatcher.invoke(request, response);


            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals("hello world", response.getContentAsString());
        }
        {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/uriParam/1234");
            request.setPathInfo("/uriParam/1234");
            MockHttpServletResponse response = new MockHttpServletResponse();

            dispatcher.invoke(request, response);


            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals("1234", response.getContentAsString());
        }
    }

    @Test
    public void testLocatingResource() throws Exception {
        HttpServletDispatcher dispatcher = new HttpServletDispatcher();
        ProviderFactory.setInstance(dispatcher.getProviderFactory());
        dispatcher.getProviderFactory().addMessageBodyReader(new DefaultPlainText());
        dispatcher.getProviderFactory().addMessageBodyWriter(new DefaultPlainText());

        POJOResourceFactory noDefaults = new POJOResourceFactory(LocatingResource.class);
        dispatcher.getRegistry().addResourceFactory(noDefaults);

        {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/locating/basic");
            request.setPathInfo("/locating/basic");
            MockHttpServletResponse response = new MockHttpServletResponse();

            dispatcher.invoke(request, response);


            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals("basic", response.getContentAsString());
        }
        {
            MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/locating/basic");
            request.setPathInfo("/locating/basic");
            request.setContent("basic".getBytes());
            request.setContentType("text/plain");
            MockHttpServletResponse response = new MockHttpServletResponse();

            dispatcher.invoke(request, response);


            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        }
        {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/locating/queryParam");
            request.setPathInfo("/locating/queryParam");
            request.addParameter("param", "hello world");
            MockHttpServletResponse response = new MockHttpServletResponse();

            dispatcher.invoke(request, response);


            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals("hello world", response.getContentAsString());
        }
        {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/locating/uriParam/1234");
            request.setPathInfo("/locating/uriParam/1234");
            MockHttpServletResponse response = new MockHttpServletResponse();

            dispatcher.invoke(request, response);


            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assert.assertEquals("1234", response.getContentAsString());
        }
    }
}
