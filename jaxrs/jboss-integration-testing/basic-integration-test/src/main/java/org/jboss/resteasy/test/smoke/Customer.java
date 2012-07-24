package org.jboss.resteasy.test.smoke;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement
public class Customer
{
   private String name;

   public Customer()
   {
   }

   public Customer(String name)
   {
      this.name = name;
   }

   @XmlElement
   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }
}
