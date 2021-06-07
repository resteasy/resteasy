package org.jboss.resteasy.plugins.providers;

import static org.jboss.resteasy.resteasy_jaxrs.i18n.Messages.MESSAGES;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.specimpl.MultivaluedTreeMap;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceInvoker;
import org.jboss.resteasy.spi.ResteasyConfiguration;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Providers;

public abstract class AbstractPatchMethodFilter implements ContainerRequestFilter {
    //TODO:these should go to jaxrs spec apis
    public static final String APPLICATION_JSON_MERGE_PATCH_JSON = "application/merge-patch+json";

    public static final MediaType APPLICATION_JSON_MERGE_PATCH_JSON_TYPE = new MediaType("application", "merge-patch+json");
    @Context
    protected Providers providers;

    protected FilterFlag readFilterDisabledFlag(ContainerRequestContext requestContext) {
        if (requestContext.getMethod().equals("PATCH") && (
                MediaType.APPLICATION_JSON_PATCH_JSON_TYPE.isCompatible(requestContext.getMediaType())
                        || APPLICATION_JSON_MERGE_PATCH_JSON_TYPE.isCompatible(requestContext.getMediaType()))) {
            ResteasyConfiguration context = ResteasyContext.getContextData(ResteasyConfiguration.class);
            boolean disabled = false;
            boolean legacyFilter = false;
            if (context == null) {
                disabled = Boolean.getBoolean(ResteasyContextParameters.RESTEASY_PATCH_FILTER_DISABLED);
                if (!disabled) {
                    legacyFilter = Boolean.getBoolean(ResteasyContextParameters.RESTEASY_PATCH_FILTER_LEGACY);
                }
            } else {
                disabled = Boolean.parseBoolean(context.getParameter(ResteasyContextParameters.RESTEASY_PATCH_FILTER_DISABLED));
                if (!disabled) {
                    legacyFilter = Boolean
                            .parseBoolean(context.getParameter(ResteasyContextParameters.RESTEASY_PATCH_FILTER_LEGACY));
                }
            }
            if (disabled) {
                return FilterFlag.SKIP;
            }
            if (legacyFilter) {
                return FilterFlag.JACKSON;
            }
            return FilterFlag.JSONP;
        }
        //if it's not PATCH method, we always skip the filter
        return FilterFlag.SKIP;
    }

    protected abstract boolean isDisabled(ContainerRequestContext context);

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void filter(final ContainerRequestContext requestContext)
            throws IOException {
        if (isDisabled(requestContext)) {
            return;
        }
        HttpRequest request = ResteasyContext.getContextData(HttpRequest.class);
        HttpResponse response = ResteasyContext.getContextData(HttpResponse.class);
        //save http headers
        List<String> patchContentTypeList = new ArrayList<String>();
        for (String header : request.getHttpHeaders().getRequestHeader(HttpHeaders.CONTENT_TYPE)) {
            patchContentTypeList.add(header);
        }
        List<String> acceptHeaders = new ArrayList<String>();
        for (String header : request.getHttpHeaders().getRequestHeader(HttpHeaders.ACCEPT)) {
            acceptHeaders.add(header);
        }
        ByteArrayOutputStream tmpOutputStream = new ByteArrayOutputStream();
        try {
            //change to application/json header to GET target object
            request.setHttpMethod("GET");
            requestContext.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, MediaType.WILDCARD);
            requestContext.getHeaders().putSingle(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
            ResourceMethodInvoker methodInvoker = this.getMethodInvoker(requestContext);
            Object targetObject = this.getTargetObject(requestContext, methodInvoker);
            MessageBodyWriter msgBodyWriter = providers.getMessageBodyWriter(targetObject.getClass(), targetObject.getClass(),
                    methodInvoker.getMethodAnnotations(), MediaType.APPLICATION_JSON_TYPE);
            if (msgBodyWriter == null) {
                throw new ProcessingException(MESSAGES.couldNotFindWriterForContentType(MediaType.APPLICATION_JSON_TYPE,
                        targetObject.getClass().getName()));
            }
            msgBodyWriter.writeTo(targetObject, targetObject.getClass(), targetObject.getClass(),
                    methodInvoker.getMethodAnnotations(), MediaType.APPLICATION_JSON_TYPE,
                    new MultivaluedTreeMap<String, Object>(), tmpOutputStream);

        } catch (ProcessingException pe) {
            Throwable c = pe.getCause();
            if (c != null && c instanceof ApplicationException) {
                c = c.getCause();
                if (c != null && c instanceof NotFoundException) {
                    throw (NotFoundException) c;
                }
            }
            throw pe;
        } finally {
            requestContext.getHeaders().put(HttpHeaders.CONTENT_TYPE, patchContentTypeList);
            requestContext.getHeaders().put(HttpHeaders.ACCEPT, acceptHeaders);
            request.setHttpMethod("PATCH");
        }
        try {
            byte[] patchResult = applyPatch(requestContext, tmpOutputStream.toByteArray());
            request.setInputStream(new ByteArrayInputStream(patchResult));
        } catch (IOException e) {
            throw new BadRequestException(e);
        }
    }

    protected abstract byte[] applyPatch(ContainerRequestContext requestContext, byte[] targetJsonBytes) throws IOException,
            Failure;

    protected Object getTargetObject(ContainerRequestContext requestContext, ResourceMethodInvoker methodInvoker) {
        HttpRequest request = ResteasyContext.getContextData(HttpRequest.class);
        HttpResponse response = ResteasyContext.getContextData(HttpResponse.class);
        Object targetObject = null;
        try {
            targetObject = methodInvoker.invokeDryRun(request, response).toCompletableFuture().getNow(null);
        } catch (Exception e) {
            if (e.getCause() instanceof WebApplicationException) {
                throw e;
            } else {
                LogMessages.LOGGER.errorPatchTarget(requestContext.getUriInfo().getRequestUri().toString());
                throw new ProcessingException("Unexpected error to get the json patch/merge target", e);
            }
        }
        return targetObject;
    }

    protected ResourceMethodInvoker getMethodInvoker(ContainerRequestContext requestContext) {
        HttpRequest request = ResteasyContext.getContextData(HttpRequest.class);
        Registry methodRegistry = ResteasyContext.getContextData(Registry.class);
        ResourceInvoker resourceInovker = null;
        try {
            resourceInovker = methodRegistry.getResourceInvoker(request);
        } catch (Exception e) {
            LogMessages.LOGGER.patchTargetMethodNotFound(requestContext.getUriInfo().getRequestUri().toString());
            throw new ProcessingException("GET method returns the patch/merge json object target not found");
        }
        return (ResourceMethodInvoker) resourceInovker;
    }

    public enum FilterFlag {
        SKIP, JACKSON, JSONP;
    }

}
