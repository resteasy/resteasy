package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AbstractMultipartFormDataWriter extends AbstractMultipartWriter
{
   @Override
   protected void writeParts(MultipartOutput multipartOutput, OutputStream entityStream, byte[] boundaryBytes) throws IOException
   {
      MultipartFormDataOutput form = (MultipartFormDataOutput) multipartOutput;
      for (Map.Entry<String, OutputPart> entry : form.getFormData().entrySet())
      {
         MultivaluedMap<String, Object> headers = new MultivaluedMapImpl<String, Object>();
         headers.putSingle("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\"");
         writePart(entityStream, boundaryBytes, entry.getValue(), headers);
      }

   }
}
