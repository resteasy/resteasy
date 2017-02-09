package org.jboss.resteasy.plugins.providers.jackson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.specimpl.MultivaluedTreeMap;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
/*
* @author <a href="mailto:ema@redhat.com">Jim Ma</a>
*/
@Provider
@Priority(Integer.MAX_VALUE)
public class PatchMethodFilter implements ContainerRequestFilter
{
   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      //Strict the filter is only executed for patch method and media type is APPLICATION_JSON_PATCH_JSON_TYPE
      if (requestContext.getMethod().equals("PATCH")
            && MediaType.APPLICATION_JSON_PATCH_JSON_TYPE.equals(requestContext.getMediaType()))
      {

         HttpRequest request = ResteasyProviderFactory.getContextData(HttpRequest.class);
         HttpResponse response = ResteasyProviderFactory.getContextData(HttpResponse.class);
         request.setHttpMethod("GET");
         Registry methodRegistry = ResteasyProviderFactory.getContextData(Registry.class);
         ResourceInvoker resourceInovker = methodRegistry.getResourceInvoker(request);
         if (resourceInovker == null)
         {
            throw new ProcessingException("Get method not found and patch method failed");
         }
         ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) resourceInovker;
         Object object = methodInvoker.invokeDryRun(request, response);

         ByteArrayOutputStream tmpOutputStream = new ByteArrayOutputStream();
         MessageBodyWriter msgBodyWriter = ResteasyProviderFactory.getInstance().getMessageBodyWriter(
               object.getClass(), object.getClass(), methodInvoker.getMethodAnnotations(),
               MediaType.APPLICATION_JSON_TYPE);
         msgBodyWriter.writeTo(object, object.getClass(), object.getClass(), methodInvoker.getMethodAnnotations(),
               MediaType.APPLICATION_JSON_TYPE, new MultivaluedTreeMap<String, Object>(), tmpOutputStream);
         ObjectMapper mapper = new ObjectMapper();
         JsonNode targetJson = mapper.readValue(tmpOutputStream.toByteArray(), JsonNode.class);
         JsonPatch patch = JsonPatch.fromJson(mapper.readValue(request.getInputStream(), JsonNode.class));

         JsonNode result = null;
         try
         {
            result = patch.apply(targetJson);
         }
         catch (JsonPatchException e)
         {
            throw new ProcessingException(e);
         }
         ByteArrayOutputStream targetOutputStream = new ByteArrayOutputStream();
         mapper.writeValue(targetOutputStream, result);
         request.setInputStream(new ByteArrayInputStream(targetOutputStream.toByteArray()));

         request.setHttpMethod("PATCH");

      }

   }

}
