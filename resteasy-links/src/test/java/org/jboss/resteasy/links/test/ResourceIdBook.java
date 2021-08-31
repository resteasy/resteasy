package org.jboss.resteasy.links.test;

import org.jboss.resteasy.links.ResourceID;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResourceIdBook extends IdBook{

   @ResourceID
   private String name;
   public ResourceIdBook() {
   }

   public ResourceIdBook(final String name) {
      this.name = name;
   }

}
