package org.jboss.resteasy.test.providers.multipart.resource;

import javax.activation.DataHandler;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="getFileRestResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class XOPMultipartProxyGetFileRestResponse extends XOPMultipartProxyGenericRestResponse {
   @XmlMimeType(MediaType.APPLICATION_OCTET_STREAM)
   private DataHandler data;

   public XOPMultipartProxyGetFileRestResponse() {
      super();
   }

   public XOPMultipartProxyGetFileRestResponse(final Exception e) {
      super(e);
   }

   public XOPMultipartProxyGetFileRestResponse(final DataHandler dh) {
      this.data = dh;
   }

   public void setData(DataHandler dh) {
      this.data = dh;
   }

   public DataHandler getData() {
      return data;
   }
}
