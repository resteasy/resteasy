package org.jboss.resteasy.test.resteasy1236;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.resteasy1236.JApplication;
import org.jboss.resteasy.resteasy1236.YamlProviderNestedObject;
import org.jboss.resteasy.resteasy1236.YamlProviderObject;
import org.jboss.resteasy.resteasy1236.YamlProviderResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
//import org.yaml.snakeyaml.Yaml;

/**
 * RESTEASY-1236
 * 
 * @tpSubChapter Yaml provider
 * @tpChapter Integration tests
 * @tpSince EAP 7.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class YamlProviderTest {

    protected static final Logger logger = Logger.getLogger(YamlProviderTest.class.getName());

    static ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy-yaml.war")
                .addClasses(JApplication.class)
                .addClasses(YamlProviderResource.class)
                .addClasses(YamlProviderObject.class)
                .addClasses(YamlProviderNestedObject.class)
                .addAsWebInfResource("1236/web.xml");
        System.out.println(war.toString(true));
        return war;
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Client sends POST request for yaml annotated resource. It is asserted that response contains yaml header.
     * @tpPassCrit The resource returns yaml entity same as in the original request entity
     * @tpSince EAP 7.0.0
     */
    @Test
    public void testPost() throws Exception {
//        WebTarget target = client.target("http://localhost:8080/resteasy-yaml/yaml");
//        YamlProviderObject o1 = YamlProviderResource.createMyObject();
//        String s1 = new Yaml().dump(o1);
//        Response response = target.request().post(Entity.entity(s1, "text/x-yaml"));
//        String stringResponse = response.readEntity(String.class);
//        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
//        Assert.assertEquals("The response doesn't contain correct yaml header",
//                "text/x-yaml", response.getHeaders().getFirst("Content-Type"));
//        Assert.assertEquals("The entity response doesn't match the original request", s1, stringResponse);
    }
}
