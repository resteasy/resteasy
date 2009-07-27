package org.jboss.fastjaxb.test.basic;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 27, 2009
 * Time: 9:17:52 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name="phone")
public class PhoneNumber implements Phone
{
   protected String number;

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
      return "PhoneNumber{" +
              "number='" + number + '\'' +
              '}';
   }
}
