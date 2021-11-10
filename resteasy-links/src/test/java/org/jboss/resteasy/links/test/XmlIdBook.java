package org.jboss.resteasy.links.test;

import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class XmlIdBook extends IdBook {

   @XmlID
   private String name;
   public XmlIdBook() {
   }

   public XmlIdBook(final String name) {
      this.name = name;
   }

}
