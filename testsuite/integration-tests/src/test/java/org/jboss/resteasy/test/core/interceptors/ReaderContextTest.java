package org.jboss.resteasy.test.core.interceptors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.core.interceptors.resource.ReaderContextArrayListEntityProvider;
import org.jboss.resteasy.test.core.interceptors.resource.ReaderContextFirstReaderInterceptor;
import org.jboss.resteasy.test.core.interceptors.resource.ReaderContextFirstWriterInterceptor;
import org.jboss.resteasy.test.core.interceptors.resource.ReaderContextLinkedListEntityProvider;
import org.jboss.resteasy.test.core.interceptors.resource.ReaderContextResource;
import org.jboss.resteasy.test.core.interceptors.resource.ReaderContextSecondReaderInterceptor;
import org.jboss.resteasy.test.core.interceptors.resource.ReaderContextSecondWriterInterceptor;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Basic test for reated context
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ReaderContextTest {

    public static final String readFromReader(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String entity = br.readLine();
        br.close();
        return entity;
    }

    static Client client;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(ReaderContextTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ReaderContextResource.class,
                ReaderContextArrayListEntityProvider.class,
                ReaderContextLinkedListEntityProvider.class,
                ReaderContextFirstReaderInterceptor.class,
                ReaderContextFirstWriterInterceptor.class,
                ReaderContextSecondReaderInterceptor.class,
                ReaderContextSecondWriterInterceptor.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ReaderContextTest.class.getSimpleName());
    }

    @AfterAll
    public static void cleanup() {
        client.close();
    }

    /**
     * @tpTestDetails Check post request.
     * @tpSince RESTEasy 3.0.16
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
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            Assertions.fail("Response was not OK (200): " + response.readEntity(String.class));
        }
        response.getHeaders().add(ReaderContextResource.HEADERNAME,
                ReaderContextFirstReaderInterceptor.class.getName());
        @SuppressWarnings("unchecked")
        List<String> list = response.readEntity(List.class);
        Assertions.assertTrue(list instanceof ArrayList, "Returned list in not instance of ArrayList");
        String entity = list.get(0);
        Assertions.assertTrue(entity.contains(ReaderContextSecondReaderInterceptor.class.getName()),
                "Wrong interceptor type in response");
        Assertions.assertTrue(entity.contains(ReaderContextSecondReaderInterceptor.class.getAnnotations()[0]
                .annotationType().getName()),
                "Wrong interceptor annotation in response");

        client.close();
    }
}
