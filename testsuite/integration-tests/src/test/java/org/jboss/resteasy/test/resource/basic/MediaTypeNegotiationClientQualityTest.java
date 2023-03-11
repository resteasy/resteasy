package org.jboss.resteasy.test.resource.basic;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.MessageBodyWriter;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class MediaTypeNegotiationClientQualityTest {

    @Produces({ "application/x;qs=0.9", "application/y;qs=0.7" })
    public static class CustomMessageBodyWriter1 implements MessageBodyWriter<Object> {

        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return true;
        }

        @Override
        public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return -1;
        }

        @Override
        public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                throws IOException, WebApplicationException {
        }

    }

    public static class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
        @Override
        public Response toResponse(NotFoundException notFoundException) {
            return Response.status(Status.NOT_FOUND).entity(new Object()).build();
        }
    }

    private static Client client;
    private static final String DEP = "MediaTypeNegotiationClientQualityTest";

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(DEP);
        return TestUtil.finishContainerPrepare(war, null, CustomMessageBodyWriter1.class,
                NotFoundExceptionMapper.class);
    }

    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void cleanup() {
        client.close();
    }

    private String generateURL() {
        return PortProviderUtil.generateBaseUrl(DEP);
    }

    @Test
    public void testClientQuality() throws Exception {
        Invocation.Builder request = client.target(generateURL()).path("echo").request("application/x;q=0.7",
                "application/y;q=0.9");
        Response response = request.get();
        try {
            Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
            MediaType mediaType = response.getMediaType();
            Assert.assertEquals("application", mediaType.getType());
            Assert.assertEquals("y", mediaType.getSubtype());
        } finally {
            response.close();
        }
    }

}
