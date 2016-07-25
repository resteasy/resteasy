package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jaxb.resource.AbstractJaxbClassPerson;
import org.jboss.resteasy.test.providers.jaxb.resource.AbstractJaxbClassCustomer;
import org.jboss.resteasy.test.providers.jaxb.resource.AbstractJaxbClassResource;
import org.jboss.resteasy.test.providers.jaxb.resource.AbstractJaxbClassCompanyCustomer;
import org.jboss.resteasy.test.providers.jaxb.resource.AbstractJaxbClassPrivatCustomer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class AbstractJaxbClassTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(AbstractJaxbClassTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, AbstractJaxbClassCompanyCustomer.class, AbstractJaxbClassCustomer.class,
                AbstractJaxbClassPerson.class, AbstractJaxbClassPrivatCustomer.class, AbstractJaxbClassResource.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, AbstractJaxbClassTest.class.getSimpleName());
    }

    private static final String customerXml = "<?xml version=\"1.0\"?>\n"
            + "<abstractJaxbClassPrivatCustomer>\n"
            + "<nachname>Test</nachname>\n"
            + "<vorname>Theo</vorname>\n"
            + "<seit>2001-01-31T00:00:00+01:00</seit>\n"
            + "<adresse><plz>76133</plz><ort>Karlsruhe</ort><strasse>Moltkestrasse</strasse><hausnr>31</hausnr></adresse>\n"
            + "</abstractJaxbClassPrivatCustomer>";

    /**
     * @tpTestDetails Test for Abstract jaxb class with @XmlSeeAlso annotation
     * @tpInfo RESTEASY-126
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPost() throws Exception {
        ResteasyWebTarget target = client.target(generateURL(""));
        String xmlInput = "<?xml version=\"1.0\"?><abstractJaxbClassPerson><name>bill</name></abstractJaxbClassPerson>";
        Response response = target.request().post(Entity.xml(xmlInput));
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        ResteasyWebTarget target2 = client.target(generateURL("/customer"));
        Response response2 = target2.request().post(Entity.entity(customerXml, "application/xml"));
        Assert.assertEquals(204, response2.getStatus());
        response2.close();
    }

}
