package org.jboss.resteasy.core.interception.jaxrs;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.client.ClientResponseFilter;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientResponseFilterRegistryImpl extends JaxrsInterceptorRegistryImpl<ClientResponseFilter> {
    public ClientResponseFilterRegistryImpl(final ResteasyProviderFactory providerFactory) {
        super(providerFactory, ClientResponseFilter.class);
    }

    @Override
    protected void sort(List<Match> matches) {
        Collections.sort(matches, new DescendingPrecedenceComparator());

    }

    @Override
    public ClientResponseFilterRegistryImpl clone(ResteasyProviderFactory factory) {
        ClientResponseFilterRegistryImpl clone = new ClientResponseFilterRegistryImpl(factory);
        clone.interceptors.addAll(interceptors);
        return clone;
    }
}
