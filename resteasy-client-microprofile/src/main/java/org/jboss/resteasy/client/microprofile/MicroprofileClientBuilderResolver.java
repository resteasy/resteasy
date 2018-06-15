package org.jboss.resteasy.client.microprofile;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.spi.RestClientBuilderResolver;

/**
 * Created by hbraun on 15.01.18.
 */
public class MicroprofileClientBuilderResolver extends RestClientBuilderResolver {
    @Override
    public RestClientBuilder newBuilder() {
        return new MicroprofileClientBuilder();
    }
}

