package org.jboss.resteasy.links;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.links.i18n.LogMessages;
import org.jboss.resteasy.links.i18n.Messages;
import org.jboss.resteasy.links.impl.AbstractLinksProvider;
import org.jboss.resteasy.links.impl.BeanUtils;
import org.jboss.resteasy.links.impl.EL;
import org.jboss.resteasy.links.impl.ServiceDiscoveryException;
import org.jboss.resteasy.spi.ResteasyUriBuilder;

public class ObjectLinksProvider extends AbstractLinksProvider<Object> {

    public ObjectLinksProvider(final UriInfo uriInfo, final ResourceMethodRegistry resourceMethodRegistry) {
        super(uriInfo, resourceMethodRegistry);
    }

    public RESTServiceDiscovery getLinks(Object entity) {
        RESTServiceDiscovery restServiceDiscovery = new RESTServiceDiscovery();

        for (Method method : serviceMethods) {
            for (LinkResource linkResource : getLinkResources(method)) {
                processLinkResource(method, entity, restServiceDiscovery, linkResource);
            }
        }
        return restServiceDiscovery;
    }

    private void processLinkResource(Method m, Object entity, RESTServiceDiscovery ret, LinkResource service) {
        String rel = service.rel();
        // if we have uri templates, we need a compatible instance
        Class<?> type = getServiceType(service, m);
        if (type.isInstance(entity)) {
            if (checkConstraint(service, entity, m)) {
                addInstanceService(m, entity, uriInfo, ret, service, rel);
            }
        } else if (entity instanceof ResourceFacade<?> && ((ResourceFacade<?>) entity).facadeFor() == type) {
            if (checkConstraint(service, type, m)) {
                addCollectionService(m, (ResourceFacade<?>) entity, uriInfo, ret, service, rel);
            }
        }
    }

    private void addCollectionService(Method m, ResourceFacade<?> entity, UriInfo uriInfo,
            RESTServiceDiscovery ret, LinkResource service, String rel) {
        Map<String, ? extends Object> pathParameters = entity.pathParameters();
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
        ret.addLink(uri, rel);
    }

    private void addInstanceService(Method m, Object entity,
            UriInfo uriInfo, RESTServiceDiscovery ret, LinkResource service,
            String rel) {
        UriBuilder uriBuilder = uriInfo.getBaseUriBuilder().path(m.getDeclaringClass());
        if (m.isAnnotationPresent(Path.class)) {
            uriBuilder.path(m);
        }
        URI uri = buildURI(uriBuilder, service, entity, m);

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
        ret.addLink(uri, rel);
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
        if (constraint == null || constraint.length() == 0) {
            return checkEJBConstraint(m);
        }
        Boolean ret = evaluateELBoolean(m, getELContext(m, object), constraint);
        return ret != null && ret.booleanValue();
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
            return elProviderClass.newInstance();
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
