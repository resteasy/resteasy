package org.resteasy.test.smoke;

import org.junit.Test;
import org.junit.Assert;
import org.resteasy.HttpServletDispatcher;
import org.resteasy.plugins.providers.POJOResourceFactory;
import org.resteasy.plugins.providers.DefaultPlainText;
import com.damnhandy.resteasy.test.mock.MockHttpServletRequest;
import com.damnhandy.resteasy.test.mock.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ext.ProviderFactory;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestSmoke {

    @Test
    public void testNoDefaultsResource() throws Exception
    {
        HttpServletDispatcher dispatcher = new HttpServletDispatcher();
        ProviderFactory.setInstance(dispatcher.getProviderFactory());
        dispatcher.getProviderFactory().addMessageBodyReader(new DefaultPlainText());
        dispatcher.getProviderFactory().addMessageBodyWriter(new DefaultPlainText());

        POJOResourceFactory noDefaults = new POJOResourceFactory(NoDefaultsResource.class);
        dispatcher.getRegistry().addResourceFactory(noDefaults);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/basic");
        request.setPathInfo("/basic");
        MockHttpServletResponse response = new MockHttpServletResponse();

        dispatcher.invoke(request, response);


        Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Assert.assertEquals("basic", response.getContentAsString());
    }
}
