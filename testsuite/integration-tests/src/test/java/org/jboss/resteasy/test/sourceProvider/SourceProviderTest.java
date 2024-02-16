package org.jboss.resteasy.test.sourceProvider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import javax.xml.transform.stream.StreamSource;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.sourceProvider.resource.Book;
import org.jboss.resteasy.test.sourceProvider.resource.BookResource;
import org.jboss.resteasy.test.sourceProvider.resource.SourceProviderApp;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
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

    @BeforeEach
    public void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterEach
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
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        Assertions.assertTrue(
                entity.contentEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><book><title>Monkey kingdom</title></book>"));
    }

    @Test
    public void testSourceWithInputStream() throws Exception {
        InputStream stream = new ByteArrayInputStream(book.getBytes(StandardCharsets.UTF_8));
        Response response = client.target(generateURL("/test")).request()
                .post(Entity.entity(new StreamSource(stream), "application/*+xml"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        Assertions.assertTrue(
                entity.contentEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><book><title>Monkey kingdom</title></book>"));
    }
}
