package org.jboss.resteasy.plugins.providers.jackson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.providers.AbstractPatchMethodFilter;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponseCodes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

/*
 * @author <a href="mailto:ema@redhat.com">Jim Ma</a>
 */
@Provider
@Priority(Integer.MAX_VALUE)
public class PatchMethodFilter extends AbstractPatchMethodFilter {
    private volatile ObjectMapper objectMapper;

    protected boolean isDisabled(ContainerRequestContext requestContext) {
       return this.readFilterDisabledFlag(requestContext) != FilterFlag.JACKSON;
    }

    protected byte[] applyPatch(ContainerRequestContext requestContext, byte[] targetJsonBytes) throws IOException, Failure {
        HttpRequest request = ResteasyContext.getContextData(HttpRequest.class);
        ObjectMapper mapper = getObjectMapper();
        PolymorphicTypeValidator ptv = mapper.getPolymorphicTypeValidator();
        //the check is protected by test org.jboss.resteasy.test.providers.jackson2.whitelist.JacksonConfig,
        //be sure to keep that in synch if changing anything here.
        if (ptv == null || ptv instanceof LaissezFaireSubTypeValidator) {
            mapper.setPolymorphicTypeValidator(new WhiteListPolymorphicTypeValidatorBuilder().build());
        }
        JsonNode targetJson = mapper.readValue(targetJsonBytes, JsonNode.class);

        JsonNode result = null;
        ByteArrayOutputStream targetOutputStream = new ByteArrayOutputStream();
        try {
            if (MediaType.APPLICATION_JSON_PATCH_JSON_TYPE.isCompatible(requestContext.getMediaType())) {
                JsonPatch patch = JsonPatch.fromJson(mapper.readValue(request.getInputStream(), JsonNode.class));
                result = patch.apply(targetJson);
            } else {
                final JsonMergePatch mergePatch = JsonMergePatch.fromJson(mapper.readValue(request.getInputStream(), JsonNode.class));
                result = mergePatch.apply(targetJson);
            }
            mapper.writeValue(targetOutputStream, result);
        } catch (JsonPatchException e) {
            throw new Failure(e, HttpResponseCodes.SC_CONFLICT);
        }
        return targetOutputStream.toByteArray();
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper currentObjectMapper = objectMapper;
        if (currentObjectMapper == null) {
            synchronized (this) {
                currentObjectMapper = objectMapper;
                if (currentObjectMapper == null) {
                    ObjectMapper contextMapper = getContextObjectMapper();
                    currentObjectMapper = (contextMapper == null) ? new ObjectMapper() : contextMapper;
                    this.objectMapper = currentObjectMapper;
                }
            }
        }
        return currentObjectMapper;
    }

    private ObjectMapper getContextObjectMapper() {
        ContextResolver<ObjectMapper> resolver = providers
                .getContextResolver(ObjectMapper.class, MediaType.APPLICATION_JSON_TYPE);
        if (resolver == null)
            return null;
        return resolver.getContext(ObjectMapper.class);
    }
}
