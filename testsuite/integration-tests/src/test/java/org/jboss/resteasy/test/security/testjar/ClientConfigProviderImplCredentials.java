package org.jboss.resteasy.test.security.testjar;

import org.jboss.resteasy.client.jaxrs.spi.ClientConfigException;
import org.jboss.resteasy.client.jaxrs.spi.ClientConfigProvider;

import javax.net.ssl.SSLContext;
import java.net.URI;

/**
 * ClientConfigProvider implementation used in jar that tests ClientConfigProvider functionality regarding HTTP BASIC auth.
 */
public class ClientConfigProviderImplCredentials implements ClientConfigProvider {

    @Override
    public String getUsername(URI uri) throws ClientConfigException {
        return "bill";
    }

    @Override
    public String getPassword(URI uri) throws ClientConfigException {
        return "password1";
    }

    @Override
    public String getBearerToken(URI uri) throws ClientConfigException {
        return null;
    }

    @Override
    public SSLContext getSSLContext(URI uri) throws ClientConfigException {
        return null;
    }
}
