package org.jboss.resteasy.utils;

import org.jboss.resteasy.client.ClientRequest; //@cs-: clientrequest (Method for testing deprecated ClientRequest)
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.ProxyFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Utility class that provides a port number for the Resteasy embedded container.
 */
public class PortProviderUtil {
    private static final int DEFAULT_PORT = 8080;

    private static final String PORT_ENV_VAR_NAME = "RESTEASY_PORT";

    private static final String PORT_PROPERTY_NAME = "org.jboss.resteasy.port";

    private static final String HOST_PROPERTY_NAME = "node";

    private static int port;

    // HOST_PROPERTY_NAME is enforced by maven-enforcer-plugin
    private static String host = System.getProperty(HOST_PROPERTY_NAME);

    public static final String ASYNC_JOB_SERVICE_CONTEXT_KEY = "resteasy.async.job.service.enabled";

    private static boolean ipv6 = Boolean.parseBoolean(System.getProperty("ipv6"));

    /**
     * Initialize port.
     */
    static {
        // Look up the configured port number, first checking an environment variable (RESTEASY_PORT),
        // then a system property (org.jboss.resteasy.port), and finally the default port (8080).
        boolean portSpecificInit = false;
        port = DEFAULT_PORT;

        String property = System.getenv(PORT_ENV_VAR_NAME);
        if (property != null) {
            try {
                port = Integer.parseInt(property);
                portSpecificInit = true;
            } catch (NumberFormatException e) {
            }
        }

        if (!portSpecificInit) {
            property = System.getProperty(PORT_PROPERTY_NAME);
            if (property != null) {
                try {
                    port = Integer.parseInt(property);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    /**
     * Create a Resteasy deprecated ClientRequest object using the configured port.
     *
     * @param path the request path
     * @return the ClientRequest object
     */
    public static ClientRequest createClientRequest(String path, String testName) {
        return new ClientRequest(generateURL(path, testName));
    }

    public static ClientRequest createClientRequest(ClientRequestFactory factory, String path, String testName) {
        return factory.createRequest(generateURL(path, testName));
    }

    /**
     * Create a Resteasy client proxy with an empty base request path.
     *
     * @param clazz the client interface class
     * @return the proxy object
     */
    public static <T> T createProxy(Class<T> clazz, String testName) {
        return createProxy(clazz, "");
    }

    /**
     * Create a Resteasy client proxy.
     *
     * @param clazz the client interface class
     * @return the proxy object
     * @param path the base request path
     */
    public static <T> T createProxy(Class<T> clazz, String path, String testName) {
        return ProxyFactory.create(clazz, generateURL(path, testName));
    }

    /**
     * Create a URI for the provided path, using the configured port
     *
     * @param path the request path
     * @return a full URI
     */
    public static URI createURI(String path, String testName) {
        return URI.create(generateURL(path, testName));
    }

    /**
     * Create a URL for the provided path, using the configured port
     *
     * @param path the request path
     * @return a full URL
     */
    public static URL createURL(String path, String testName) throws MalformedURLException {
        return new URL(generateURL(path, testName));
    }

    /**
     * Generate a base URL incorporating the configured port.
     *
     * @return a full URL
     */
    public static String generateBaseUrl(String testName) {
        return generateURL("", testName);
    }

    /**
     * Generate a URL incorporating the configured port.
     *
     * @param path the path
     * @param testName the test name 
     * @return a full URL
     */
    public static String generateURL(String path, String testName) {
        return generateURL(path, testName,  getHost(), getPort());
    }
    /**
     * Generate a URL with port, hostname
     *
     * @param path the path
     * @return a full URL
     */
    public static String generateURL(String path, String testName, String hostName, int port) {
        // ipv4
        if (!ipv6) {
            return String.format("http://%s:%d/%s%s", hostName, port, testName, path);
        }
        // ipv6
        return String.format("http://[%s]:%d/%s%s", hostName, port, testName, path);
    }

    /**
     * Get port.
     *
     * @return The port number
     */
    public static int getPort() {
        return port;
    }

    /**
     * Get host IP.
     *
     * @return The host IP
     */
    public static String getHost() {
        return host;
    }

    /**
     * Get information about IPv6 connectivity.
     *
     * @return IPv6 connectivity.
     */
    public static boolean isIpv6() {
        return ipv6;
    }
}
