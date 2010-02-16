package org.jboss.resteasy.star.bpm;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.MediaType;
import java.io.Serializable;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Representation implements Serializable
{
   private String mediaType;
   private transient MediaType type;
   private byte[] representation;

   public Representation()
   {
   }

   public Representation(MediaType mediaType, byte[] representation)
   {
      this.type = mediaType;
      this.mediaType = mediaType.toString();
      this.representation = representation;
   }

   public Representation(String mediaType, byte[] representation)
   {
      this.mediaType = mediaType;
      this.representation = representation;
   }

   public MediaType getMediaType()
   {
      if (type == null)
      {
         type = MediaType.valueOf(mediaType);
      }
      return type;
   }

   public byte[] getRepresentation()
   {
      return representation;
   }

   public void setRepresentation(byte[] representation)
   {
      this.representation = representation;
   }

   public Representation transformTo(MediaType mediaType, ResteasyProviderFactory factory)
   {
      throw new RuntimeException("Transformation not supported");
   }

}
