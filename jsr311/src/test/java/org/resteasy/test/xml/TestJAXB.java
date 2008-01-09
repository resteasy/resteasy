package org.resteasy.test.xml;

import Acme.Serve.Serve;
import org.apache.commons.httpclient.HttpClient;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.plugins.client.httpclient.ProxyFactory;
import org.resteasy.plugins.providers.DefaultPlainText;
import org.resteasy.plugins.providers.JAXBProvider;
import org.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;

import javax.ws.rs.ext.ProviderFactory;
import java.util.Properties;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestJAXB {

    private static Serve server = null;
    private static HttpServletDispatcher dispatcher = new HttpServletDispatcher();

    @BeforeClass
    public static void before() throws Exception {
        ProviderFactory.setInstance(dispatcher.getProviderFactory());
        dispatcher.getProviderFactory().addMessageBodyReader(new DefaultPlainText());
        dispatcher.getProviderFactory().addMessageBodyWriter(new DefaultPlainText());
        dispatcher.getProviderFactory().addMessageBodyReader(new JAXBProvider());
        dispatcher.getProviderFactory().addMessageBodyWriter(new JAXBProvider());


        server = new Serve();
        Properties props = new Properties();
        props.put("port", 8081);
        props.setProperty(Serve.ARG_NOHUP, "nohup");
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
        POJOResourceFactory noDefaults = new POJOResourceFactory(BookStore.class);
        dispatcher.getRegistry().addResourceFactory(noDefaults);

        HttpClient httpClient = new HttpClient();
        BookStoreClient client = ProxyFactory.create(BookStoreClient.class, "http://localhost:8081", httpClient);

        Book book = client.getBookByISBN("596529260");
        Assert.assertNotNull(book);
        Assert.assertEquals("RESTful Web Services", book.getTitle());

        // TJWS does not support chunk encodings well so I need to kill kept alive connections
        httpClient.getHttpConnectionManager().closeIdleConnections(0);

        book = new Book("Bill Burke", "666", "EJB 3.0");
        client.addBook(book);
        // TJWS does not support chunk encodings so I need to kill kept alive connections
        httpClient.getHttpConnectionManager().closeIdleConnections(0);
        book = client.getBookByISBN("666");
        Assert.assertEquals("Bill Burke", book.getAuthor());
        httpClient.getHttpConnectionManager().closeIdleConnections(0);
    }


}