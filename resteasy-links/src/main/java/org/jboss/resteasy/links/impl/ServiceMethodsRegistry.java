package org.jboss.resteasy.links.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.links.LinkResource;
import org.jboss.resteasy.links.LinkResources;
import org.jboss.resteasy.links.i18n.Messages;
import org.jboss.resteasy.spi.ResourceInvoker;
import org.jboss.resteasy.spi.util.FindAnnotation;

final class ServiceMethodsRegistry {

    private final ResourceMethodRegistry registry;

    ServiceMethodsRegistry(final ResourceMethodRegistry registry) {
        this.registry = registry;
    }

    public List<Method> getMethods() {
        List<Method> results = new LinkedList<>();
        for (Map.Entry<String, List<ResourceInvoker>> entry : registry.getBounded().entrySet()) {
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

    public List<LinkResource> getLinkResources(Method method) {
        List<LinkResource> linkResources = new LinkedList<>();

        if (method.getAnnotation(LinkResource.class) != null) {
            linkResources.add(method.getAnnotation(LinkResource.class));
        }

        if (method.getAnnotation(LinkResources.class) != null) {
            linkResources.addAll(Arrays.asList(method.getAnnotation(LinkResources.class).value()));
        }

        return linkResources;
    }

    public Class<?> getServiceType(LinkResource linkResource, Method method) {
        if (linkResource.value() != Void.class) {
            return linkResource.value();
        } else {
            try {
                return Class.forName(linkResource.entityClassName());
            } catch (ClassNotFoundException ignored) {
            }
        }
        Class<?> serviceType = getBodyType(method);
        validateServiceType(serviceType, method);
        return serviceType;
    }

    public Class<?> getServiceType(LinkResource linkResource, Method method, ClassLoader classLoader) {
        if (linkResource.value() != Void.class) {
            return linkResource.value();
        } else {
            try {
                return Class.forName(linkResource.entityClassName(), true, classLoader);
            } catch (ClassNotFoundException ignored) {
            }
        }
        Class<?> serviceType = getBodyType(method);
        validateServiceType(serviceType, method);
        return serviceType;
    }

    private Class<?> getBodyType(Method m) {
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

    private void validateServiceType(Class<?> type, Method m) {
        if (Void.TYPE == type) {
            throw new ServiceDiscoveryException(m, Messages.MESSAGES.cannotGuessResourceType());
        }
        if (Collection.class.isAssignableFrom(type)) {
            throw new ServiceDiscoveryException(m, Messages.MESSAGES.cannotGuessCollectionType());
        }
        if (Response.class.isAssignableFrom(type)) {
            throw new ServiceDiscoveryException(m, Messages.MESSAGES.cannotGuessType());
        }
    }
}
