package org.jboss.resteasy.spring.web;

import org.springframework.http.ResponseEntity;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ResponseEntityContainerResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // a Spring Web RestControllerAdvice can potentially handle the conversion of a ResponseEntity into a Response
        // before this has been handled so we need to be defensive about that we expect here
        Object entity = responseContext.getEntity();
        if (!(entity instanceof ResponseEntity)) {
            return;
        }
        ResponseEntity<?> responseEntity = (ResponseEntity<?>) entity;
        responseContext.setStatus(responseEntity.getStatusCodeValue());
        responseContext.setEntity(responseEntity.getBody());
        for (Map.Entry<String, List<String>> entry : responseEntity.getHeaders().entrySet()) {
            responseContext.getHeaders().addAll(entry.getKey(), entry.getValue().toArray(new Object[0]));
        }
    }
}
