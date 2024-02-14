package org.jboss.resteasy.test.cdi.generic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.cdi.generic.resource.Animal;
import org.jboss.resteasy.test.cdi.generic.resource.Australopithecus;
import org.jboss.resteasy.test.cdi.generic.resource.ConcreteDecorator;
import org.jboss.resteasy.test.cdi.generic.resource.ConcreteResource;
import org.jboss.resteasy.test.cdi.generic.resource.ConcreteResourceIntf;
import org.jboss.resteasy.test.cdi.generic.resource.GenericsProducer;
import org.jboss.resteasy.test.cdi.generic.resource.HierarchyHolder;
import org.jboss.resteasy.test.cdi.generic.resource.HolderBinding;
import org.jboss.resteasy.test.cdi.generic.resource.LowerBoundHierarchyHolder;
import org.jboss.resteasy.test.cdi.generic.resource.NestedHierarchyHolder;
import org.jboss.resteasy.test.cdi.generic.resource.ObjectHolder;
import org.jboss.resteasy.test.cdi.generic.resource.Primate;
import org.jboss.resteasy.test.cdi.generic.resource.UpperBoundHierarchyHolder;
import org.jboss.resteasy.test.cdi.generic.resource.VisitList;
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
 * @tpTestCaseDetails RESTEasy integration test for CDI && decorators
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ConcreteDecoratorTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive("resteasy-cdi-ejb-test");
        war.addClasses(UtilityProducer.class, VisitList.class);
        war.addClasses(ObjectHolder.class, ConcreteResourceIntf.class);
        war.addClasses(HolderBinding.class, HierarchyHolder.class);
        war.addClasses(GenericsProducer.class);
        war.addClasses(ConcreteResource.class);
        war.addClasses(NestedHierarchyHolder.class);
        war.addClasses(UpperBoundHierarchyHolder.class, LowerBoundHierarchyHolder.class);
        war.addClasses(Animal.class, Primate.class, Australopithecus.class);
        war.addClasses(ConcreteDecorator.class);
        war.addAsWebInfResource(ConcreteDecoratorTest.class.getPackage(), "concrete_beans.xml", "beans.xml");
        return war;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, "resteasy-cdi-ejb-test");
    }

    /**
     * @tpTestDetails Run REST point method and check execution of decorators.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testConcreteConcreteDecorator() throws Exception {
        Client client = ClientBuilder.newClient();

        WebTarget base = client.target(generateURL("/concrete/decorators/clear"));
        Response response = base.request().get();
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        base = client.target(generateURL("/concrete/decorators/execute"));
        response = base.request().get();
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        base = client.target(generateURL("/concrete/decorators/test"));
        response = base.request().get();
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        client.close();
    }
}
