package org.jboss.resteasy.plugins.providers.multipart;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MultipartOutput
{

   protected List<OutputPart> parts = new ArrayList<OutputPart>();
   protected String boundary = "f231sldkxx11";

   public OutputPart addPart(Object entity, MediaType mediaType)
   {
      OutputPart outputPart = new OutputPart(entity, mediaType);
      parts.add(outputPart);
      return outputPart;
   }

   public List<OutputPart> getParts()
   {
      return parts;
   }

   public String getBoundary()
   {
      return boundary;
   }

   public void setBoundary(String boundary)
   {
      this.boundary = boundary;
   }
}
