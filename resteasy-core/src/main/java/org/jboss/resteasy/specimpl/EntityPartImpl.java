package org.jboss.resteasy.specimpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
//TODO: 3.1 implementation
public class EntityPartImpl implements EntityPart {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public Optional<String> getFileName() {
        return Optional.empty();
    }

    @Override
    public InputStream getContent() {
        return null;
    }

    @Override
    public <T> T getContent(Class<T> type)
            throws IllegalArgumentException, IllegalStateException, IOException, WebApplicationException {
        return null;
    }

    @Override
    public <T> T getContent(GenericType<T> type)
            throws IllegalArgumentException, IllegalStateException, IOException, WebApplicationException {
        return null;
    }

    @Override
    public MultivaluedMap<String, String> getHeaders() {
        return null;
    }

    @Override
    public MediaType getMediaType() {
        return null;
    }
}
