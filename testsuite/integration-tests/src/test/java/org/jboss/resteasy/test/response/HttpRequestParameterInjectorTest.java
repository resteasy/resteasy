package org.jboss.resteasy.test.response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.response.resource.HttpRequestParameterInjectorClassicParam;
import org.jboss.resteasy.test.response.resource.HttpRequestParameterInjectorParamFactoryImpl;
import org.jboss.resteasy.test.response.resource.HttpRequestParameterInjectorResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;


/**
 * @tpSubChapter Localization
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for InjectorFactoryImpl. It is used for new type of parameters in resource.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class HttpRequestParameterInjectorTest {

    private static final String DEPLOYMENT_NAME = "app";

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(DEPLOYMENT_NAME);
        war.addClass(HttpRequestParameterInjectorClassicParam.class);
        return TestUtil.finishContainerPrepare(war, null, HttpRequestParameterInjectorResource.class,
                HttpRequestParameterInjectorParamFactoryImpl.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, DEPLOYMENT_NAME);
    }

    /**
     * @tpTestDetails New Client usage.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCustomInjectorFactory() throws Exception {
        Client client = ClientBuilder.newClient();

        String getResult = client.target(generateURL("/foo")).queryParam("param", "getValue").request()
                .accept("text/plain").get().readEntity(String.class);
        Assert.assertEquals("getValue, getValue, ", getResult);


        Form form = new Form().param("param", "postValue");
        String postResult = client.target(generateURL("/foo")).request()
                .accept("text/plain").post(Entity.form(form)).readEntity(String.class);
        Assert.assertEquals("postValue, , postValue", postResult);

        client.close();
    }

}
