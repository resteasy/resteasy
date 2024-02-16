package org.jboss.resteasy.test.resource.param;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterClient;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterDefaultClient;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterDefaultIntegerResource;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterIntegerConverter;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterIntegerConverterProvider;
import org.jboss.resteasy.test.resource.param.resource.ParamConverterIntegerResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Parameters
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for ParamConverter RESTEASY-2222
 * @tpSince RESTEasy 3.7.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class PrimitiveParamConverterTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PrimitiveParamConverterTest.class.getSimpleName());
        war.addClass(ParamConverterIntegerConverter.class);
        war.addClass(ParamConverterDefaultClient.class);
        war.addClass(ParamConverterClient.class);
        return TestUtil.finishContainerPrepare(war, null, ParamConverterIntegerConverterProvider.class,
                ParamConverterIntegerResource.class, ParamConverterDefaultIntegerResource.class);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(PrimitiveParamConverterTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Set specific values
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testIt() throws Exception {
        ResteasyClient client = new ResteasyClientBuilderImpl().build();
        ParamConverterClient proxy = client.target(generateBaseUrl()).proxy(ParamConverterClient.class);
        proxy.put("4", "4", "4", "4");
        client.close();
    }

    /**
     * @tpTestDetails Check default values
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testDefault() throws Exception {
        ResteasyClient client = new ResteasyClientBuilderImpl().build();
        ParamConverterDefaultClient proxy = client.target(generateBaseUrl()).proxy(ParamConverterDefaultClient.class);
        proxy.put();
        client.close();
    }
}
