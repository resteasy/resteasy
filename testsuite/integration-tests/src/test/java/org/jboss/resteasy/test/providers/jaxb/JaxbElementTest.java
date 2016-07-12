package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbElementEntityMessageReader;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbElementEntityMessageWriter;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbElementResource;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbElementReadableWritableEntity;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;


/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JaxbElementTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JaxbElementTest.class.getSimpleName());
        war.addClass(JaxbCollectionTest.class);
        return TestUtil.finishContainerPrepare(war, null, JaxbElementEntityMessageReader.class, JaxbElementEntityMessageWriter.class,
                JaxbElementResource.class, JaxbElementReadableWritableEntity.class);
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
        return PortProviderUtil.generateURL(path, JaxbElementTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Creates entity type JAXBElement and sends it to the server, user defined Writer and Reader implementing
     * custom type is used
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testWriter() {
        JAXBElement<String> element = new JAXBElement<String>(new QName(""),
                String.class, JaxbElementResource.class.getName());
        Response response = client.target(generateURL("/resource/standardwriter")).request().post(Entity.xml(element));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }


}
