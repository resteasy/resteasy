package org.jboss.resteasy.client.jaxrs.spi;

import javax.net.ssl.SSLContext;
import java.net.URI;

/**
 * Interface to enable loading of authentication configuration from outside.
 * @author dvilkola@redhat.com
 */
public interface ClientConfigProvider {

    String getUsername(URI uri) throws ClientConfigException;
    String getPassword(URI uri) throws ClientConfigException;
    String getBearerToken(URI uri) throws ClientConfigException;
    SSLContext getSSLContext(URI uri) throws ClientConfigException;
}
