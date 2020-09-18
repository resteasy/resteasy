package org.jboss.resteasy.links;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.links.impl.AbstractLinksProvider;
import org.jboss.resteasy.spi.ResteasyUriBuilder;

public class ClassLinksProvider extends AbstractLinksProvider<Class<?>> {

    public ClassLinksProvider(final UriInfo uriInfo, final ResourceMethodRegistry resourceMethodRegistry) {
        super(uriInfo, resourceMethodRegistry);
    }

    public RESTServiceDiscovery getLinks(Class<?> entityClass) {
        RESTServiceDiscovery restServiceDiscovery = new RESTServiceDiscovery();
        for (Method method : serviceMethods) {
            for (LinkResource linkResource : getLinkResources(method)) {
                processLinkResource(entityClass, method, restServiceDiscovery, linkResource);
            }
        }
        return restServiceDiscovery;
    }

    private void processLinkResource(Class<?> entityClass, Method method, RESTServiceDiscovery links,
            LinkResource linkResource) {
        if (!entityClass.isAssignableFrom(getServiceType(linkResource, method)) || !checkEJBConstraint(method)) {
            return;
        }

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
