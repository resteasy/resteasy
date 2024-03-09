package org.jboss.resteasy.test.interceptor.gzip;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;

/**
 * Abstract base class for tests with gzip disabled on server side.
 *
 * This abstract class provides deployments.
 *
 * This abstract class is extended by:
 * NotAllowGzipOnServerAllowGzipOnClientTest
 * NotAllowGzipOnServerNotAllowGzipOnClientTest
 */
public class NotAllowGzipOnServerAbstractTestBase extends GzipAbstractTestBase {

    /**
     * Deployment with jakarta.ws.rs.ext.Providers file, that contains gzip interceptor definition
     */
    @Deployment(name = WAR_WITH_PROVIDERS_FILE, testable = false)
    public static Archive<?> createWebDeploymentWithGzipProvidersFile() {
        return createWebArchive(WAR_WITH_PROVIDERS_FILE, true, false);
    }

    /**
     * Deployment without any jakarta.ws.rs.ext.Providers file
     */
    @Deployment(name = WAR_WITHOUT_PROVIDERS_FILE, testable = false)
    public static Archive<?> createWebDeploymentWithoutGzipProvidersFile() {
        return createWebArchive(WAR_WITHOUT_PROVIDERS_FILE, false, false);
    }
}
