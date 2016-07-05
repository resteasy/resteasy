package org.jboss.resteasy.test.form;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.form.resource.FormUrlEncodedResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Form tests
 * @tpChapter Integration tests
 * @tpSince EAP 7.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class FormUrlEncodedTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(FormUrlEncodedTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, FormUrlEncodedResource.class);
    }

    /**
     * @tpTestDetails Get form parameter from resource using InputStream and StreamingOutput
     * @tpSince EAP 7.0.0
     */
    @Test
    public void testPost() {
        ResteasyClient client = new ResteasyClientBuilder().build();
        WebTarget base = client.target(PortProviderUtil.generateURL("/simple", FormUrlEncodedTest.class.getSimpleName()));
        Response response = base.request().post(Entity.form(new Form().param("hello", "world")));

        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String body = response.readEntity(String.class);
        Assert.assertEquals("Wrong response content", "hello=world", body);

        response.close();
        client.close();
    }

}
