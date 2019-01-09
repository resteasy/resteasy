package org.jboss.resteasy.test.providers.multipart.resource;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class XOPMultipartProxyGenericRestResponse {
   private Exception exception;

   public XOPMultipartProxyGenericRestResponse() {
   }

   public XOPMultipartProxyGenericRestResponse(final Exception e) {
      this.exception=e;
   }

   public Exception getException() {
      return exception;
   }
}
