package org.jboss.resteasy.test.validation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooReaderWriter;
import org.jboss.resteasy.test.validation.resource.ValidationHibernateI18NResource;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.hamcrest.core.StringStartsWith.startsWith;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for internationalization of hibernate validator
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ValidationHibernateI18NTest {
    static final String WRONG_ERROR_MSG = "Expected validation error is not in response";
    ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ValidationHibernateI18NTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ValidationHibernateI18NResource.class);
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
        return PortProviderUtil.generateURL(path, ValidationHibernateI18NTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test two languages.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testI18NSetAcceptLanguage() throws Exception {
        doTestI18NSetAcceptLanguage("fr", "la taille doit être");
        doTestI18NSetAcceptLanguage("es", "el tama\u00F1o tiene que estar entre");
    }

    protected void doTestI18NSetAcceptLanguage(String locale, String expectedMessage) throws Exception {
        Response response = client.target(generateURL("/test")).request()
                .accept(MediaType.APPLICATION_XML).header(HttpHeaderNames.ACCEPT_LANGUAGE, locale).get();

        ViolationReport report = response.readEntity(ViolationReport.class);
        String message = report.getReturnValueViolations().iterator().next().getMessage();
        TestUtil.countViolations(report, 0, 0, 0, 0, 1);
        Assert.assertThat(WRONG_ERROR_MSG, message, startsWith(expectedMessage));
        response.close();
    }
}
