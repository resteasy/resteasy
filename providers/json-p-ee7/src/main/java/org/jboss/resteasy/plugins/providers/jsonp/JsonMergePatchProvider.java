/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.plugins.providers.jsonp;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.json.Json;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;

/**
 * Provider for reading JsonMergePatch from request body.
 * Specifically for JSON Merge Patch (RFC 7386) with media type application/merge-patch+json.
 */
@Provider
@Consumes("application/merge-patch+json")
public class JsonMergePatchProvider extends AbstractJsonpProvider
        implements MessageBodyReader<JsonMergePatch> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return JsonMergePatch.class.isAssignableFrom(type);
    }

    @Override
    public JsonMergePatch readFrom(Class<JsonMergePatch> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
        JsonReader reader = findReader(mediaType, entityStream);
        try {
            JsonValue jsonValue = reader.readValue();
            return Json.createMergePatch(jsonValue);
        } finally {
            reader.close();
        }
    }
}
