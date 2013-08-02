package org.jboss.resteasy.test.providers.atom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "customer", namespace = "http://jboss.org/Customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerAtom
{
   @XmlElement
   private String name;

   public CustomerAtom()
   {
   }

   public CustomerAtom(String name)
   {
      this.name = name;
   }

   public String getName()
   {
      return name;
   }
}
