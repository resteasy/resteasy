package org.jboss.resteasy.embedded.test.core.interceptors;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.embedded.test.AbstractBootstrapTest;
import org.jboss.resteasy.embedded.test.TestApplication;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextArrayListEntityProvider;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextFirstReaderInterceptor;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextFirstWriterInterceptor;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextLinkedListEntityProvider;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextResource;
import org.jboss.resteasy.embedded.test.core.interceptors.resource.ReaderContextSecondReaderInterceptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter
 * @tpChapter Embedded Containers
 * @tpTestCaseDetails Basic test for reated context
 * @tpSince RESTEasy 4.1.0
 */
public class ReaderContextTest extends AbstractBootstrapTest {

    @BeforeEach
    public void before() throws Exception {
        start(new TestApplication(ReaderContextResource.class,
                ReaderContextArrayListEntityProvider.class,
                ReaderContextLinkedListEntityProvider.class,
                ReaderContextFirstReaderInterceptor.class,
                ReaderContextFirstWriterInterceptor.class,
                ReaderContextSecondReaderInterceptor.class,
                ReaderContextSecondReaderInterceptor.class));
    }

    /**
     * @tpTestDetails Check post request.
     * @tpSince RESTEasy 4.1.0
     */
    @Test
    public void readerContextOnClientTest() {
        client = ClientBuilder.newClient();

        WebTarget target = client.target(generateURL("/resource/poststring"));
        target.register(ReaderContextFirstReaderInterceptor.class);
        target.register(ReaderContextSecondReaderInterceptor.class);
        target.register(ReaderContextArrayListEntityProvider.class);
        target.register(ReaderContextLinkedListEntityProvider.class);
        Response response = target.request().post(Entity.text("plaintext"));
        response.getHeaders().add(ReaderContextResource.HEADERNAME,
                ReaderContextFirstReaderInterceptor.class.getName());
        @SuppressWarnings("unchecked")
        List<String> list = response.readEntity(List.class);
        Assertions.assertTrue(ArrayList.class.isInstance(list), "Returned list in not instance of ArrayList");
        String entity = list.get(0);
        Assertions.assertTrue(entity.contains(ReaderContextSecondReaderInterceptor.class.getName()),
                "Wrong interceptor type in response");
        Assertions.assertTrue(entity.contains(ReaderContextSecondReaderInterceptor.class.getAnnotations()[0]
                .annotationType().getName()), "Wrong interceptor annotation in response");

        client.close();
    }
}
