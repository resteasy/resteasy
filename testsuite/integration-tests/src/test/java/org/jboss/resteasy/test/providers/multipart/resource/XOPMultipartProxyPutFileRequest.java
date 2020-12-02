package org.jboss.resteasy.test.providers.multipart.resource;

import javax.activation.DataHandler;
import jakarta.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;

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
