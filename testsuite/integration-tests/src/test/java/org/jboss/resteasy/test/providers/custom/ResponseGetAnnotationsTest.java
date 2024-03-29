package org.jboss.resteasy.test.providers.custom;

import java.lang.annotation.Annotation;
import java.util.Calendar;
import java.util.Date;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.providers.custom.resource.ResponseGetAnnotationsAnnotatedClass;
import org.jboss.resteasy.test.providers.custom.resource.ResponseGetAnnotationsDateClientReaderWriter;
import org.jboss.resteasy.test.providers.custom.resource.ResponseGetAnnotationsDateContainerReaderWriter;
import org.jboss.resteasy.test.providers.custom.resource.ResponseGetAnnotationsResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ResponseGetAnnotationsTest {

    static Client client;

    @BeforeAll
    public static void setup() throws Exception {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResponseGetAnnotationsTest.class.getSimpleName());
        war.addClasses(ResponseGetAnnotationsAnnotatedClass.class);
        return TestUtil.finishContainerPrepare(war, null, ResponseGetAnnotationsResource.class,
                ResponseGetAnnotationsDateContainerReaderWriter.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResponseGetAnnotationsTest.class.getSimpleName());
    }

    @AfterAll
    public static void close() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Client registers it's own instance of Date MessageBodyReader and MessageBodyWriter. Server gets
     *                registered provider to Read and Write responses with Date and Annotations objects. Client sends POST
     *                request with
     *                Date entity and expects response with Date and Annotations from a test class.
     * @tpPassCrit The date and annotations are present in the response
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetAnnotations() {
        Date date = Calendar.getInstance().getTime();
        String entity = ResponseGetAnnotationsDateContainerReaderWriter.dateToString(date);
        StringBuilder sb = new StringBuilder();
        ResponseGetAnnotationsDateClientReaderWriter rw = new ResponseGetAnnotationsDateClientReaderWriter(sb);

        Response response = client.target(generateURL("/entity")).register(rw).request().post(Entity.text(entity));

        Date responseDate = response.readEntity(Date.class);
        Assertions.assertTrue(date.equals(responseDate),
                "The date in the response doesn't match the expected one");

        Annotation[] annotations = ResponseGetAnnotationsAnnotatedClass.class.getAnnotations();
        for (Annotation annotation : annotations) {
            String name = annotation.annotationType().getName();
            Assertions.assertTrue(sb.toString().contains(name),
                    "The response doesn't contain the expected annotation");
        }
    }

}
