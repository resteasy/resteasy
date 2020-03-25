package org.jboss.resteasy.plugins.providers.jackson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Priority;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.specimpl.MultivaluedTreeMap;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpResponseCodes;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
public class PatchMethodFilter implements ContainerRequestFilter
{

   private volatile ObjectMapper objectMapper;

   @Context
   protected Providers providers;
   //TODO:thse should go to jaxrs spec apis
   public static final String APPLICATION_JSON_MERGE_PATCH_JSON = "application/merge-patch+json";

   public static final MediaType APPLICATION_JSON_MERGE_PATCH_JSON_TYPE = new MediaType("application",
         "merge-patch+json");

   @Override
   @SuppressWarnings(
   {"rawtypes", "unchecked"})
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      if (requestContext.getMethod().equals("PATCH")
            && (MediaType.APPLICATION_JSON_PATCH_JSON_TYPE.isCompatible(requestContext.getMediaType())
            || APPLICATION_JSON_MERGE_PATCH_JSON_TYPE.isCompatible(requestContext.getMediaType())))
      {
         ResteasyConfiguration context = ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
         boolean disabled = false;
         if (context == null) {
            disabled = Boolean.getBoolean(ResteasyContextParameters.RESTEASY_PATCH_FILTER_DISABLED);
         } else {
            disabled = Boolean.parseBoolean(context.getParameter(ResteasyContextParameters.RESTEASY_PATCH_FILTER_DISABLED));
         }
         if (disabled) {
            return;
         }
         HttpRequest request = ResteasyProviderFactory.getContextData(HttpRequest.class);
         HttpResponse response = ResteasyProviderFactory.getContextData(HttpResponse.class);
         request.setHttpMethod("GET");
         List<String> patchContentTypeList = new ArrayList<String>();
         for (String header : request.getHttpHeaders().getRequestHeader(HttpHeaders.CONTENT_TYPE))
         {
            patchContentTypeList.add(header);
         }
         List<String> acceptHeaders = new ArrayList<String>();
         for (String header : request.getHttpHeaders().getRequestHeader(HttpHeaders.ACCEPT))
         {
            acceptHeaders.add(header);
         }
         requestContext.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, MediaType.WILDCARD);
         requestContext.getHeaders().putSingle(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
         Registry methodRegistry = ResteasyProviderFactory.getContextData(Registry.class);
         ResourceInvoker resourceInovker = null;
         try
         {
            resourceInovker = methodRegistry.getResourceInvoker(request);
         }
         catch (Exception e)
         {
            LogMessages.LOGGER.patchTargetMethodNotFound(requestContext.getUriInfo().getRequestUri().toString());
            throw new ProcessingException("GET method returns the patch/merge json object target not found");
         }
         ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) resourceInovker;
         Object object;
         try{
            object = methodInvoker.invokeDryRun(request, response);
            ByteArrayOutputStream tmpOutputStream = new ByteArrayOutputStream();
            MessageBodyWriter msgBodyWriter = ResteasyProviderFactory.getInstance().getMessageBodyWriter(
                  object.getClass(), object.getClass(), methodInvoker.getMethodAnnotations(),
                  MediaType.APPLICATION_JSON_TYPE);
            msgBodyWriter.writeTo(object, object.getClass(), object.getClass(), methodInvoker.getMethodAnnotations(),
                  MediaType.APPLICATION_JSON_TYPE, new MultivaluedTreeMap<String, Object>(), tmpOutputStream);
            ObjectMapper mapper = getObjectMapper();
            PolymorphicTypeValidator ptv = mapper.getPolymorphicTypeValidator();
            //the check is protected by test org.jboss.resteasy.test.providers.jackson2.whitelist.JacksonConfig,
            //be sure to keep that in synch if changing anything here.
            if (ptv == null || ptv instanceof LaissezFaireSubTypeValidator) {
               mapper.setPolymorphicTypeValidator(new WhiteListPolymorphicTypeValidatorBuilder().build());
            }
            JsonNode targetJson = mapper.readValue(tmpOutputStream.toByteArray(), JsonNode.class);
            requestContext.getHeaders().put(HttpHeaders.CONTENT_TYPE, patchContentTypeList);
            requestContext.getHeaders().put(HttpHeaders.ACCEPT, acceptHeaders);
            JsonNode result = null;
            if (MediaType.APPLICATION_JSON_PATCH_JSON_TYPE.isCompatible(requestContext.getMediaType()))
            {
               JsonPatch patch = JsonPatch.fromJson(mapper.readValue(request.getInputStream(), JsonNode.class));
               result = patch.apply(targetJson);
            }
            else
            {
               final JsonMergePatch mergePatch = JsonMergePatch.fromJson(mapper.readValue(request.getInputStream(),
                     JsonNode.class));
               result = mergePatch.apply(targetJson);
            }
            ByteArrayOutputStream targetOutputStream = new ByteArrayOutputStream();
            mapper.writeValue(targetOutputStream, result);
            request.setInputStream(new ByteArrayInputStream(targetOutputStream.toByteArray()));
            request.setHttpMethod("PATCH");
         }
         catch (ProcessingException pe)
         {
            Throwable c = pe.getCause();
            if (c != null && c instanceof ApplicationException)
            {
               c = c.getCause();
               if (c != null && c instanceof NotFoundException)
               {
                  throw (NotFoundException) c;
               }
            }
            throw pe;
         }
         catch (JsonMappingException | JsonParseException e)
         {
            throw new BadRequestException(e);
         }
         catch (JsonPatchException e)
         {
            throw new Failure(e, HttpResponseCodes.SC_CONFLICT);
         }
      }
   }
   private ObjectMapper getObjectMapper() {
      if (objectMapper == null) {
          synchronized(this) {
              if (objectMapper == null) {
                 ObjectMapper contextMapper = getContextObjectMapper();
                 objectMapper = (contextMapper == null) ? new ObjectMapper() : contextMapper;
              }
          }
      }
      return objectMapper;
   }

   private ObjectMapper getContextObjectMapper()
   {
      ContextResolver<ObjectMapper> resolver = providers.getContextResolver(ObjectMapper.class, MediaType.APPLICATION_JSON_TYPE);
      if (resolver == null) return null;
      return resolver.getContext(ObjectMapper.class);
   }
}
