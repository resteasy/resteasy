package org.jboss.resteasy.links.impl;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.links.ELProvider;
import org.jboss.resteasy.links.LinkELProvider;
import org.jboss.resteasy.links.LinkResource;
import org.jboss.resteasy.links.LinksProvider;
import org.jboss.resteasy.links.ParamBinding;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.ResourceFacade;
import org.jboss.resteasy.links.i18n.LogMessages;
import org.jboss.resteasy.links.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyUriBuilder;

public final class ObjectLinksProvider implements LinksProvider<Object> {

    private final UriInfo uriInfo;

    private final ServiceMethodsRegistry registry;

    private final EJBConstraintChecker ejbConstraintChecker;

    public ObjectLinksProvider(final UriInfo uriInfo, final ResourceMethodRegistry resourceMethodRegistry) {
        this.uriInfo = uriInfo;
        this.registry = new ServiceMethodsRegistry(resourceMethodRegistry);
        this.ejbConstraintChecker = new EJBConstraintChecker();
    }

    public RESTServiceDiscovery getLinks(Object entity) {
        RESTServiceDiscovery links = new RESTServiceDiscovery();
        for (Method method : registry.getMethods()) {
            for (LinkResource linkResource : registry.getLinkResources(method)) {
                Class<?> type = registry.getServiceType(linkResource, method);
                if (type.isInstance(entity) && checkConstraint(linkResource, entity, method)) {
                    addInstanceService(method, entity, linkResource, links);
                } else if (isResourceFacade(entity, type) && checkConstraint(linkResource, type, method)) {
                    addCollectionService(method, (ResourceFacade<?>) entity, linkResource.rel(), links);
                }
            }
        }
        return links;
    }

    public RESTServiceDiscovery getLinks(Object entity, ClassLoader classLoader) {
        RESTServiceDiscovery links = new RESTServiceDiscovery();
        for (Method method : registry.getMethods()) {
            for (LinkResource linkResource : registry.getLinkResources(method)) {
                Class<?> type = registry.getServiceType(linkResource, method, classLoader);
                if (type.isInstance(entity) && checkConstraint(linkResource, entity, method, classLoader)) {
                    addInstanceService(method, entity, linkResource, links);
                } else if (isResourceFacade(entity, type) && checkConstraint(linkResource, type, method, classLoader)) {
                    addCollectionService(method, (ResourceFacade<?>) entity, linkResource.rel(), links);
                }
            }
        }
        return links;
    }

    private boolean isResourceFacade(Object entity, Class<?> type) {
        return entity instanceof ResourceFacade<?> && ((ResourceFacade<?>) entity).facadeFor() == type;
    }

    private void addCollectionService(Method m, ResourceFacade<?> entity, String rel, RESTServiceDiscovery links) {
        Map<String, ?> pathParameters = entity.pathParameters();
        // do we need any path parameters?
        UriBuilder uriBuilder = uriInfo.getBaseUriBuilder().path(m.getDeclaringClass());
        if (m.isAnnotationPresent(Path.class)) {
            uriBuilder.path(m);
        }
        URI uri;
        List<String> paramNames = ((ResteasyUriBuilder) uriBuilder).getPathParamNamesInDeclarationOrder();
        if (paramNames.isEmpty()) {
            uri = uriBuilder.build();
        } else if (pathParameters.size() >= paramNames.size()) {
            uri = uriBuilder.buildFromMap(pathParameters);
        } else
        // just bail out since we don't have enough parameters, that must be an instance service
        {
            return;
        }
        if (rel.length() == 0) {
            if (m.isAnnotationPresent(GET.class)) {
                rel = "list";
            } else if (m.isAnnotationPresent(POST.class)) {
                rel = "add";
            }
        }
        links.addLink(uri, rel);
    }

    private void addInstanceService(Method m, Object entity, LinkResource linkResource, RESTServiceDiscovery links) {
        UriBuilder uriBuilder = uriInfo.getBaseUriBuilder().path(m.getDeclaringClass());
        if (m.isAnnotationPresent(Path.class)) {
            uriBuilder.path(m);
        }
        URI uri = buildURI(uriBuilder, linkResource, entity, m);
        String rel = linkResource.rel();
        if (rel.length() == 0) {
            if (m.isAnnotationPresent(GET.class)) {
                Class<?> type = m.getReturnType();
                if (Collection.class.isAssignableFrom(type)) {
                    rel = "list";
                } else {
                    rel = "self";
                }
            } else if (m.isAnnotationPresent(PUT.class)) {
                rel = "update";
            } else if (m.isAnnotationPresent(POST.class)) {
                rel = "add";
            } else if (m.isAnnotationPresent(DELETE.class)) {
                rel = "remove";
            }
        }
        links.addLink(uri, rel);
    }

    private URI buildURI(UriBuilder uriBuilder, LinkResource service,
            Object entity, Method m) {
        for (ParamBinding binding : service.queryParameters()) {
            uriBuilder.queryParam(binding.name(), evaluateEL(m, getELContext(m, entity), binding.value()));
        }
        for (ParamBinding binding : service.matrixParameters()) {
            uriBuilder.matrixParam(binding.name(), evaluateEL(m, getELContext(m, entity), binding.value()));
        }

        String[] uriTemplates = service.pathParameters();
        if (uriTemplates.length > 0) {
            Object[] values = new Object[uriTemplates.length];
            for (int i = 0; i < values.length; i++) {
                values[i] = evaluateEL(m, getELContext(m, entity), uriTemplates[i]);
            }
            return uriBuilder.build(values);
        }
        // do we need any path parameters?
        List<String> paramNames = ((ResteasyUriBuilder) uriBuilder).getPathParamNamesInDeclarationOrder();
        if (paramNames.isEmpty()) {
            return uriBuilder.build();
        }
        // try to find the IDs
        List<Object> params = findURIParamsFromResource(entity);
        if (params.size() == paramNames.size()) {
            return uriBuilder.build(params.toArray());
        }
        // if we have too many, ignore the last ones
        if (params.size() > paramNames.size()) {
            return uriBuilder.build(params.subList(0, paramNames.size()).toArray());
        }
        throw new ServiceDiscoveryException(m, Messages.MESSAGES.notEnoughtUriParameters(paramNames.size(), params.size()));
    }

    private List<Object> findURIParamsFromResource(Object entity) {
        List<Object> ids = new ArrayList<Object>();
        do {
            List<Object> theseIDs = BeanUtils.findIDs(entity);
            ids.addAll(0, theseIDs);
        } while ((entity = BeanUtils.findParentResource(entity)) != null);
        return ids;
    }

    private boolean checkConstraint(LinkResource service, Object object, Method m) {
        String constraint = service.constraint();
        if (constraint.length() == 0) {
            return ejbConstraintChecker.check(m);
        }
        Boolean ret = evaluateELBoolean(m, getELContext(m, object), constraint);
        return ret != null && ret;
    }

    private boolean checkConstraint(LinkResource service, Object object, Method m, ClassLoader classLoader) {
        if (service.constraint().length() == 0) {
            return ejbConstraintChecker.check(m, classLoader);
        }
        Boolean ret = evaluateELBoolean(m, getELContext(m, object), service.constraint());
        return ret != null && ret;
    }

    private ELContext getELContext(Method m, Object base) {
        ELContext ours = EL.createELContext(base);
        ELProvider elProvider = getELProvider(m);
        if (elProvider != null) {
            return elProvider.getContext(ours);
        }
        return ours;
    }

    private ELProvider getELProvider(Method m) {
        LinkELProvider linkElProvider = findLinkELProvider(m);
        if (linkElProvider == null) {
            return null;
        }
        Class<? extends ELProvider> elProviderClass = linkElProvider.value();
        try {
            return elProviderClass.getDeclaredConstructor().newInstance();
        } catch (Exception x) {
            LogMessages.LOGGER.error(Messages.MESSAGES.couldNotInstantiateELProviderClass(elProviderClass.getName()), x);
            throw new ServiceDiscoveryException(m, Messages.MESSAGES.failedToInstantiateELProvider(elProviderClass.getName()),
                    x);
        }
    }

    private LinkELProvider findLinkELProvider(Method m) {
        if (m.isAnnotationPresent(LinkELProvider.class)) {
            return m.getAnnotation(LinkELProvider.class);
        }
        Class<?> c = m.getDeclaringClass();
        if (c.isAnnotationPresent(LinkELProvider.class)) {
            return c.getAnnotation(LinkELProvider.class);
        }
        Package p = c.getPackage();
        if (p != null && p.isAnnotationPresent(LinkELProvider.class)) {
            return p.getAnnotation(LinkELProvider.class);
        }
        return null;
    }

    public Object evaluateEL(Method m, ELContext context, String expression) {
        try {
            return EL.EXPRESSION_FACTORY.createValueExpression(context, expression,
                    Object.class).getValue(context);
        } catch (Exception x) {
            throw new ServiceDiscoveryException(m, Messages.MESSAGES.failedToEvaluateELExpression(expression), x);
        }
    }

    public Boolean evaluateELBoolean(Method m, ELContext context, String expression) {
        try {
            return (Boolean) EL.EXPRESSION_FACTORY.createValueExpression(context, expression,
                    Boolean.class).getValue(context);
        } catch (Exception x) {
            throw new ServiceDiscoveryException(m, Messages.MESSAGES.failedToEvaluateELExpression(expression), x);

        }
    }
}
