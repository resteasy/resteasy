package org.jboss.resteasy.test.providers.jackson2.multipart;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class Jackson2MultipartFormTest {

    static ResteasyClient client;

    @BeforeClass
    public static void init() {
        client = new ResteasyClientBuilder().build();
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(Jackson2MultipartFormTest.class.getSimpleName());
        war.addClass(Jackson2MultipartFormTest.class);
        return TestUtil.finishContainerPrepare(war, null, JsonFormResource.class, JsonUser.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, Jackson2MultipartFormTest.class.getSimpleName());
    }

    @Test
    public void testJacksonProxy() {
        JsonForm proxy = client.target(generateURL("")).proxy(JsonForm.class);
        String name = proxy.putMultipartForm(new JsonFormResource.Form(new JsonUser("bill")));
        Assert.assertEquals("bill", name);
    }
}
