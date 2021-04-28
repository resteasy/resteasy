package org.jboss.resteasy.test.providers.multipart.resource;

import jakarta.activation.DataHandler;
import jakarta.ws.rs.core.MediaType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlMimeType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "putFileRequest")
public class XOPMultipartProxyPutFileRequest {
   @XmlMimeType(MediaType.APPLICATION_OCTET_STREAM)
   private DataHandler content;

   public DataHandler getContent() {
      return content;
   }

   public void setContent(DataHandler value) {
      this.content = value;
   }
}
