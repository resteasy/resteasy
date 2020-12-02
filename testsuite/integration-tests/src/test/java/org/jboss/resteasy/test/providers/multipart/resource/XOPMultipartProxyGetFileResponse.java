package org.jboss.resteasy.test.providers.multipart.resource;

import javax.activation.DataHandler;
import jakarta.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;

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
