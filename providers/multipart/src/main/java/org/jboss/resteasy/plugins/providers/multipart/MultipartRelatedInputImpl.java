package org.jboss.resteasy.plugins.providers.multipart;

import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.field.ContentTypeField;
import org.apache.james.mime4j.dom.field.FieldName;
import org.apache.james.mime4j.message.BodyPart;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implements {@link MultipartRelatedInput} by extending
 * {@link MultipartInputImpl} and adding multipart/related functionality.
 *
 * @author Attila Kiraly
 * @version $Revision: 1 $
 */
public class MultipartRelatedInputImpl extends MultipartInputImpl implements
      MultipartRelatedInput {
   private Map<String, InputPart> relatedMap;
   private String start;
   private String startInfo;
   private String type;
   private InputPart rootPart;

   public MultipartRelatedInputImpl(final MediaType contentType, final Providers workers) {
      super(contentType, workers);
   }

   public MultipartRelatedInputImpl (final Multipart multipart, final Providers workers)
           throws IOException {
      super(multipart, workers);
   }

   @Override
   public void parse(InputStream is) throws IOException {
      super.parse(is);
      ContentTypeField contentTypeField = (ContentTypeField) mimeMessage
            .getHeader().getField(FieldName.CONTENT_TYPE);
      start = contentTypeField.getParameter("start");
      startInfo = contentTypeField.getParameter("start-info");
      type = contentTypeField.getParameter("type");
      rootPart = start == null ? getParts().get(0) : getRelatedMap().get(start);
   }

   @Override
   protected InputPart extractPart(BodyPart bodyPart) throws IOException {
      InputPart inputPart = super.extractPart(bodyPart);
      getRelatedMap()
            .put(inputPart.getHeaders().getFirst("Content-ID"), inputPart);
      return inputPart;
   }

   public Map<String, InputPart> getRelatedMap() {
      if (relatedMap == null) {
         relatedMap = new LinkedHashMap<String, InputPart>();
      }
      return relatedMap;
   }

   public InputPart getRootPart() {
      return rootPart;
   }

   public String getStart() {
      return start;
   }

   public String getStartInfo() {
      return startInfo;
   }

   public String getType() {
      return type;
   }

}
