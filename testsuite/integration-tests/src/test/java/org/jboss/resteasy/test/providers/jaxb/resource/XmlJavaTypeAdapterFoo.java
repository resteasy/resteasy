package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlJavaTypeAdapterFoo {
   @XmlJavaTypeAdapter(XmlJavaTypeAdapterAlienAdapter.class)
   @XmlElement
   XmlJavaTypeAdapterAlien alien;

   public void setName(String name) {
      alien = new XmlJavaTypeAdapterAlien();
      alien.setName(name);
   }

   public String toString() {
      return "Foo[Alien[" + alien.getName() + "]]: " + super.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof XmlJavaTypeAdapterFoo)) {
         return false;
      }
      XmlJavaTypeAdapterFoo foo = XmlJavaTypeAdapterFoo.class.cast(o);
      return alien.getName().equals(foo.alien.getName());
   }

   @Override
   public int hashCode() {
      return alien.hashCode();
   }

}
