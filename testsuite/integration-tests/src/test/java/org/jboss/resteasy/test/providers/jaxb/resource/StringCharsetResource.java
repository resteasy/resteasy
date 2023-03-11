package org.jboss.resteasy.test.providers.jaxb.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.StreamingOutput;

@Path("/charset")
public class StringCharsetResource {
    @GET
    @Path("test.xml")
    @Produces("application/xml")
    public StringCharsetRespond getTestXML() {
        String test = "Test " + (char) 353 + (char) 273 + (char) 382 + (char) 269;
        return new StringCharsetRespond(test);
    }

    @GET
    @Path("test.json")
    @Produces("application/json;charset=UTF-8")
    public StringCharsetRespond getTestJSON() {
        return new StringCharsetRespond("Test " + (char) 353 + (char) 273 + (char) 382 + (char) 269);
    }

    @GET
    @Path("test.html")
    @Produces("text/html;charset=UTF-8")
    public String getTestHTML() {
        return "<html><body>Test " + (char) 353 + (char) 273 + (char) 382 + (char) 269 + "</body></html>";
    }

    @GET
    @Path("test_stream.html")
    @Produces("text/html;charset=UTF-8")
    public StreamingOutput getTestStream() {
        return new StreamingOutput() {
            public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                PrintStream writer = new PrintStream(outputStream, true, StandardCharsets.UTF_8.name());
                writer.println("<html><body>Test " + (char) 353 + (char) 273 + (char) 382 + (char) 269 + "</body></html>");
            }
        };
    }

}
