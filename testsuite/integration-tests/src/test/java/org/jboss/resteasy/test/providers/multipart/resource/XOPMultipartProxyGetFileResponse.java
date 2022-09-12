package org.jboss.resteasy.test.providers.multipart.resource;

import jakarta.activation.DataHandler;
import jakarta.ws.rs.core.MediaType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlMimeType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "getFileRestResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class XOPMultipartProxyGetFileResponse {
   @XmlMimeType(MediaType.APPLICATION_OCTET_STREAM)
   private DataHandler data;

   public XOPMultipartProxyGetFileResponse() {
      super();
   }

   public XOPMultipartProxyGetFileResponse(final DataHandler dh) {
      this.data = dh;
   }

   public void setData(DataHandler dh) {
      this.data = dh;
   }

   public DataHandler getData() {
      return data;
   }
}
