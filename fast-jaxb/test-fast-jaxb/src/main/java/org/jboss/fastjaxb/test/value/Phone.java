package org.jboss.fastjaxb.test.value;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "phone")
public class Phone
{
   private String number;

   @XmlValue
   public String getNumber()
   {
      return number;
   }

   public void setNumber(String number)
   {
      this.number = number;
   }

   @Override
   public String toString()
   {
      return "Phone{" +
              "number='" + number + '\'' +
              '}';
   }
}
