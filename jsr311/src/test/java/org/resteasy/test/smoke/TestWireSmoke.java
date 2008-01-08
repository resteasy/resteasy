package org.resteasy.test.smoke;

import Acme.Serve.Serve;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.plugins.providers.DefaultPlainText;
import org.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ext.ProviderFactory;
import java.util.Properties;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestWireSmoke {

    private static Serve server = null;
    private static HttpServletDispatcher dispatcher = new HttpServletDispatcher();

    @BeforeClass
    public static void before() throws Exception {
        ProviderFactory.setInstance(dispatcher.getProviderFactory());
        dispatcher.getProviderFactory().addMessageBodyReader(new DefaultPlainText());
        dispatcher.getProviderFactory().addMessageBodyWriter(new DefaultPlainText());

        server = new Serve();
        Properties props = new Properties();
        props.put("port", 8081);
        props.setProperty(Acme.Serve.Serve.ARG_NOHUP, "nohup");
        server.arguments = props;
        server.addDefaultServlets(null); // optional file servlet
        server.addServlet("/", dispatcher); // optional
        new Thread() {
            public void run() {
                server.serve();
            }
        }.start();
    }

    @AfterClass
    public static void after() throws Exception {
        server.notifyStop();
    }

    @Test
    public void testNoDefaultsResource() throws Exception {
        POJOResourceFactory noDefaults = new POJOResourceFactory(SimpleResource.class);
        dispatcher.getRegistry().addResourceFactory(noDefaults);

        HttpClient client = new HttpClient();

        {
            GetMethod method = new GetMethod("http://localhost:8081/basic");
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpServletResponse.SC_OK, status);
            Assert.assertEquals("basic", method.getResponseBodyAsString());
            method.releaseConnection();
        }
        {
            PutMethod method = new PutMethod("http://localhost:8081/basic");
            method.setRequestEntity(new StringRequestEntity("basic", "text/plain", null));
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpServletResponse.SC_OK, status);
            method.releaseConnection();
        }
        {
            GetMethod method = new GetMethod("http://localhost:8081/queryParam");
            NameValuePair[] params = {new NameValuePair("param", "hello world")};
            method.setQueryString(params);
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpServletResponse.SC_OK, status);
            Assert.assertEquals("hello world", method.getResponseBodyAsString());
            method.releaseConnection();
        }
        {
            GetMethod method = new GetMethod("http://localhost:8081/uriParam/1234");
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpServletResponse.SC_OK, status);
            Assert.assertEquals("1234", method.getResponseBodyAsString());
            method.releaseConnection();
        }
        {
            GetMethod method = new GetMethod("http://localhost:8081/should/accept/anything");
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpServletResponse.SC_OK, status);
            Assert.assertEquals("Wild", method.getResponseBodyAsString());
            method.releaseConnection();
        }
        {
            GetMethod method = new GetMethod("http://localhost:8081/basic/should/accept");
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpServletResponse.SC_OK, status);
            Assert.assertEquals("Wild", method.getResponseBodyAsString());
            method.releaseConnection();
        }
        {
            GetMethod method = new GetMethod("http://localhost:8081/uriParam");
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpServletResponse.SC_OK, status);
            Assert.assertEquals("Wild", method.getResponseBodyAsString());
            method.releaseConnection();
        }
    }


    @Test
    public void testLocatingResource() throws Exception {
        POJOResourceFactory noDefaults = new POJOResourceFactory(LocatingResource.class);
        dispatcher.getRegistry().addResourceFactory(noDefaults);

        HttpClient client = new HttpClient();

        {
            GetMethod method = new GetMethod("http://localhost:8081/locating/basic");
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpServletResponse.SC_OK, status);
            Assert.assertEquals("basic", method.getResponseBodyAsString());
            method.releaseConnection();
        }
        {
            PutMethod method = new PutMethod("http://localhost:8081/locating/basic");
            method.setRequestEntity(new StringRequestEntity("basic", "text/plain", null));
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpServletResponse.SC_OK, status);
            method.releaseConnection();
        }
        {
            GetMethod method = new GetMethod("http://localhost:8081/locating/queryParam");
            NameValuePair[] params = {new NameValuePair("param", "hello world")};
            method.setQueryString(params);
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpServletResponse.SC_OK, status);
            Assert.assertEquals("hello world", method.getResponseBodyAsString());
            method.releaseConnection();
        }
        {
            GetMethod method = new GetMethod("http://localhost:8081/locating/uriParam/1234");
            int status = client.executeMethod(method);
            Assert.assertEquals(HttpServletResponse.SC_OK, status);
            Assert.assertEquals("1234", method.getResponseBodyAsString());
            method.releaseConnection();
        }
    }
}