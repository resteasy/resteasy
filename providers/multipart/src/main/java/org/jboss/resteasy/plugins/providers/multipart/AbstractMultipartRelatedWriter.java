package org.jboss.resteasy.plugins.providers.multipart;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.spi.AsyncOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * Helper base class for multipart/related producing providers.
 *
 * @author Attila Kiraly
 * @version $Revision: 1 $
 */
public class AbstractMultipartRelatedWriter extends AbstractMultipartWriter {
   protected void writeRelated(MultipartRelatedOutput multipartRelatedOutput,
         MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
         OutputStream entityStream) throws IOException,
         WebApplicationException {
      for (OutputPart outputPart : multipartRelatedOutput.getParts())
         if (outputPart.getHeaders().get("Content-ID") == null)
            outputPart.getHeaders().putSingle("Content-ID",
                  ContentIDUtils.generateContentID());
      OutputPart rootOutputPart = multipartRelatedOutput.getRootPart();
      Map<String, String> mediaTypeParameters = new LinkedHashMap<String, String>(
            mediaType.getParameters());
      if (mediaTypeParameters.containsKey("boundary"))
         multipartRelatedOutput.setBoundary(mediaTypeParameters
               .get("boundary"));
      mediaTypeParameters.put("start", (String) rootOutputPart.getHeaders()
            .getFirst("Content-ID"));
      mediaTypeParameters.put("type", rootOutputPart.getMediaType().getType()
            + "/" + rootOutputPart.getMediaType().getSubtype());
      if (multipartRelatedOutput.getStartInfo() != null)
         mediaTypeParameters.put("start-info", multipartRelatedOutput
               .getStartInfo());
      MediaType modifiedMediaType = new MediaType(mediaType.getType(),
            mediaType.getSubtype(), mediaTypeParameters);
      write(multipartRelatedOutput, modifiedMediaType, httpHeaders,
            entityStream);
   }

   protected CompletionStage<Void> asyncWriteRelated(MultipartRelatedOutput multipartRelatedOutput,
                                                     MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                                                     AsyncOutputStream entityStream) {
       for (OutputPart outputPart : multipartRelatedOutput.getParts())
           if (outputPart.getHeaders().get("Content-ID") == null)
               outputPart.getHeaders().putSingle("Content-ID",
                                                 ContentIDUtils.generateContentID());
       OutputPart rootOutputPart = multipartRelatedOutput.getRootPart();
       Map<String, String> mediaTypeParameters = new LinkedHashMap<String, String>(
               mediaType.getParameters());
       if (mediaTypeParameters.containsKey("boundary"))
           multipartRelatedOutput.setBoundary(mediaTypeParameters
                                              .get("boundary"));
       mediaTypeParameters.put("start", (String) rootOutputPart.getHeaders()
                               .getFirst("Content-ID"));
       mediaTypeParameters.put("type", rootOutputPart.getMediaType().getType()
                               + "/" + rootOutputPart.getMediaType().getSubtype());
       if (multipartRelatedOutput.getStartInfo() != null)
           mediaTypeParameters.put("start-info", multipartRelatedOutput
                                   .getStartInfo());
       MediaType modifiedMediaType = new MediaType(mediaType.getType(),
                                                   mediaType.getSubtype(), mediaTypeParameters);
       return asyncWrite(multipartRelatedOutput, modifiedMediaType, httpHeaders,
             entityStream);
   }
}
