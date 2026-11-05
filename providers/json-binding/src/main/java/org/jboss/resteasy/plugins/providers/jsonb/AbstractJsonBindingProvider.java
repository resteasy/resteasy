package org.jboss.resteasy.plugins.providers.jsonb;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import jakarta.json.bind.Jsonb;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Providers;

/**
 * Created by rsearls
 */
public class AbstractJsonBindingProvider {

    private static final String JSON = "json";
    private static final String PLUS_JSON = "+json";

    @Context
    private Providers providers;

    protected Jsonb getJsonb(Class<?> type) {
        ContextResolver<Jsonb> contextResolver = providers.getContextResolver(Jsonb.class, MediaType.APPLICATION_JSON_TYPE);
        Jsonb delegate = null;
        if (contextResolver != null) {
            delegate = contextResolver.getContext(type);
        }
        return new ManagedJsonb(delegate);
    }

    public static Charset getCharset(final MediaType mediaType) {
        return StandardCharsets.UTF_8;
    }

    public static boolean isSupportedMediaType(final MediaType mediaType) {
        return mediaType.getSubtype().equals(JSON) || mediaType.getSubtype().endsWith(PLUS_JSON);
    }
}