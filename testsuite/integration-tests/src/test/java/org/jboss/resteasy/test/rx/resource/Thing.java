package org.jboss.resteasy.test.rx.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Thing {

   @XmlElement
   private String name;

   public Thing() {
   }

   public Thing(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String toString() {
      return "Thing[" + name + "]";
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof Thing)) {
         return false;
      }
      return name.equals(((Thing) o).name);
   }
}
