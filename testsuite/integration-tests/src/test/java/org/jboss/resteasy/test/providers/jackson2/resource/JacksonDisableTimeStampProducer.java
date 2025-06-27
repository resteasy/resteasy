package org.jboss.resteasy.test.providers.jackson2.resource;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonDisableTimeStampProducer implements ContextResolver<ObjectMapper> {

    private final ObjectMapper json;

    public JacksonDisableTimeStampProducer() {
        this.json = JsonMapper.builder()
                .findAndAddModules()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .build();
    }

    @Override
    public ObjectMapper getContext(final Class<?> type) {
        return json;
    }
}