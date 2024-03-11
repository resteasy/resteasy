package org.jboss.resteasy.test.providers.atom;

import java.lang.reflect.Field;
import java.util.Iterator;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.atom.resource.JAXBContextFinderAtomServer;
import org.jboss.resteasy.test.providers.atom.resource.JAXBContextFinderCustomerAtom;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Atom provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test integration of atom provider and JAXB Context finder
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class JAXBContextFinderTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JAXBContextFinderTest.class.getSimpleName());
        war.addClass(JAXBContextFinderCustomerAtom.class);
        return TestUtil.finishContainerPrepare(war, null, JAXBContextFinderAtomServer.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JAXBContextFinderTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test new client
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAtomFeedNewClient() throws Exception {
        Response response = client.target(generateURL("/atom/feed")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Feed feed = response.readEntity(Feed.class);
        Iterator<Entry> it = feed.getEntries().iterator();
        Entry entry1 = it.next();
        Entry entry2 = it.next();
        Field field = Entry.class.getDeclaredField("finder");
        field.setAccessible(true);
        Assertions.assertNotNull(field.get(entry1), "First feet is not correct");
        Assertions.assertEquals(field.get(entry1), field.get(entry2), "Second feet is not correct");
        response.close();
    }
}
