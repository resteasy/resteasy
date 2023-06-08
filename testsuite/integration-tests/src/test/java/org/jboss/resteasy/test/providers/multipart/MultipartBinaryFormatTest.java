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

package org.jboss.resteasy.test.providers.multipart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.plugins.providers.multipart.OutputPart;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests that multipart data is always sent in binary format.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 * @since 3.15.8
 */
@RunWith(Arquillian.class)
public class MultipartBinaryFormatTest {
    private static final String PNG = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJ" +
            "bWFnZVJlYWR5ccllPAAABYtJREFUeNrsVm9sU1UUP127tlvX7q1jWzfG9uaYIAi8xRiiQ/uc/ElI" +
            "gFKMMGLcEk384oeHH/xgYigx8atNJCYYP3SJApIgXaLEDZAOg1GB+IpAJmPsFZqOwf687k/XsbX1" +
            "nHrf8hgdg09+2Ulu7n333HvPOb/zO6cFWJIl+Z/FoC3a29sPztMpOOTW1tYw6lpxzee4T2eCeCaO" +
            "ZzY47T0ec/44qBP1SvKhM4g6boF7mqhzDlw8uztjL4w9op2escP9USFUVBCDEnufmOuFG8p+yWBI" +
            "q9VlFwL6+9H7m5QMGJQV5b+KC1qfqAOj9rFjZwvvtPcKY5M1ysj48/74JC+bjNOrnfabq2NDTUq+" +
            "cQos5jh3Z9AdSEyXBzFCsFnv87Mp6+8OW8RfYBmGyODmwODIS36TcUqscIZdiWQFT07RHXqvuCgy" +
            "934G8oQ8w6zVpDkwNskHcGpLpfOVbTsOH6K90x0HwFEYlRCBEO5TJPzw2NoAQt6NkAeiD14neJWV" +
            "y4O+h4gWviFROk6e+JxSIE5MVcHA8MtAd+huTUX33PsdJz9V0AHe9CSCGPNmBJoTyXLVah7RtgU0" +
            "PscBNBg5d/p9pYz7m+ddnQoa909MLQ/sefuTQ/q38M4jXNi157PsI485YMkfE7o795/PZPJ4hy3M" +
            "D8XXqGOJ2qDT0eMhfWPDV37tLEbnw+lQ9MFrHkxFsIwL88gV3+h4vfT9sS+kvS0H2hergrzHojYm" +
            "OcqhreAen0pbiIQ+ilLTUz7JMA2EOER7VCkIa11v1Nv2QF2n2AvvcrWuMwGM2v3MDiDcoabNpwx3" +
            "Bpt9FiwphFbS6ymflEMaCHM3g7eWZor4ze3f1A0MbwwVIildzkviYg4syAHMo5+gLLH/wx/77suD" +
            "6fRlqmkwm8ZExgGhpuKcH8kmA/wFZ378AGLDr1JKVJPxEq9xZ9FG9M6+7ZkFdPJu76ZgQ3WHj+Ub" +
            "KksvPXaIIKeZSDibMgOljZAbHGlUk7NuYYenJXIx9MPBuwOT8praw0EMKuTedvQNPQIyRUOeszXH" +
            "vgVEQYzcawbqbqPjDSrTPSLxyVp5aro8hPXdhv3Ag/yB2NArss22TnUWmWQ0TiUolTmtKjmYKwWU" +
            "YyKT/O3x01nPEJXzjFxxXBPrsQJu86z1hvBchJ0rxn0Pe4N0HtTFD3z47q59LRulEqeLGxkakFKp" +
            "GbBajJya2Iud77fFOaA5ouv3+shVNCyy/Zy6F9etCa5d3wS2Ig5mZx/CQLQPYjgsliqYSm5BJI/m" +
            "rAKR+KAN3T7HUkNGA+xbYmnS67Lpe2uPh9+ydSckk4mscZIV/AtQVV2fXRdYi30LlaHKUqENTXhm" +
            "uI0Z1fY0htMePepD5AwWq0WIq0NgtRZmI6d1tr+Y8rHH5EOT6A3PrwK3ZlQPPZJnQ1fXOaHvdr+f" +
            "Raowo4J2Fu/Sz7RflwZCwe/dvTPQvNWLCMxA380w/HL+Aly/dhVWrmyQb93qFYknCzYiZvwUPVTi" +
            "LNEgp+jq6Lc/B19KGDqac1IsNgDhKyG4dvUyHDnyddY4CRrncvUBtw7y7Lx1S7NQX/8c19+vwM+d" +
            "Z7XIQiz3oDsr6lImmc1mzmIxw7JlpVBeXgmZ9AxMJhLQ03NTTaVSEqu08Pwq0KDl2IMwMqoCUcZV" +
            "6ZKZTmTR+Vm0GvkU9i2icfB6PXD8+AkYH5+A/v4I1Kyohjt3o4Ramx72nH/J9PLxR++5ueLiYGPj" +
            "enA4HFqEZFBGEsVzpMxtLSjyX7/RK3R1/qQnNRnueKr/hPMFU0M/MIGqqkpx1aoGqONrsY4tOc+m" +
            "0nnwx59XICxf0baeGPVTOaBzpFWr+9JSJ9jt9myONSHC0XiWqJ/JAZ0jG/5ryVku8LqeoJGUovY/" +
            "TdRLsiR6+VeAAQCs+Y2VnlcECgAAAABJRU5ErkJggg==";

    @ArquillianResource
    private URL url;

    @Deployment
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class, MultipartBinaryFormatTest.class.getSimpleName() + ".war")
                .addClasses(RestActivator.class, MultipartResource.class);
    }

    /**
     * Tests that multipart data sent in base64 text has a response in a binary format.
     * See <a href="https://issues.redhat.com/browse/RESTEASY-3341">RESTEASY-3341</a> for details.
     *
     * @throws Exception if an error occurs
     */
    @Test
    @RunAsClient
    public void checkRestClient() throws Exception {
        Client client = ClientBuilder.newClient();
        try {
            final MultipartOutput output = new MultipartOutput();
            output.setBoundary("TestData");
            final OutputPart part = output.addPart(PNG.getBytes(StandardCharsets.UTF_8), MediaType.valueOf("image/png"), "test.png", true);
            part.getHeaders().add("Content-Transfer-Encoding", "base64");
            try (
                    Response response = client.target(TestUtil.generateUri(url, "test/multipart"))
                            .request()
                            .post(Entity.entity(output, "multipart/mixed"))
            ) {
                final byte[] content = response.readEntity(byte[].class);
                if (response.getStatus() != 200) {
                    Assert.fail("Expected 200 but got " + response.getStatus() + ": " + new String(content));
                }
                // These are the expected bytes, the first is a non-ascii byte followed by PNG\r\n0x1a\n
                final byte[] expected = new byte[] {-119, 80, 78, 71, 13, 10, 26, 10};
                final byte[] sub = Arrays.copyOf(content, expected.length);
                Assert.assertArrayEquals(expected, sub);
            }
        } finally {
            client.close();
        }
    }

    /**
     * Tests that multipart data sent in base64 text has a response in a binary format.
     * See <a href="https://issues.redhat.com/browse/RESTEASY-3341">RESTEASY-3341</a> for details.
     *
     * @throws Exception if an error occurs
     */
    @Test
    @RunAsClient
    public void checkHttpClient() throws Exception {
        // Use a standard URL connection as there is no generic HTTP client available in Java 8
        final HttpURLConnection connection = (HttpURLConnection) TestUtil.generateUri(url, "test/multipart")
                .toURL()
                .openConnection();
        try {
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Setup are content
            final String part = "--TestData\n" +
                    "Content-Disposition: name\"IMAGE\"; filename=\"test.png\"\n" +
                    "Content-Transfer-Encoding: base64\n" +
                    "Content-Type: image/png\n\n"
                    + PNG + "\n\n--TestData--\r\n";
            connection.setRequestProperty("Content-Type", "multipart/mixed; boundary=TestData");
            connection.setRequestProperty("Accept", "application/octet-stream");
            try (OutputStream out = connection.getOutputStream()) {
                out.write(part.getBytes(StandardCharsets.UTF_8));
            }

            // Now we need to read the response
            try (InputStream in = connection.getInputStream()) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                if (connection.getResponseCode() != 200) {
                    // Read the bytes into a string for the failure message
                    final byte[] buffer = new byte[256];
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    Assert.fail(String.format("Expected 200 but got %d: %s - %s", connection.getResponseCode(), connection.getResponseMessage(), out));
                }
                // These are the expected bytes, the first is a non-ascii byte followed by PNG\r\n0x1a\n
                final byte[] expected = new byte[] {-119, 80, 78, 71, 13, 10, 26, 10};
                final byte[] buffer = new byte[expected.length];
                Assert.assertEquals("Invalid byte length found: " + Arrays.toString(buffer), expected.length, in.read(buffer));
                Assert.assertArrayEquals(expected, buffer);
            }
        } finally {
            connection.disconnect();
        }
    }

    @ApplicationPath("/test")
    public static class RestActivator extends Application {

    }

    @Path("/multipart")
    public static class MultipartResource {

        @POST
        @Produces(MediaType.APPLICATION_OCTET_STREAM)
        @Consumes("multipart/mixed")
        public byte[] consume(final MultipartInput content) throws IOException {
            for (InputPart part : content.getParts()) {
                if (part.getMediaType().toString().equals("image/png")) {
                    return part.getBody(byte[].class, null);
                }
            }
            throw new WebApplicationException("Failed to post data: " + content);
        }
    }
}
