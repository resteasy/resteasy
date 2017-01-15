package org.jboss.resteasy.test.resource.param;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.resource.param.resource.SerializableWithParametersObject;
import org.jboss.resteasy.test.resource.param.resource.SerializableWithParametersResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Invocation;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Regression test for RESTEASY-839
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SerializableWithParametersTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SerializableWithParametersTest.class.getSimpleName());
        war.addClass(SerializableWithParametersObject.class);
        war.addAsResource(SerializableWithParametersTest.class.getPackage(), "javax.ws.rs.ext.Providers", "META-INF/services/javax.ws.rs.ext.Providers");
        return TestUtil.finishContainerPrepare(war, null, SerializableWithParametersResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SerializableWithParametersTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Get serializable object.
     *                Test was updated by RESTEASY-1269 in this
     *                commit: https://github.com/resteasy/Resteasy/commit/bb8657c9808763d4c4b9227f6a2fcf47b9146636
     *                Serializable provider was deprecated in RESTEASY-1461
     * @tpSince RESTEasy 3.0.16
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testSerialize() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().register(org.jboss.resteasy.plugins.providers.SerializableProvider.class).build();
        Invocation.Builder request = client.target(generateURL("/test")).request();
        SerializableWithParametersObject foo = request.get(SerializableWithParametersObject.class);
        Assert.assertEquals("Wrong response", new SerializableWithParametersObject("abc"), foo);
        client.close();
    }
}
