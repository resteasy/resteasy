package org.jboss.resteasy.springmvc;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.MessageBodyWriter;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServletResponseWrapper;
import org.jboss.resteasy.plugins.server.servlet.ServletSecurityContext;
import org.jboss.resteasy.specimpl.ResponseImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Solomon
 * 
 */
// TODO: Right now there's a problematic relationship between Dispatcher and
// Registry. Ideally, the Registry shouldn't be owned by the Dispatcher, and the
// methods needed from SynchronousDispatcher should move into a shared class.
public class ResteasyHandlerAdapter extends SynchronousDispatcher implements
        HandlerAdapter {

    public ResteasyHandlerAdapter(ResteasyProviderFactory providerFactory) {
        super(providerFactory);
    }

    public long getLastModified(HttpServletRequest request, Object handler) {
        // TODO Auto-generated method stub
        return 0;
    }

    public ModelAndView handle(HttpServletRequest servletRequest,
            HttpServletResponse servletResponse, Object handler)
            throws Exception {

        ResteasyRequestWrapper responseWrapper = (ResteasyRequestWrapper) handler;

        // TODO: copied from HttpServletDisptacher;
        HttpResponse response = new HttpServletResponseWrapper(servletResponse,
                getProviderFactory());

        try {
            ResteasyProviderFactory.pushContext(HttpServletRequest.class,
                    servletRequest);
            ResteasyProviderFactory.pushContext(HttpServletResponse.class,
                    servletResponse);
            ResteasyProviderFactory.pushContext(SecurityContext.class,
                    new ServletSecurityContext(servletRequest));

            // TODO: copied from SynchronousDispatcher!
            HttpRequest request = responseWrapper.getHttpRequest();
            pushContextObjects(request, response);

            Response jaxrsResponse = null;
            try {
                jaxrsResponse = responseWrapper.getInvoker().invoke(request,
                        response);
            } catch (Exception e) {
                handleInvokerException(request, response, e);
            }

            try {
                if (jaxrsResponse != null)
                    return createModelAndView(response, jaxrsResponse);
            } catch (Exception e) {
                handleWriteResponseException(request, response, e);
            }

        } finally {
            ResteasyProviderFactory.clearContextData();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public ModelAndView createModelAndView(HttpResponse response, Response jaxrsResponse) throws IOException,
            WebApplicationException {
        writeCookies(response, jaxrsResponse);

        if (jaxrsResponse.getEntity() == null) {
            response.setStatus(jaxrsResponse.getStatus());
            outputHeaders(response, jaxrsResponse);
            return null;
        }

        Object entity = jaxrsResponse.getEntity();
        Type genericType = null;
        Annotation[] annotations = null;
        if (entity instanceof GenericEntity) {
            GenericEntity ge = (GenericEntity) entity;
            genericType = ge.getType();
            entity = ge.getEntity();
        }

        if (entity instanceof ModelAndView) {
            return (ModelAndView) entity;
        }

        if (jaxrsResponse instanceof ResponseImpl) {
            // if we haven't set it in GenericEntity processing...
            if (genericType == null)
                genericType = ((ResponseImpl) jaxrsResponse).getGenericType();

            annotations = ((ResponseImpl) jaxrsResponse).getAnnotations();
        }

        Class type = entity.getClass();

        MediaType resolvedContentType = resolveContentType(jaxrsResponse);

        MessageBodyWriter writer = providerFactory.getMessageBodyWriter(type, genericType, annotations,
                resolvedContentType);
        if (writer == null) {
            throw new LoggableFailure("Could not find MessageBodyWriter for response object of type: " + type.getName()
                    + " of media type: " + resolvedContentType, HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
        }
        long size = writer.getSize(entity, type, genericType, annotations, resolvedContentType);
        response.getOutputHeaders().putSingle(HttpHeaderNames.CONTENT_LENGTH, Integer.toString((int) size));
        response.setStatus(jaxrsResponse.getStatus());
        outputHeaders(response, jaxrsResponse);

        ResteasyView resteasyView = new ResteasyView(resolvedContentType.toString(), this);
        return new ModelAndView(resteasyView).addObject(jaxrsResponse);
    }

    public boolean supports(Object handler) {
        return handler instanceof ResteasyRequestWrapper;
    }

}
