package org.jboss.resteasy.test.resource.param;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamMyClass;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamParamConverterProvider;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamParamConverterTestService;
import org.jboss.resteasy.test.resource.param.resource.HeaderParamParamConverterTestServiceImpl;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import jakarta.ws.rs.client.ClientBuilder;

/**
 * Provides a ParamConverter for an input parameter using annotation @HeaderParam
 */
@RunWith(Arquillian.class)
@RunAsClient
public class HeaderParamParamConverterTest {
    private static String testSimpleName = HeaderParamParamConverterTest.class.getSimpleName();
    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(testSimpleName);
        war.addClasses(HeaderParamMyClass.class,
                HeaderParamParamConverterProvider.class,
                HeaderParamParamConverterTestServiceImpl.class,
                HeaderParamParamConverterTestService.class);
        return TestUtil.finishContainerPrepare(war, null, null);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, testSimpleName);
    }
    private static String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(testSimpleName);
    }

    @Test
    public void testOne() throws Exception {
        HeaderParamMyClass header = new HeaderParamMyClass();
        header.setValue("someValue");
        // test
        ResteasyClient proxyClient = (ResteasyClient) ClientBuilder.newClient();
        HeaderParamParamConverterTestService service = proxyClient.target(generateBaseUrl())
                .proxyBuilder(HeaderParamParamConverterTestService .class).build();

        Assert.assertTrue(service.test(header));
        proxyClient.close();
    }
}
