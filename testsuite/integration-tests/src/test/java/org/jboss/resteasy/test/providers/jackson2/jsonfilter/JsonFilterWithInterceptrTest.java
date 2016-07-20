package org.jboss.resteasy.test.providers.jackson2.jsonfilter;

import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * @author <a href="mailto:ema@redhat.com">Jim Ma</a>
 *
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JsonFilterWithInterceptrTest {
	@ArquillianResource
	URL baseUrl;
    @Deployment(name = "default")
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive("jsonfilter");
        war.addAsManifestResource(new StringAsset("Manifest-Version: 1.0\n" + "Dependencies: com.fasterxml.jackson.jaxrs.jackson-jaxrs-json-provider\n"), "MANIFEST.MF");
        war.addClasses(ObjectFilterModifier.class, Jackson2Resource.class, Jackson2Product.class, JsonFilterWriteInterceptor.class);
        return war;
    }
    @Test
    public void testJacksonString() throws Exception {
    	Client client = new ResteasyClientBuilder().build();
        WebTarget target = client.target(baseUrl.toString()+ "products/333");
        Response response = target.request().get();
        Assert.assertTrue("filter doesn't work", !response.readEntity(String.class).contains("id"));
    }
}
