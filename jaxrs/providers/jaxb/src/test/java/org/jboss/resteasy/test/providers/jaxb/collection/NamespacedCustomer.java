package org.jboss.resteasy.test.providers.jaxb.collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "customer", namespace = "http://customer.com")
@XmlAccessorType(XmlAccessType.FIELD)
public class NamespacedCustomer
{
   @XmlElement
   private String name;

   public NamespacedCustomer()
   {
   }

   public NamespacedCustomer(String name)
   {
      this.name = name;
   }

   public String getName()
   {
      return name;
   }
}