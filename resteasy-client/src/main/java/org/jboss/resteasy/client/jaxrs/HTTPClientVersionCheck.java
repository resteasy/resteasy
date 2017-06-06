package org.jboss.resteasy.client.jaxrs;

/**
 * This is a workaound.  For backward compatibility Apache client APIs pre-4.3
 * and 4.3 versions must be simultaneously supported.  This code checks for
 * the presents of 4.3 version code.
 *
 * This static code resides in this class so it will not interfere with the
 * ability to override the ResteasyClientBuilder method in which it is used.
 *
 * User: rsearls
 * Date: 5/4/17
 */
public class HTTPClientVersionCheck {
    private static final boolean useOldHTTPClient = Boolean.getBoolean("org.jboss.resteasy.client.useOldHTTPClient");
    private static final boolean newHTTPClientAvailable;
    static {
        boolean res = true;
        try {
            Class.forName(ClientHttpEngineBuilder43.class.getName());
        } catch (Throwable t) {
            res = false;
        }
        newHTTPClientAvailable = res;
    }

    static public boolean isUseOldHTTPClient () {
        return useOldHTTPClient;
    }

    static public boolean isNewHTTPClientAvailable() {
        return newHTTPClientAvailable;
    }
}
