package org.jboss.resteasy.test.cdi.basic;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.cdi.basic.resource.BeanReaderWriterConfigBean;
import org.jboss.resteasy.test.cdi.basic.resource.BeanReaderWriterService;
import org.jboss.resteasy.test.cdi.basic.resource.BeanReaderWriterXFormat;
import org.jboss.resteasy.test.cdi.basic.resource.BeanReaderWriterXFormatProvider;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for custom reader-writer for bean.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class BeanReaderWriterTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(BeanReaderWriterTest.class.getSimpleName());
        war.addClasses(BeanReaderWriterConfigBean.class,
                BeanReaderWriterService.class, BeanReaderWriterXFormat.class, BeanReaderWriterXFormatProvider.class);

        war.addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");

        return war;
    }

    /**
     * @tpTestDetails Bean set constant used in custom reader-writer.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIt2() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(PortProviderUtil.generateBaseUrl(BeanReaderWriterTest.class.getSimpleName()))
                .request().get();
        String format = response.readEntity(String.class);
        Assertions.assertEquals("foo 1.1", format);
        response.close();
        client.close();
    }
}
