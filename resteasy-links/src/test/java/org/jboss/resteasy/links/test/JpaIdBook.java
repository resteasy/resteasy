package org.jboss.resteasy.links.test;

import javax.persistence.Id;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JpaIdBook extends IdBook{

   @Id
   private String name;

   public JpaIdBook() {
   }

   public JpaIdBook(final String name) {
      this.name = name;
   }

}
