package org.jboss.resteasy.test.sourceProvider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.sourceProvider.resource.Book;
import org.jboss.resteasy.test.sourceProvider.resource.BookResource;
import org.jboss.resteasy.test.sourceProvider.resource.SourceProviderApp;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

@RunWith(Arquillian.class)
@RunAsClient
public class SourceProviderTest {

    private static Client client;
    private String book = "<book><title>Monkey kingdom</title></book>";

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(SourceProviderTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SourceProviderApp.class,
                BookResource.class, Book.class);
    }

    @Before
    public void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @After
    public void close() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SourceProviderTest.class.getSimpleName());
    }

    @Test
    public void testSourceWithStringReader() throws Exception {
        Response response = client.target(generateURL("/test")).request()
                .post(Entity.entity(new StreamSource(new StringReader(book)), "application/*+xml"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        Assert.assertTrue(entity.contentEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><book><title>Monkey kingdom</title></book>"));
    }

    @Test
    public void testSourceWithInputStream() throws Exception {
        InputStream stream = new ByteArrayInputStream(book.getBytes(StandardCharsets.UTF_8));
        Response response = client.target(generateURL("/test")).request()
                .post(Entity.entity(new StreamSource(stream), "application/*+xml"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        Assert.assertTrue(entity.contentEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><book><title>Monkey kingdom</title></book>"));
    }
}
