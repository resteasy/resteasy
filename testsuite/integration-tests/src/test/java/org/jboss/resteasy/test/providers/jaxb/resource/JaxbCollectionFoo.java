package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "foo")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbCollectionFoo {
   @XmlAttribute
   private String test;

   public JaxbCollectionFoo() {
   }

   public JaxbCollectionFoo(final String test) {
      this.test = test;
   }

   public String getTest() {
      return test;
   }

   public void setTest(String test) {
      this.test = test;
   }
}
