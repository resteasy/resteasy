package org.jboss.resteasy.links.impl;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.links.LinkResource;
import org.jboss.resteasy.links.LinksProvider;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.spi.ResteasyUriBuilder;

public final class ClassLinksProvider implements LinksProvider<Class<?>> {

    private final UriInfo uriInfo;

    private final ServiceMethodsRegistry registry;

    private final EJBConstraintChecker constraintChecker;

    public ClassLinksProvider(final UriInfo uriInfo, final ResourceMethodRegistry resourceMethodRegistry) {
        this.uriInfo = uriInfo;
        this.registry = new ServiceMethodsRegistry(resourceMethodRegistry);
        this.constraintChecker = new EJBConstraintChecker();
    }

    public RESTServiceDiscovery getLinks(Class<?> entityClass) {
        RESTServiceDiscovery links = new RESTServiceDiscovery();
        for (Method method : registry.getMethods()) {
            for (LinkResource linkResource : registry.getLinkResources(method)) {
                Class<?> type = registry.getServiceType(linkResource, method);
                if (entityClass.isAssignableFrom(type) && constraintChecker.check(method)) {
                    processLinkResource(method, links);
                }
            }
        }
        return links;
    }

    public RESTServiceDiscovery getLinks(Class<?> entityClass, ClassLoader classLoader) {
        RESTServiceDiscovery links = new RESTServiceDiscovery();
        for (Method method : registry.getMethods()) {
            for (LinkResource linkResource : registry.getLinkResources(method)) {
                Class<?> type = registry.getServiceType(linkResource, method, classLoader);
                if (entityClass.isAssignableFrom(type) && constraintChecker.check(method, classLoader)) {
                    processLinkResource(method, links);
                }
            }
        }
        return links;
    }

    private void processLinkResource(Method method, RESTServiceDiscovery links) {
        UriBuilder uriBuilder = uriInfo.getBaseUriBuilder().path(method.getDeclaringClass());
        if (method.isAnnotationPresent(Path.class)) {
            uriBuilder.path(method);
        }
        URI uri;
        List<String> paramNames = ((ResteasyUriBuilder) uriBuilder).getPathParamNamesInDeclarationOrder();
        if (paramNames.isEmpty()) {
            uri = uriBuilder.build();
        } else {
            // just bail out since we don't have enough parameters, that must be an instance service
            return;
        }

        if (method.isAnnotationPresent(GET.class)) {
            links.addLink(uri, "list");
        } else if (method.isAnnotationPresent(POST.class)) {
            links.addLink(uri, "add");
        }
    }
}
