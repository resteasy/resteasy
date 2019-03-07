package org.jboss.resteasy.test.asynch.resource;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "data")
public class JaxrsAsyncServletXmlData {
   protected String name;

   public JaxrsAsyncServletXmlData(final String data) {
      this.name = data;
   }

   public JaxrsAsyncServletXmlData() {
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

}
