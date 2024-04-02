package org.jboss.resteasy.test.cdi.inheritence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.cdi.inheritence.resource.BookProducer;
import org.jboss.resteasy.test.cdi.inheritence.resource.CDIInheritenceBook;
import org.jboss.resteasy.test.cdi.inheritence.resource.CDIInheritenceInheritanceResource;
import org.jboss.resteasy.test.cdi.inheritence.resource.CDIInheritenceSelectBook;
import org.jboss.resteasy.test.cdi.util.UtilityProducer;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails This class tests CDI inheritance (default bean - Book)
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class VanillaInheritanceTest {
    protected static final Logger log = Logger.getLogger(SpecializedInheritanceTest.class.getName());

    @SuppressWarnings(value = "unchecked")
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(VanillaInheritanceTest.class.getSimpleName());
        war.addClasses(UtilityProducer.class, CDIInheritenceBook.class, CDIInheritenceSelectBook.class,
                CDIInheritenceInheritanceResource.class, BookProducer.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    /**
     * @tpTestDetails Client get request. Resource check inheritance bean on server.
     * @tpPassCrit Response status should not contain error.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testVanilla() throws Exception {
        Client client = ClientBuilder.newClient();
        log.info("starting testVanilla()");
        WebTarget base = client.target(PortProviderUtil.generateURL("/vanilla/", VanillaInheritanceTest.class.getSimpleName()));
        Response response = base.request().get();
        log.info("Status: " + response.getStatus());
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
        client.close();
    }
}
