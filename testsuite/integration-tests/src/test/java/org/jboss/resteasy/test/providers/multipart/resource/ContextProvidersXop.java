package org.jboss.resteasy.test.providers.multipart.resource;

import jakarta.ws.rs.core.MediaType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlMimeType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ContextProvidersXop {
   @XmlMimeType(MediaType.APPLICATION_OCTET_STREAM)
   private byte[] bytes;

   public ContextProvidersXop(final byte[] bytes) {
      this.bytes = bytes;
   }

   public ContextProvidersXop() {
   }

   public byte[] getBytes() {
      return bytes;
   }

   public void setBytes(byte[] bytes) {
      this.bytes = bytes;
   }
}
