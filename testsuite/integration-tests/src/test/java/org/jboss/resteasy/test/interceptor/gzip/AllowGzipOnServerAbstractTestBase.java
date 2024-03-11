package org.jboss.resteasy.test.interceptor.gzip;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.shrinkwrap.api.Archive;

/**
 * Abstract base class for tests with gzip enabled on server side.
 *
 * This abstract class provides deployments and starts and stops custom server.
 *
 * This abstract class is extended by:
 * AllowGzipOnServerAllowGzipOnClientTest
 * AllowGzipOnServerNotAllowGzipOnClientTest
 */
public class AllowGzipOnServerAbstractTestBase extends GzipAbstractTestBase {

    //keep in sync with offset in arquillian.xml
    protected static String gzipServerBaseUrl = "http://" + PortProviderUtil.getHost() + ":" + PortProviderUtil.getPort();

    /**
     * Deployment with jakarta.ws.rs.ext.Providers file, that contains gzip interceptor definition
     */
    @Deployment(name = WAR_WITH_PROVIDERS_FILE, testable = false)
    public static Archive<?> createWebDeploymentWithGzipProvidersFile() {
        return createWebArchive(WAR_WITH_PROVIDERS_FILE, true, true);
    }

    /**
     * Deployment without any jakarta.ws.rs.ext.Providers file
     */
    @Deployment(name = WAR_WITHOUT_PROVIDERS_FILE, testable = false)
    public static Archive<?> createWebDeploymentWithoutGzipProvidersFile() {
        return createWebArchive(WAR_WITHOUT_PROVIDERS_FILE, false, true);
    }
}
