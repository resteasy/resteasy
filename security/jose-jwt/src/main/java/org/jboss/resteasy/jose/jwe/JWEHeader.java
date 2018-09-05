package org.jboss.resteasy.jose.jwe;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JWEHeader implements Serializable
{
   @JsonProperty("alg")
   private Algorithm algorithm;

   @JsonProperty("enc")
   private EncryptionMethod encryptionMethod;

   @JsonProperty("typ")
   private String type;

   @JsonProperty("cty")
   private String contentType;

   @JsonProperty("zip")
   private CompressionAlgorithm compressionAlgorithm;

   public JWEHeader()
   {
   }

   public Algorithm getAlgorithm()
   {
      return algorithm;
   }

   public String getType()
   {
      return type;
   }

   public String getContentType()
   {
      return contentType;
   }

   public CompressionAlgorithm getCompressionAlgorithm()
   {
      return compressionAlgorithm;
   }

   public void setCompressionAlgorithm(CompressionAlgorithm compressionAlgorithm)
   {
      this.compressionAlgorithm = compressionAlgorithm;
   }

   public EncryptionMethod getEncryptionMethod()
   {
      return encryptionMethod;
   }

   public void setEncryptionMethod(EncryptionMethod encryptionMethod)
   {
      this.encryptionMethod = encryptionMethod;
   }

   @JsonIgnore
   public MediaType getMediaType()
   {
      if (contentType == null) return null;
      return MediaType.valueOf(contentType);
   }

   private static final ObjectMapper mapper = new ObjectMapper();
   static
   {
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
   }

   public String toString()
   {
      try
      {
         return mapper.writeValueAsString(this);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }


   }

}
