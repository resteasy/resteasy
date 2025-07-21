package org.jboss.resteasy.test.providers.custom;

import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.ext.ReaderInterceptor;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.ContainerConstants;
import org.jboss.resteasy.test.providers.custom.resource.DuplicateProviderRegistrationFeature;
import org.jboss.resteasy.test.providers.custom.resource.DuplicateProviderRegistrationFilter;
import org.jboss.resteasy.test.providers.custom.resource.DuplicateProviderRegistrationInterceptor;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Providers
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for JBEAP-4703
 * @tpSince RESTEasy 3.0.17
 */
@ExtendWith(ArquillianExtension.class)
@Tag("NotForBootableJar")
public class DuplicateProviderRegistrationTest {

    private static final String RESTEASY_002155_ERR_MSG = "Wrong count of RESTEASY002155 warning message";
    private static final String RESTEASY_002160_ERR_MSG = "Wrong count of RESTEASY002160 warning message";

    @SuppressWarnings(value = "unchecked")
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(DuplicateProviderRegistrationTest.class.getSimpleName());
        war.addClasses(DuplicateProviderRegistrationFeature.class, DuplicateProviderRegistrationFilter.class,
                TestUtil.class, DuplicateProviderRegistrationInterceptor.class, ContainerConstants.class);
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    private static int getRESTEASY002155WarningCount() {
        return TestUtil.getWarningCount("RESTEASY002155", true, DEFAULT_CONTAINER_QUALIFIER);
    }

    private static int getRESTEASY002160WarningCount() {
        return TestUtil.getWarningCount("RESTEASY002160", true, DEFAULT_CONTAINER_QUALIFIER);
    }

    /**
     * @tpTestDetails Basic test
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testDuplicateProvider() {
        int initRESTEASY002160WarningCount = getRESTEASY002160WarningCount();
        Client client = ClientBuilder.newClient();
        try {
            WebTarget webTarget = client.target("http://www.changeit.com");
            // DuplicateProviderRegistrationFeature will be registered third on the same webTarget even if
            //   webTarget.getConfiguration().isRegistered(DuplicateProviderRegistrationFeature.class)==true
            webTarget.register(DuplicateProviderRegistrationFeature.class).register(new DuplicateProviderRegistrationFeature())
                    .register(new DuplicateProviderRegistrationFeature());
        } finally {
            client.close();
        }
        Assertions.assertEquals(2, getRESTEASY002160WarningCount() - initRESTEASY002160WarningCount,
                RESTEASY_002160_ERR_MSG);
    }

    /**
     * @tpTestDetails This test is taken from jakarta.ws.rs.core.Configurable javadoc
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testFromJavadoc() {
        int initRESTEASY002155WarningCount = getRESTEASY002155WarningCount();
        int initRESTEASY002160WarningCount = getRESTEASY002160WarningCount();
        Client client = ClientBuilder.newClient();
        try {
            WebTarget webTarget = client.target("http://www.changeit.com");
            webTarget.register(DuplicateProviderRegistrationInterceptor.class, ReaderInterceptor.class);
            webTarget.register(DuplicateProviderRegistrationInterceptor.class); // Rejected by runtime.
            webTarget.register(new DuplicateProviderRegistrationInterceptor()); // Rejected by runtime.
            webTarget.register(DuplicateProviderRegistrationInterceptor.class, 6500); // Rejected by runtime.

            webTarget.register(new DuplicateProviderRegistrationFeature());
            webTarget.register(new DuplicateProviderRegistrationFeature()); // rejected by runtime.
            webTarget.register(DuplicateProviderRegistrationFeature.class); // rejected by runtime.
            webTarget.register(DuplicateProviderRegistrationFeature.class, Feature.class); // Rejected by runtime.
        } finally {
            client.close();
        }
        Assertions.assertEquals(4, getRESTEASY002155WarningCount() - initRESTEASY002155WarningCount,
                RESTEASY_002155_ERR_MSG);
        Assertions.assertEquals(2, getRESTEASY002160WarningCount() - initRESTEASY002160WarningCount,
                RESTEASY_002160_ERR_MSG);
    }
}
