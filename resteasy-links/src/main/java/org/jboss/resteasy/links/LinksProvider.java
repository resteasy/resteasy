package org.jboss.resteasy.links;

import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.links.impl.ClassLinksProvider;
import org.jboss.resteasy.links.impl.ObjectLinksProvider;
import org.jboss.resteasy.spi.Registry;

public interface LinksProvider<T> {

    static LinksProvider<Class<?>> getClassLinksProvider() {
        return new ClassLinksProvider(ResteasyContext.getContextData(UriInfo.class),
                (ResourceMethodRegistry) ResteasyContext.getContextData(Registry.class));
    }

    static LinksProvider<Object> getObjectLinksProvider() {
        return new ObjectLinksProvider(ResteasyContext.getContextData(UriInfo.class),
                (ResourceMethodRegistry) ResteasyContext.getContextData(Registry.class));
    }

    RESTServiceDiscovery getLinks(T object);

    RESTServiceDiscovery getLinks(T object, ClassLoader classLoader);
}
