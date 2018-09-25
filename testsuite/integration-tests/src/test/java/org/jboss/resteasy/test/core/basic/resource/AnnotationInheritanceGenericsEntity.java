package org.jboss.resteasy.test.core.basic.resource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "entity")
public class AnnotationInheritanceGenericsEntity {

   private Long id;

   @XmlElement
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

}
