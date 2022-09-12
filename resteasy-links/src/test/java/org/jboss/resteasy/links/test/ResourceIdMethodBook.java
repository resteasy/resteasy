package org.jboss.resteasy.links.test;

import org.jboss.resteasy.links.ResourceID;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResourceIdMethodBook extends IdBook{

   private String name;

   public ResourceIdMethodBook() {
   }

   public ResourceIdMethodBook(final String name) {
      this.name = name;
   }

   @ResourceID
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

}
