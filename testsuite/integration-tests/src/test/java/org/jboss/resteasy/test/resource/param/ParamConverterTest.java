package org.jboss.resteasy.test.resource.param;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterClient;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterDefaultClient;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterDefaultResource;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterPOJO;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterPOJOConverter;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterPOJOConverterProvider;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for ParamConverter
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ParamConverterTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ParamConverterTest.class.getSimpleName());
        war.addClass(ParamConverterPOJOConverter.class);
        war.addClass(ParamConverterPOJO.class);
        war.addClass(ParamConverterDefaultClient.class);
        war.addClass(ParamConverterClient.class);
        return TestUtil.finishContainerPrepare(war, null, ParamConverterPOJOConverterProvider.class,
                ParamConverterResource.class, ParamConverterDefaultResource.class);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(ParamConverterTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Set specific values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIt() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ParamConverterClient proxy = client.target(generateBaseUrl()).proxy(ParamConverterClient.class);
        proxy.put("pojo", "pojo", "pojo", "pojo");
        client.close();
    }

    /**
     * @tpTestDetails Check default values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDefault() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ParamConverterDefaultClient proxy = client.target(generateBaseUrl()).proxy(ParamConverterDefaultClient.class);
        proxy.put();
        client.close();
    }
}
