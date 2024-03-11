package org.jboss.resteasy.test.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.client.resource.ClientProviderStringEntityProviderReader;
import org.jboss.resteasy.test.client.resource.ClientProviderStringEntityProviderWriter;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:kanovotn@redhat.com">Katerina Novotna</a>
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Client registers it's own implementations as variants of built-in entity providers.
 *                    The entity providers registered by client have to be used before built-in ones. See spec 4.2.4:
 *                    "An implementation MUST support application-provided entity providers and MUST use those in preference to
 *                    its own pre-packaged providers when either could handle the same request."
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ClientProviderTest extends ClientTestBase {

    static Client client;

    @BeforeEach
    public void before() {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ClientProviderTest.class.getSimpleName());
        war.addClass(ClientTestBase.class);
        return TestUtil.finishContainerPrepare(war, null, ClientProviderResource.class);
    }

    @AfterEach
    public void close() {
        client.close();
    }

    @Path("/")
    @Produces("text/plain")
    @Consumes("text/plain")
    public static class ClientProviderResource {
        @POST
        @Path("post")
        public String post(String value) {
            return value;
        }

        @GET
        @Path("get")
        public String nothing() {
            return "OK";
        }
    }

    public static final String readFromStream(InputStream stream) throws IOException {
        InputStreamReader isr = new InputStreamReader(stream);
        return readFromReader(isr);
    }

    public static final String readFromReader(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String entity = br.readLine();
        br.close();
        return entity;
    }

    /**
     * @tpTestDetails Create WebTarget from client and register custom MessageBodyReader on it
     * @tpPassCrit Verify application provided MessageBodyReader is used instead of built-in one,
     *             verify that following request is is processed by built-in MessageBodyReader again
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void applicationDefinedMessageBodyReaderTest() {
        WebTarget base = client.target(generateURL("/") + "get");
        String result = base.register(ClientProviderStringEntityProviderReader.class).request().get(String.class);
        Assertions.assertEquals("Application defined provider reader: OK", result);

        base = client.target(generateURL("/") + "get");
        result = base.request().get(String.class);
        Assertions.assertEquals("OK", result);
    }

    /**
     * @tpTestDetails Create WebTarget from client and register custom MessageBodyWriter on it
     * @tpPassCrit Verify application provided MessageBodyWriter is used instead of built-in one,
     *             verify that following request is is processed by built-in MessageBodyWriter again
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void applicationDefinedMessageBodyWriterTest() {
        WebTarget base = client.target(generateURL("/") + "post");
        String result = base.register(ClientProviderStringEntityProviderWriter.class).request().post(Entity.text("test"),
                String.class);
        Assertions.assertEquals("Application defined provider writer: text/plain[Content-Type=text/plain]", result);

        base = client.target(generateURL("/") + "post");
        result = base.request().post(Entity.text("test"), String.class);
        Assertions.assertEquals("test", result);
    }

}
