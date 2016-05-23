package org.jboss.resteasy.resteasy1141;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
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
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@RunWith(Arquillian.class)
public class TestResteasy1141 {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "resteasy1141.war")
                .addClasses(TestApplication.class, TestResource.class)
                .addAsWebInfResource("web.xml");
        System.out.println(war.toString(true));
        return war;
    }

    @Test
    public void test1() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget put = client.target("http://127.0.0.1:9090/resteasy1141/test/42?foo=xyz");

        Form form = new Form().param("formParam", "Weinan Li");
        Response response = put.request().put(Entity.form(form));
        response.close();

        WebTarget get = client.target("http://127.0.0.1:9090/resteasy1141/test");
        assertEquals("Weinan Li", get.request().get().readEntity(String.class));
    }
}
