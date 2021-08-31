package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.plugins.providers.multipart.i18n.Messages;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.AsyncOutputStream;

import jakarta.ws.rs.core.MultivaluedMap;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AbstractMultipartFormDataWriter extends AbstractMultipartWriter {
   @Override
   protected void writeParts(MultipartOutput multipartOutput,
                             OutputStream entityStream, byte[] boundaryBytes) throws IOException {
      if (!(multipartOutput instanceof MultipartFormDataOutput))
         throw new IllegalArgumentException(Messages.MESSAGES.hadToWriteMultipartOutput(multipartOutput, this, MultipartFormDataOutput.class));
      MultipartFormDataOutput form = (MultipartFormDataOutput) multipartOutput;
      for (Map.Entry<String, List<OutputPart>> entry : form.getFormDataMap().entrySet()) {
         for (OutputPart outputPart : entry.getValue()) {
            if (outputPart.getEntity() == null) {
               continue;
            }
            MultivaluedMap<String, Object> headers = new MultivaluedMapImpl<String, Object>();
            headers.putSingle("Content-Disposition", "form-data; name=\""
                  + entry.getKey() + "\""
                  + getFilename(outputPart));
            writePart(entityStream, boundaryBytes, outputPart, headers);
         }
      }
   }

   @Override
   protected CompletionStage<Void> asyncWriteParts(MultipartOutput multipartOutput, AsyncOutputStream entityStream, byte[] boundaryBytes) {
       if (!(multipartOutput instanceof MultipartFormDataOutput))
           throw new IllegalArgumentException(Messages.MESSAGES.hadToWriteMultipartOutput(multipartOutput, this, MultipartFormDataOutput.class));
       MultipartFormDataOutput form = (MultipartFormDataOutput) multipartOutput;
       CompletionStage<Void> ret = CompletableFuture.completedFuture(null);
       for (Map.Entry<String, List<OutputPart>> entry : form.getFormDataMap().entrySet()) {
           for (OutputPart outputPart : entry.getValue()) {
               if (outputPart.getEntity() == null) {
                   continue;
               }
               MultivaluedMap<String, Object> headers = new MultivaluedMapImpl<String, Object>();
               headers.putSingle("Content-Disposition", "form-data; name=\""
                       + entry.getKey() + "\""
                       + getFilename(outputPart));
               ret = ret.thenCompose(v -> asyncWritePart(entityStream, boundaryBytes, outputPart, headers));
           }
       }
       return ret;
   }

   private String getFilename(OutputPart part) {
      String filename = part.getFilename();
      if (filename == null) {
         return "";
      } else {
         if (part.isUtf8Encode()) {
            String encodedFilename = filename;
            try {
               encodedFilename = URLEncoder.encode(filename, "UTF-8");
               // append encoding charset into the value if and only if encoding was needed
               if (!encodedFilename.equals(filename)) {
               // encoding was needed, so per rfc5987 we have to prepend charset
                  return "; filename*=utf-8''" + encodedFilename.replaceAll("\\+", "%20");
               }
            } catch (UnsupportedEncodingException e) {
               // should not happen
            }
         }
         return "; filename=\"" + filename + "\"";
      }
   }
}
