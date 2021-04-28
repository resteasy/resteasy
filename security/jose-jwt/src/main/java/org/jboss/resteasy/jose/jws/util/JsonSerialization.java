package org.jboss.resteasy.jose.jws.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Copied from keycloak
 *
 * Any class that extends JsonWebToken will use NON_DEFAULT inclusion
 *
 */
public class JsonSerialization {
    public static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String writeValueAsString(Object obj) throws IOException {
        return mapper.writeValueAsString(obj);
    }

    public static byte[] writeValueAsBytes(Object obj) throws IOException {
        return mapper.writeValueAsBytes(obj);
    }

    public static <T> T readValue(byte[] bytes, Class<T> type) throws IOException {
        return mapper.readValue(bytes, type);
    }

    public static <T> T readValue(String bytes, Class<T> type) throws IOException {
        return mapper.readValue(bytes, type);
    }

    public static <T> T readValue(InputStream bytes, Class<T> type) throws IOException {
        return mapper.readValue(bytes, type);
    }



}
