package org.jboss.resteasy.plugins.providers.jsonp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.annotation.Priority;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonMergePatch;
import javax.json.JsonObject;
import javax.json.JsonPatch;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.providers.AbstractPatchMethodFilter;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponseCodes;

@Provider
@Priority(Integer.MAX_VALUE - 1)
public class JsonpPatchMethodFilter extends AbstractPatchMethodFilter {
    private static final JsonReaderFactory readerFactory = Json.createReaderFactory(null);

    private static final JsonWriterFactory writerFactory = Json.createWriterFactory(null);

    protected boolean isDisabled(ContainerRequestContext requestContext) {
        if (this.readFilterDisabledFlag(requestContext) == FilterFlag.JSONP) {
            return false;
        }
        return true;
    }

    @Override
    protected byte[] applyPatch(final ContainerRequestContext requestContext, final byte[] targetJsonBytes) throws IOException,
            ProcessingException {
        HttpRequest request = ResteasyContext.getContextData(HttpRequest.class);
        //TODO: look at if we need to get reader factory from ContextResolver
        Charset charset = AbstractJsonpProvider.getCharset(requestContext.getMediaType());
        ByteArrayInputStream is = new ByteArrayInputStream(targetJsonBytes);
        if (charset == null) {
            charset = Charset.defaultCharset();
        }
        JsonReader reader = readerFactory.createReader(is, charset);
        JsonObject targetJson = reader.readObject();
        JsonObject result = null;
        try {
            if (MediaType.APPLICATION_JSON_PATCH_JSON_TYPE.isCompatible(requestContext.getMediaType())) {
                JsonReader arrayReader = readerFactory.createReader(request.getInputStream(), charset);
                JsonArray jsonArray = arrayReader.readArray();
                JsonPatch patch = Json.createPatch(jsonArray);
                result = patch.apply(targetJson);
            } else {
                JsonReader valueReader = readerFactory.createReader(request.getInputStream(), charset);
                JsonValue mergePatchValue = valueReader.readValue();
                final JsonMergePatch mergePatch = Json.createMergePatch(mergePatchValue);
                result = mergePatch.apply(targetJson).asJsonObject();
            }
        } catch (JsonException e) {
            //this is kind of hack and JsonPatch/JsonMergePatch doesn't imply this a parsing exception or patch exception
            //TODO: talk with jsonp community fix this
            if (e.getMessage().contains("Illegal value") || e.getMessage().contains("JSON Patch must")) {
                throw new BadRequestException(e.getMessage());
            }
            throw new Failure(e, HttpResponseCodes.SC_CONFLICT);
        }
        ByteArrayOutputStream targetOutputStream = new ByteArrayOutputStream();
        JsonWriter jsonWriter = writerFactory.createWriter(targetOutputStream, charset);
        jsonWriter.write(result);
        return targetOutputStream.toByteArray();
    }
}