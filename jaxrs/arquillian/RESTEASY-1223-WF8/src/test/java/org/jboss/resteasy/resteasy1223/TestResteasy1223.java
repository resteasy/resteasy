package org.jboss.resteasy.resteasy1223;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yaml.snakeyaml.Yaml;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@RunWith(Arquillian.class)
public class TestResteasy1223 {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy1223.war")
                .addClasses(TestApplication.class, YamlResource.class, MyNestedObject.class, MyObject.class)
                .addAsWebInfResource("web.xml").addAsManifestResource("MANIFEST.MF")
                .addAsResource("META-INF/services/javax.ws.rs.ext.Providers");
        return war;
    }

    private static final String TEST_URI = "http://localhost:8080/resteasy1223/yaml";

    @Test
    public void testGet() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget get = client.target(TEST_URI);
        Response response = get.request().get();

        assertEquals(200, response.getStatus());
        assertEquals("text/x-yaml", response.getHeaders().getFirst("Content-Type"));

        String s = response.readEntity(String.class);
        MyObject o1 = YamlResource.createMyObject();
        String s1 = new Yaml().dump(o1);
        Assert.assertEquals(s1, s);
        response.close();
    }

    @Test
    public void testPost() throws Exception {

        Client client = ClientBuilder.newClient();
        WebTarget post = client.target(TEST_URI);

        MyObject o1 = YamlResource.createMyObject();
        String s1 = new Yaml().dump(o1);
        Response response = post.request().post(Entity.entity(s1, "text/x-yaml"));

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("text/x-yaml", response.getHeaders().getFirst("Content-Type"));
        Assert.assertEquals(s1, response.readEntity(String.class));
        response.close();
    }

    @Test
    public void testBadPost() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget post = client.target(TEST_URI);
        Response response = post.request().post(Entity.entity("---! bad", "text/x-yaml"));
        Assert.assertEquals(400, response.getStatus());
        response.close();
    }

    @Test
    public void testPostList() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget post = client.target(TEST_URI + "/list");

        List<String> data = Arrays.asList("a", "b", "c");
        String s1 = new Yaml().dump(data).trim();

        Response response = post.request().post(Entity.entity(s1, "text/x-yaml"));
        Assert.assertEquals(200, response.getStatus());

        Assert.assertEquals("text/plain", response.getHeaders().getFirst("Content-Type"));
        Assert.assertEquals(s1, response.readEntity(String.class).trim());
    }

}
