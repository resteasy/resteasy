package org.jboss.resteasy.test.cdi.modules;

import java.net.URI;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.modules.resource.CDIModulesLibraryProvider;
import org.jboss.resteasy.test.cdi.modules.resource.CDIModulesLibraryProviderResource;
import org.jboss.resteasy.test.cdi.modules.resource.CDIModulesSingletonsApplication;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test that a provider in an EAR/lib library is discovered. The library has no {@code beans.xml}, so the provider is
 * only a CDI bean because {@code @Provider} is a bean defining annotation.
 * <p>
 * The EAR has a second WAR whose {@link CDIModulesSingletonsApplication} returns an instance of the provider from
 * {@code getSingletons()}. The WARs share the CDI container, and the modules are initialized in order, so the first WAR
 * is deployed before the second one.
 * </p>
 *
 * @since 8.0
 */
@ArquillianTest
@RunAsClient
public class EarLibProviderTest {
    private static final String SCANNED_WAR = EarLibProviderTest.class.getSimpleName();
    private static final String SINGLETONS_WAR = EarLibProviderTest.class.getSimpleName() + "Singletons";

    @ArquillianResource
    private URI baseUri;

    @Deployment(testable = false)
    public static EnterpriseArchive createTestArchive() {
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "library.jar")
                .addClasses(CDIModulesLibraryProvider.class);
        final WebArchive scannedWar = ShrinkWrap.create(WebArchive.class, SCANNED_WAR + ".war")
                .addClasses(TestApplication.class, CDIModulesLibraryProviderResource.class);
        final WebArchive singletonsWar = ShrinkWrap.create(WebArchive.class, SINGLETONS_WAR + ".war")
                .addClasses(CDIModulesSingletonsApplication.class, CDIModulesLibraryProviderResource.class);
        return ShrinkWrap.create(EnterpriseArchive.class, EarLibProviderTest.class.getSimpleName() + ".ear")
                .addAsLibrary(jar)
                .addAsModule(scannedWar)
                .addAsModule(singletonsWar)
                .setApplicationXML(new StringAsset("""
                        <application xmlns="https://jakarta.ee/xml/ns/jakartaee" version="10">
                            <initialize-in-order>true</initialize-in-order>
                            <module>
                                <web>
                                    <web-uri>%1$s.war</web-uri>
                                    <context-root>%1$s</context-root>
                                </web>
                            </module>
                            <module>
                                <web>
                                    <web-uri>%2$s.war</web-uri>
                                    <context-root>%2$s</context-root>
                                </web>
                            </module>
                        </application>
                        """.formatted(SCANNED_WAR, SINGLETONS_WAR)));
    }

    /**
     * The provider in the library is discovered and its {@code @Context} field is injected.
     */
    @Test
    public void libraryProvider() throws Exception {
        try (
                Client client = ClientBuilder.newClient();
                Response response = client.target(generateUri(SCANNED_WAR)).request().get()) {
            assertProviderInvoked(response);
        }
    }

    /**
     * The provider instance returned from {@code getSingletons()} in the second WAR has its {@code @Context} field
     * injected, although the first WAR registered the provider class.
     */
    @Test
    public void singletonProvider() throws Exception {
        try (
                Client client = ClientBuilder.newClient();
                Response response = client.target(generateUri(SINGLETONS_WAR)).request().get()) {
            assertProviderInvoked(response);
        }
    }

    /**
     * The {@code @Context Application} injected in the first WAR is its own application, not the one deployed by the
     * second WAR, although the WARs share the CDI container.
     */
    @Test
    public void scannedApplication() throws Exception {
        assertApplicationProperty(SCANNED_WAR, "null");
    }

    /**
     * The {@code @Context Application} injected in the second WAR is its own application, not the one deployed by the
     * first WAR, although the WARs share the CDI container.
     */
    @Test
    public void singletonsApplication() throws Exception {
        assertApplicationProperty(SINGLETONS_WAR, CDIModulesSingletonsApplication.PROPERTY_VALUE);
    }

    private void assertApplicationProperty(final String contextRoot, final String expected) throws Exception {
        try (
                Client client = ClientBuilder.newClient();
                Response response = client.target(generateUri(contextRoot, "library-provider/application")).request()
                        .get()) {
            Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertEquals(expected, response.readEntity(String.class));
        }
    }

    private URI generateUri(final String contextRoot) throws Exception {
        return generateUri(contextRoot, "library-provider");
    }

    private URI generateUri(final String contextRoot, final String path) throws Exception {
        return TestUtil.generateUri(baseUri, "%s/%s".formatted(contextRoot, path));
    }

    private static void assertProviderInvoked(final Response response) {
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        final String path = response.getHeaderString(CDIModulesLibraryProvider.HEADER);
        Assertions.assertNotNull(path, "The provider in the library was not invoked");
        Assertions.assertTrue(path.endsWith("/library-provider"), () -> "Unexpected request path " + path);
    }
}
