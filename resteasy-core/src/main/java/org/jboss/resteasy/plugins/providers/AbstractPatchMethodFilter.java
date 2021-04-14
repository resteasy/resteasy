package org.jboss.resteasy.plugins.providers;

import static org.jboss.resteasy.resteasy_jaxrs.i18n.Messages.MESSAGES;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.specimpl.MultivaluedTreeMap;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceInvoker;
import org.jboss.resteasy.spi.ResteasyConfiguration;

public abstract class AbstractPatchMethodFilter implements ContainerRequestFilter
{
    //TODO:thse should go to jaxrs spec apis
    public static final String APPLICATION_JSON_MERGE_PATCH_JSON = "application/merge-patch+json";

    public static final MediaType APPLICATION_JSON_MERGE_PATCH_JSON_TYPE = new MediaType("application",
          "merge-patch+json");
    @Context
    protected Providers providers;
    @Override
    @SuppressWarnings(
          {"rawtypes", "unchecked"})
    public void filter(final ContainerRequestContext requestContext) throws IOException
    {
        if(this.isDisabled(requestContext))
        {
            return;
        }
        try
        {
            ResourceMethodInvoker methodInvoker = this.getMethodInvoker(requestContext);
            Object targetObject = this.getTargetObject(requestContext, methodInvoker);
            ByteArrayOutputStream tmpOutputStream = new ByteArrayOutputStream();
            MessageBodyWriter msgBodyWriter = providers.getMessageBodyWriter(
                  targetObject.getClass(), targetObject.getClass(), methodInvoker.getMethodAnnotations(),
                  MediaType.APPLICATION_JSON_TYPE);
            if (msgBodyWriter == null) {
                throw new ProcessingException(MESSAGES.couldNotFindWriterForContentType(MediaType.APPLICATION_JSON_TYPE, targetObject.getClass().getName()));
            }
            msgBodyWriter.writeTo(targetObject, targetObject.getClass(), targetObject.getClass(),
                  methodInvoker.getMethodAnnotations(), MediaType.APPLICATION_JSON_TYPE,
                  new MultivaluedTreeMap<String, Object>(), tmpOutputStream);

            byte[] patchResult = applyPatch(requestContext, tmpOutputStream.toByteArray());
            HttpRequest request = ResteasyContext.getContextData(HttpRequest.class);
            request.setInputStream(new ByteArrayInputStream(patchResult));
            request.setHttpMethod("PATCH");
        }
        catch (ProcessingException pe)
        {
            Throwable c = pe.getCause();
            if (c != null && c instanceof ApplicationException)
            {
                c = c.getCause();
                if (c != null && c instanceof NotFoundException)
                {
                    throw (NotFoundException) c;
                }
            }
            throw pe;
        } catch (Exception e)
        {
            throw new BadRequestException(e);
        }

        /*catch (JsonMappingException | JsonParseException e)
        {
            throw new BadRequestException(e);
        }
        catch (JsonPatchException e)
        {
            throw new Failure(e, HttpResponseCodes.SC_CONFLICT);
        }*/
    }

    protected abstract byte[] applyPatch(ContainerRequestContext requestContext, byte[] targetJsonBytes) throws Exception;

    protected boolean isDisabled(ContainerRequestContext requestContext)
    {
        if (requestContext.getMethod().equals("PATCH")
              && (MediaType.APPLICATION_JSON_PATCH_JSON_TYPE.isCompatible(requestContext.getMediaType()) ||
              APPLICATION_JSON_MERGE_PATCH_JSON_TYPE
                    .isCompatible(requestContext.getMediaType())))
        {
            ResteasyConfiguration context = ResteasyContext.getContextData(ResteasyConfiguration.class);
            boolean disabled = false;
            if (context == null)
            {
                disabled = Boolean.getBoolean(ResteasyContextParameters.RESTEASY_PATCH_FILTER_DISABLED);
            }
            else
            {
                disabled = Boolean.parseBoolean(context
                      .getParameter(ResteasyContextParameters.RESTEASY_PATCH_FILTER_DISABLED));
            }
            return disabled;
        }
        //if it's not PATCH , we always skip the filter
        return true;
    }
    protected Object getTargetObject(ContainerRequestContext requestContext, ResourceMethodInvoker methodInvoker) {
        HttpRequest request = ResteasyContext.getContextData(HttpRequest.class);
        request.setHttpMethod("GET");
        //save http headers
        List<String> patchContentTypeList = new ArrayList<String>();
        for (String header : request.getHttpHeaders().getRequestHeader(HttpHeaders.CONTENT_TYPE))
        {
            patchContentTypeList.add(header);
        }
        List<String> acceptHeaders = new ArrayList<String>();
        for (String header : request.getHttpHeaders().getRequestHeader(HttpHeaders.ACCEPT))
        {
            acceptHeaders.add(header);
        }
        //change to application/json header to GET target object
        requestContext.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, MediaType.WILDCARD);
        requestContext.getHeaders().putSingle(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
        HttpResponse response = ResteasyContext.getContextData(HttpResponse.class);
        Object targetObject = null;
        try
        {
            targetObject = methodInvoker.invokeDryRun(request, response).toCompletableFuture().getNow(null);
        }
        catch (Exception e)
        {
            if (e.getCause() instanceof WebApplicationException)
            {
                throw e;
            }
            else

            {
                LogMessages.LOGGER.errorPatchTarget(requestContext.getUriInfo().getRequestUri().toString());
                throw new ProcessingException("Unexpected error to get the json patch/merge target", e);
            }
        }finally{
            requestContext.getHeaders().put(HttpHeaders.CONTENT_TYPE, patchContentTypeList);
            requestContext.getHeaders().put(HttpHeaders.ACCEPT, acceptHeaders);
        }
        return targetObject;
    }

    protected ResourceMethodInvoker getMethodInvoker(ContainerRequestContext requestContext) {
        HttpRequest request = ResteasyContext.getContextData(HttpRequest.class);
        Registry methodRegistry = ResteasyContext.getContextData(Registry.class);
        ResourceInvoker resourceInovker = null;
        try
        {
            resourceInovker = methodRegistry.getResourceInvoker(request);
        }
        catch (Exception e)
        {
            LogMessages.LOGGER.patchTargetMethodNotFound(requestContext.getUriInfo().getRequestUri().toString());
            throw new ProcessingException("GET method returns the patch/merge json object target not found");
        }
        return (ResourceMethodInvoker) resourceInovker;
    }
}
