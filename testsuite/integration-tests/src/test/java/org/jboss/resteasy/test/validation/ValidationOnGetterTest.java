package org.jboss.resteasy.test.validation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.validation.resource.ValidationOnGetterNotNullOrOne;
import org.jboss.resteasy.test.validation.resource.ValidationOnGetterNotNullOrOneStringBeanValidator;
import org.jboss.resteasy.test.validation.resource.ValidationOnGetterStringBean;
import org.jboss.resteasy.test.validation.resource.ValidationOnGetterValidateExecutableResource;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooReaderWriter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test getter validation
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ValidationOnGetterTest {
    ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ValidationOnGetterTest.class.getSimpleName())
                .addClasses(ValidationOnGetterNotNullOrOne.class, ValidationOnGetterNotNullOrOneStringBeanValidator.class, ValidationOnGetterStringBean.class);
        return TestUtil.finishContainerPrepare(war, null, ValidationOnGetterValidateExecutableResource.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build().register(ValidationCoreFooReaderWriter.class);
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ValidationOnGetterTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test xml media type.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetter() throws Exception {
        Response response = client.target(generateURL("/resource/executable/getter")).request().accept(MediaType.APPLICATION_XML).get();
        ViolationReport report = response.readEntity(ViolationReport.class);
        TestUtil.countViolations(report, 0, 1, 0, 0, 0);
        response.close();
    }
}
