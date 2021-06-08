package org.jboss.resteasy.specimpl;

import java.io.IOException;
import java.io.InputStream;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;

public class EntityPartBuilderImpl implements EntityPart.Builder{
    private String part;
    public EntityPartBuilderImpl(final String part) {
        this.part = part;
    }
    @Override
    public EntityPart.Builder mediaType(MediaType mediaType) throws IllegalArgumentException {
        return null;
    }

    @Override
    public EntityPart.Builder mediaType(String mediaTypeString) throws IllegalArgumentException {
        return null;
    }

    @Override
    public EntityPart.Builder header(String headerName, String... headerValues) throws IllegalArgumentException {
        return null;
    }

    @Override
    public EntityPart.Builder headers(MultivaluedMap<String, String> newHeaders) throws IllegalArgumentException {
        return null;
    }

    @Override
    public EntityPart.Builder fileName(String fileName) throws IllegalArgumentException {
        return null;
    }

    @Override
    public EntityPart.Builder content(InputStream content) throws IllegalArgumentException {
        return null;
    }

    @Override
    public <T> EntityPart.Builder content(T content, Class<? extends T> type) throws IllegalArgumentException {
        return null;
    }

    @Override
    public <T> EntityPart.Builder content(T content, GenericType<T> type) throws IllegalArgumentException {
        return null;
    }

    @Override
    public EntityPart build() throws IllegalStateException, IOException, WebApplicationException {
        return null;
    }
}
