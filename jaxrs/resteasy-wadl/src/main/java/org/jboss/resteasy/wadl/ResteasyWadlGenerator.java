package org.jboss.resteasy.wadl;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class ResteasyWadlGenerator {

    public static ResteasyWadlServiceRegistry generateServiceRegistry(ResteasyDeployment deployment) {
        ResourceMethodRegistry registry = (ResourceMethodRegistry) deployment.getRegistry();
        ResteasyProviderFactory providerFactory = deployment.getProviderFactory();
        ResteasyWadlServiceRegistry service = new ResteasyWadlServiceRegistry(null, registry, providerFactory, null);
        return service;
    }
}
