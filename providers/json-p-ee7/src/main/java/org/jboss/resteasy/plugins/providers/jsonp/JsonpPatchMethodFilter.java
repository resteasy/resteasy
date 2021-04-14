package org.jboss.resteasy.plugins.providers.jsonp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonMergePatch;
import javax.json.JsonObject;
import javax.json.JsonPatch;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.providers.AbstractPatchMethodFilter;
import org.jboss.resteasy.spi.HttpRequest;

public class JsonpPatchMethodFilter extends AbstractPatchMethodFilter
{
    private static final JsonReaderFactory readerFactory = Json.createReaderFactory(null);

    private static final JsonWriterFactory writerFactory = Json.createWriterFactory(null);

    protected byte[] applyPatch(ContainerRequestContext requestContext, byte[] targetJsonBytes) throws Exception
    {
        HttpRequest request = ResteasyContext.getContextData(HttpRequest.class);

        ContextResolver<JsonReaderFactory> resolver = providers
              .getContextResolver(JsonReaderFactory.class, requestContext.getMediaType());
        JsonReaderFactory factory = null;
        if (resolver != null)
        {
            factory = resolver.getContext(JsonReaderFactory.class);
        }
        if (factory == null)
        {
            factory = readerFactory;
        }
        Charset charset = AbstractJsonpProvider.getCharset(requestContext.getMediaType());
        ByteArrayInputStream is = new ByteArrayInputStream(targetJsonBytes);
        JsonReader reader = charset == null ? factory.createReader(is) : factory.createReader(is, charset);
        JsonObject targetJson = reader.readObject();
        JsonObject result = null;
        if (MediaType.APPLICATION_JSON_PATCH_JSON_TYPE.isCompatible(requestContext.getMediaType()))
        {
            JsonArray jsonArray = factory.createReader(request.getInputStream(), charset).readArray();
            JsonPatch patch = Json.createPatch(jsonArray);
            patch.apply(targetJson);
        }
        else
        {
            JsonValue mergePatchValue = factory.createReader(request.getInputStream(), charset).readValue();
            final JsonMergePatch mergePatch = Json.createMergePatch(mergePatchValue);
            result = mergePatch.apply(targetJson).asJsonObject();
        }
        ContextResolver<JsonWriterFactory> writerResolver = providers
              .getContextResolver(JsonWriterFactory.class, requestContext.getMediaType());
        JsonWriterFactory wfactory = null;
        if (resolver != null)
        {
            wfactory = writerResolver.getContext(JsonWriterFactory.class);
        }
        if (wfactory == null)
        {
            wfactory = writerFactory;
        }
        ByteArrayOutputStream targetOutputStream = new ByteArrayOutputStream();
        JsonWriter jsonWriter = wfactory.createWriter(targetOutputStream, charset);
        jsonWriter.write(result);
        return targetOutputStream.toByteArray();
    }
}