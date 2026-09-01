/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.xml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.util.XmlSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@RestBootstrap({ SourceProviderXxeTest.TestResource.class, SourceProviderXxeTest.SAXSourceMessageBodyReader.class })
public class SourceProviderXxeTest {
    private static final String SECURE_TEXT = "This is secure data";

    private static java.nio.file.Path SECURE_FILE;

    @BeforeAll
    static void setup(@TempDir final java.nio.file.Path tempDir) throws Exception {
        SECURE_FILE = tempDir.resolve("secure-file.txt");
        Files.writeString(SECURE_FILE, SECURE_TEXT, StandardOpenOption.CREATE);
    }

    @Test
    void source(@RestResource @RequestPath("test") final WebTarget target) {
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE secret [<!ENTITY xxe SYSTEM \"" + SECURE_FILE.toAbsolutePath().toUri() + "\">]>\n" +
                "<secret>&xxe;</secret>";
        try (Response response = target.request(MediaType.APPLICATION_XML_TYPE).post(Entity.xml(xml))) {
            // A 500 response should be expected
            Assertions.assertEquals(500, response.getStatus(), () -> String.format("Unexpected status code %d: %s",
                    response.getStatus(), response.readEntity(String.class)));
            final String xmlResponse = response.readEntity(String.class);
            // We should have an empty response
            Assertions.assertTrue(xmlResponse.isBlank(),
                    () -> String.format("Expected empty response, but got %s", xmlResponse));
        }
    }

    @Test
    void saxSource(@RestResource @RequestPath("test/sax") final WebTarget target) {
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE secret [<!ENTITY xxe SYSTEM \"" + SECURE_FILE.toAbsolutePath().toUri() + "\">]>\n" +
                "<secret>&xxe;</secret>";
        try (
                Response response = target.request(MediaType.APPLICATION_XML_TYPE)
                        .post(Entity.xml(xml))) {
            // A 200 response should be expected
            Assertions.assertEquals(200, response.getStatus(), () -> String.format("Unexpected status code %d: %s",
                    response.getStatus(), response.readEntity(String.class)));
            final String xmlResponse = response.readEntity(String.class);
            // The response should contain the secure text as we don't secure the XMLReader
            Assertions.assertTrue(xmlResponse.contains(SECURE_TEXT),
                    () -> String.format("Expected the response to contain '%s': %s", SECURE_TEXT, xmlResponse));
        }
    }

    @Test
    void saxSourceWrapped(@RestResource @RequestPath("test/sax") final WebTarget target) {
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE secret [<!ENTITY xxe SYSTEM \"" + SECURE_FILE.toAbsolutePath().toUri() + "\">]>\n" +
                "<secret>&xxe;</secret>";
        try (
                Response response = target.request(MediaType.APPLICATION_XML_TYPE).header("wrap-xml-reader", "true")
                        .post(Entity.xml(xml))) {
            // A 500 response should be expected
            Assertions.assertEquals(500, response.getStatus(), () -> String.format("Unexpected status code %d: %s",
                    response.getStatus(), response.readEntity(String.class)));
            final String xmlResponse = response.readEntity(String.class);
            // We should have an empty response
            Assertions.assertTrue(xmlResponse.isBlank(),
                    () -> String.format("Expected empty response, but got %s", xmlResponse));
        }
    }

    @Path("test")
    @Consumes(MediaType.APPLICATION_XML)
    public static class TestResource {
        @POST
        public Source echo(final Source source) {
            return source;
        }

        @POST
        @Path("/sax")
        public Source echoSax(final SAXSource source) {
            return source;
        }
    }

    @Provider
    @Consumes(MediaType.APPLICATION_XML)
    public static class SAXSourceMessageBodyReader implements MessageBodyReader<SAXSource> {
        @Override
        public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations,
                final MediaType mediaType) {
            return SAXSource.class.isAssignableFrom(type);
        }

        @Override
        public SAXSource readFrom(final Class<SAXSource> type, final Type genericType, final Annotation[] annotations,
                final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream)
                throws IOException, WebApplicationException {
            if (httpHeaders.containsKey("wrap-xml-reader")) {
                try {
                    return new SAXSource(XmlSupport.newXmlReader(), new InputSource(entityStream));
                } catch (ParserConfigurationException | SAXException e) {
                    throw new BadRequestException(e);
                }
            }
            return new SAXSource(new InputSource(entityStream));
        }
    }
}
