package org.jboss.resteasy.springmvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
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
import org.jboss.resteasy.util.HttpResponseCodes;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.View;

public class ResteasyView implements View {

    private String contentType = null;
    private SynchronousDispatcher dispatcher = null;
    private List<String> potentialContentTypes = null;

    public ResteasyView(String contentType, SynchronousDispatcher dispatcher) {
        this.contentType = contentType;
        this.dispatcher = dispatcher;
    }

    public ResteasyView(SynchronousDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public String getContentType() {
        return contentType;
    }

    public List<String> getPotentialContentTypes() {
        return potentialContentTypes;
    }

    public void setPotentialContentTypes(List<String> potentialContentTypes) {
        this.potentialContentTypes = potentialContentTypes;
    }

    public void render(Map model, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
            throws Exception {
        try {
            ResteasyProviderFactory.pushContext(HttpServletRequest.class, servletRequest);
            ResteasyProviderFactory.pushContext(HttpServletResponse.class, servletResponse);
            ResteasyProviderFactory.pushContext(SecurityContext.class, new ServletSecurityContext(servletRequest));

            ResteasyProviderFactory providerFactory = dispatcher.getProviderFactory();

            HttpRequest jaxrsRequest = RequestUtil.getHttpRequest(servletRequest);
            HttpResponse response = new HttpServletResponseWrapper(servletResponse, providerFactory);

            dispatcher.pushContextObjects(jaxrsRequest, response);

            Response jaxrsResponse = getResponse(model);

            if (jaxrsResponse == null) {
                return;
            }

            Object entity = jaxrsResponse.getEntity();
            Type genericType = null;
            Annotation[] annotations = null;
            if (entity instanceof GenericEntity) {
                GenericEntity ge = (GenericEntity) entity;
                genericType = ge.getType();
                entity = ge.getEntity();
            }

            if (jaxrsResponse instanceof ResponseImpl) {
                // if we haven't set it in GenericEntity processing...
                if (genericType == null)
                    genericType = ((ResponseImpl) jaxrsResponse).getGenericType();

                annotations = ((ResponseImpl) jaxrsResponse).getAnnotations();
            }

            MediaType mediaType = null;

            if (contentType != null) {
                mediaType = MediaType.valueOf(this.contentType);
            } else {
                mediaType = resolveContentType(jaxrsRequest, jaxrsResponse);
            }

            Class type = entity.getClass();

            MessageBodyWriter writer = providerFactory.getMessageBodyWriter(type, genericType, annotations, mediaType);
            if (writer == null) {
                throw new LoggableFailure("Could not find MessageBodyWriter for response object of type: "
                        + type.getName() + " of media type: " + contentType, HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
            }

            MultivaluedMap<String, Object> outputHeaders = new HttpServletResponseWrapper(servletResponse,
                    providerFactory).getOutputHeaders();
            writer.writeTo(entity, type, genericType, annotations, mediaType, outputHeaders, servletResponse
                    .getOutputStream());
        } finally {
            ResteasyProviderFactory.clearContextData();
        }
    }

    private MediaType resolveContentType(HttpRequest jaxrsRequest, Response jaxrsResponse) {
        MediaType mt = SynchronousDispatcher.resolveContentType(jaxrsResponse);
        if (MediaType.MEDIA_TYPE_WILDCARD.equals(mt.getType()) && !CollectionUtils.isEmpty(potentialContentTypes)) {
            List<MediaType> acceptableMediaTypes = jaxrsRequest.getHttpHeaders().getAcceptableMediaTypes();
            outer: for (String potentialContentTypesStr : potentialContentTypes) {
                MediaType potentialContentType = MediaType.valueOf(potentialContentTypesStr);
                for (MediaType acceptableMediaType : acceptableMediaTypes) {
                    if (acceptableMediaType.isCompatible(potentialContentType)) {
                        mt = potentialContentType;
                        break outer;
                    }
                }
            }
        }
        return mt;
    }

    protected Response getResponse(Map model) {
        for (Object value : model.values()) {
            if (value instanceof Response) {
                return (Response) value;
            }
        }
        if (model.size() == 1) {
            ResponseImpl responseImpl = new ResponseImpl();
            responseImpl.setEntity(model.values().iterator().next());
            return responseImpl;
        }
        return null;
    }

}
