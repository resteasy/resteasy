package org.jboss.resteasy.links.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.links.LinkResource;
import org.jboss.resteasy.links.LinkResources;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.i18n.Messages;
import org.jboss.resteasy.spi.ResourceInvoker;
import org.jboss.resteasy.spi.util.FindAnnotation;

public abstract class AbstractLinksProvider<T> {

    protected final UriInfo uriInfo;

    protected final List<Method> serviceMethods;

    public AbstractLinksProvider(final UriInfo uriInfo, final ResourceMethodRegistry resourceMethodRegistry) {
        this.uriInfo = uriInfo;
        this.serviceMethods = getServiceMethods(resourceMethodRegistry);
    }

    public abstract RESTServiceDiscovery getLinks(T object);

    protected List<LinkResource> getLinkResources(Method method) {
        List<LinkResource> linkResources = new LinkedList<>();

        if (method.getAnnotation(LinkResource.class) != null) {
            linkResources.add(method.getAnnotation(LinkResource.class));
        }

        if (method.getAnnotation(LinkResources.class) != null) {
            linkResources.addAll(Arrays.asList(method.getAnnotation(LinkResources.class).value()));
        }

        return linkResources;
    }

    protected boolean checkEJBConstraint(Method m) {
        // Use dynamic class loading here since if the EJB annotation class is not present
        // it cannot be on the method, so we don't have to check for it
        try {
            Class.forName("javax.annotation.security.RolesAllowed");
        } catch (ClassNotFoundException e) {
            // class not here, therefore not on method either
            return true;
        }
        // From now on we can use this class since it's there. I (Stef Epardaud) don't think we need to
        // remove the reference here and use reflection.
        RolesAllowed rolesAllowed = m.getAnnotation(RolesAllowed.class);
        if (rolesAllowed == null) {
            return true;
        }
        SecurityContext context = ResteasyContext.getContextData(SecurityContext.class);
        for (String role : rolesAllowed.value()) {
            if (context.isUserInRole(role)) {
                return true;
            }
        }
        return false;
    }

    protected Class<?> getServiceType(LinkResource service, Method m) {
        if (service.value() != Void.class) {
            return service.value();
        } else if (!service.entityClassName().isEmpty()) {
            try {
                return Class.forName(service.entityClassName());
            } catch (ClassNotFoundException ignored) {
            }
        }

        Class<?> type = findBodyType(m);

        if (Void.TYPE == type) {
            throw new ServiceDiscoveryException(m, Messages.MESSAGES.cannotGuessResourceType());
        }
        if (Collection.class.isAssignableFrom(type)) {
            throw new ServiceDiscoveryException(m, Messages.MESSAGES.cannotGuessCollectionType());
        }
        if (Response.class.isAssignableFrom(type)) {
            throw new ServiceDiscoveryException(m, Messages.MESSAGES.cannotGuessType());
        }
        return type;
    }

    private Class<?> findBodyType(Method m) {
        Annotation[][] annotations = m.getParameterAnnotations();
        Class<?>[] types = m.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            // if there's no JAXRS annotation nor @Form, it's a body right?
            if (FindAnnotation.findJaxRSAnnotations(annotations[i]).length == 0
                    && FindAnnotation.findAnnotation(annotations[i], Form.class) == null) {
                return types[i];
            }
        }

        return m.getReturnType();
    }

    private List<Method> getServiceMethods(ResourceMethodRegistry registry) {
        ArrayList<Method> results = new ArrayList<>();
        for (Entry<String, List<ResourceInvoker>> entry : registry.getBounded().entrySet()) {
            List<ResourceInvoker> invokers = entry.getValue();
            for (ResourceInvoker invoker : invokers) {
                if (invoker instanceof ResourceMethodInvoker) {
                    ResourceMethodInvoker resourceMethod = (ResourceMethodInvoker) invoker;
                    Method method = resourceMethod.getMethod();
                    results.add(method);
                } else {
                    // TODO: fix this?
                }
            }
        }
        return results;
    }
}
