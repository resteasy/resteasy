package org.jboss.resteasy.test.security.testjar;

import java.net.URI;

import javax.net.ssl.SSLContext;

import org.jboss.resteasy.client.jaxrs.spi.ClientConfigException;
import org.jboss.resteasy.client.jaxrs.spi.ClientConfigProvider;

/**
 * ClientConfigProvider implementation used in jar that tests ClientConfigProvider functionality regarding Bearer token.
 */
public class ClientConfigProviderImplWithBearerMocked implements ClientConfigProvider {

    @Override
    public String getUsername(URI uri) throws ClientConfigException {
        return "name";
    }

    @Override
    public String getPassword(URI uri) throws ClientConfigException {
        return "pass";
    }

    @Override
    public String getBearerToken(URI uri) throws ClientConfigException {
        return "myTestToken";
    }

    @Override
    public SSLContext getSSLContext(URI uri) throws ClientConfigException {
        return null;
    }
}
