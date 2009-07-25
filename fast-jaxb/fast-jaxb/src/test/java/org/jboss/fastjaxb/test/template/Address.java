package org.jboss.fastjaxb.test.template;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 24, 2009
 * Time: 10:00:46 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name="address")
public class Address
{
   private String street;
   private String city;

   public String getStreet()
   {
      return street;
   }

   public void setStreet(String street)
   {
      this.street = street;
   }

   public String getCity()
   {
      return city;
   }

   public void setCity(String city)
   {
      this.city = city;
   }

   @Override
   public String toString()
   {
      return "Address{" +
              "street='" + street + '\'' +
              ", city='" + city + '\'' +
              '}';
   }
}
