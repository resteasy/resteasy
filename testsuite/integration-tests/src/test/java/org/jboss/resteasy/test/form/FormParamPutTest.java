package org.jboss.resteasy.test.form;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.form.resource.FormParamPutResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter Form tests
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for JBEAP-982
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class FormParamPutTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(FormParamPutTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, FormParamPutResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, FormParamPutTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test with query param and without query param
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test1() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget put = client.target(generateURL("/test/42?foo=xyz"));

        Form form = new Form().param("formParam", "Weinan Li");
        Response response = put.request().put(Entity.form(form));
        response.close();

        WebTarget get = client.target(generateURL("/test"));
        assertEquals("Weinan Li", get.request().get().readEntity(String.class));
    }
}
