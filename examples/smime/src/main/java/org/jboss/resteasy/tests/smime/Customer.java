package org.jboss.resteasy.tests.smime;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "customer")
public class Customer
{
   private String name;

   @XmlAttribute
   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   @Override
   public String toString()
   {
      return "Customer{" +
              "name='" + name + '\'' +
              '}';
   }
}
