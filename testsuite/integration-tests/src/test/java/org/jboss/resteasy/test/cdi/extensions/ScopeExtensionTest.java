package org.jboss.resteasy.test.cdi.extensions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.logging.Logger;

import jakarta.enterprise.inject.spi.Extension;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.cdi.extensions.resource.ScopeExtensionObsolescent;
import org.jboss.resteasy.test.cdi.extensions.resource.ScopeExtensionObsolescentAfterThreeUses;
import org.jboss.resteasy.test.cdi.extensions.resource.ScopeExtensionObsolescentAfterTwoUses;
import org.jboss.resteasy.test.cdi.extensions.resource.ScopeExtensionPlannedObsolescenceContext;
import org.jboss.resteasy.test.cdi.extensions.resource.ScopeExtensionPlannedObsolescenceExtension;
import org.jboss.resteasy.test.cdi.extensions.resource.ScopeExtensionPlannedObsolescenceScope;
import org.jboss.resteasy.test.cdi.extensions.resource.ScopeExtensionResource;
import org.jboss.resteasy.test.cdi.util.Utilities;
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
 * @tpTestCaseDetails CDIScopeExtensionTest tests that Resteasy interacts well with beans in
 *                    a user defined scope.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
public class ScopeExtensionTest {
    @Inject
    Logger log;

    static Client client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ScopeExtensionTest.class.getSimpleName());
        war.addClasses(UtilityProducer.class, Utilities.class, PortProviderUtil.class)
                .addClasses(ScopeExtensionPlannedObsolescenceExtension.class, ScopeExtensionPlannedObsolescenceScope.class)
                .addClasses(ScopeExtensionPlannedObsolescenceContext.class, ScopeExtensionResource.class)
                .addClasses(ScopeExtensionObsolescent.class, ScopeExtensionObsolescentAfterTwoUses.class,
                        ScopeExtensionObsolescentAfterThreeUses.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml")
                .addAsServiceProvider(Extension.class, ScopeExtensionPlannedObsolescenceExtension.class);
        return war;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ScopeExtensionTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Beans in scope test.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testObsolescentScope() throws Exception {
        client = ClientBuilder.newClient();

        log.info("starting testScope()");
        WebTarget base = client.target(generateURL("/extension/setup/"));
        Response response = base.request().post(Entity.text(new String()));
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        base = client.target(generateURL("/extension/test1/"));
        response = base.request().post(Entity.text(new String()));
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        base = client.target(generateURL("/extension/test2/"));
        response = base.request().post(Entity.text(new String()));
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        client.close();
    }
}
