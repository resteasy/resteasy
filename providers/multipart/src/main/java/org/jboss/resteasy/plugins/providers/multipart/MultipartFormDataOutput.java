package org.jboss.resteasy.plugins.providers.multipart;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MultipartFormDataOutput extends MultipartOutput {
    @Deprecated(forRemoval = true)
    protected Map<String, OutputPart> formData = new LinkedHashMap<String, OutputPart>();
    @Deprecated(forRemoval = true)
    protected Map<String, List<OutputPart>> formDataMap = new LinkedHashMap<>();

    private void addFormDataMap(String key, OutputPart part) {
        getFormDataMap().computeIfAbsent(key, (ignore) -> new LinkedList<>()).add(part);
    }

    public Map<String, List<OutputPart>> getFormDataMap() {
        return formDataMap;
    }

    public OutputPart addFormData(String key, Object entity, MediaType mediaType) {
        OutputPart part = super.addPart(entity, mediaType);
        formData.put(key, part);
        addFormDataMap(key, part);
        return part;
    }

    public OutputPart addFormData(String key, Object entity, MediaType mediaType, String filename) {
        return addFormData(key, entity, mediaType, filename, false);
    }

    public OutputPart addFormData(String key, Object entity, MediaType mediaType, String filename, boolean utf8Encode) {
        OutputPart part = super.addPart(entity, mediaType, filename, utf8Encode);
        formData.put(key, part);
        addFormDataMap(key, part);
        return part;
    }

    public OutputPart addFormData(String key, Object entity, GenericType<?> type, MediaType mediaType) {
        OutputPart part = super.addPart(entity, type, mediaType);
        formData.put(key, part);
        addFormDataMap(key, part);
        return part;
    }

    public OutputPart addFormData(String key, Object entity, GenericType<?> type, MediaType mediaType, String filename) {
        return addFormData(key, entity, type, mediaType, filename, false);
    }

    public OutputPart addFormData(String key, Object entity, GenericType<?> type, MediaType mediaType, String filename,
            boolean utf8Encode) {
        OutputPart part = super.addPart(entity, type, mediaType, filename, utf8Encode);
        formData.put(key, part);
        addFormDataMap(key, part);
        return part;
    }

    public OutputPart addFormData(String key, Object entity, Class<?> type, Type genericType, MediaType mediaType) {
        OutputPart part = super.addPart(entity, type, genericType, mediaType);
        formData.put(key, part);
        addFormDataMap(key, part);
        return part;
    }

    public OutputPart addFormData(String key, Object entity, Class<?> type, Type genericType, MediaType mediaType,
            String filename) {
        return addFormData(key, entity, type, genericType, mediaType, filename, false);
    }

    public OutputPart addFormData(String key, Object entity, Class<?> type, Type genericType, MediaType mediaType,
            String filename, boolean utf8Encode) {
        OutputPart part = super.addPart(entity, type, genericType, mediaType, filename, utf8Encode);
        formData.put(key, part);
        addFormDataMap(key, part);
        return part;
    }

    @Deprecated(forRemoval = true)
    public Map<String, OutputPart> getFormData() {
        return formData;
    }
}
