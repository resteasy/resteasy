package org.jboss.fastjaxb.test.template;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 24, 2009
 * Time: 10:00:24 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name="person", namespace = "urn:person")
public class Person
{
   private String id;
   private String name;
   private List<Address> addresses;
   private List<Address> businessAddresses;

   
   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   @XmlElement(name="business-address")
   public List<Address> getBusinessAddresses()
   {
      return businessAddresses;
   }

   public void setBusinessAddresses(List<Address> businessAddresses)
   {
      this.businessAddresses = businessAddresses;
   }

   @XmlElementRef
   public List<Address> getAddresses()
   {
      return addresses;
   }

   public void setAddresses(List<Address> addresses)
   {
      this.addresses = addresses;
   }

   @XmlAttribute
   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   @Override
   public String toString()
   {
      return "Person{" +
              "id='" + id + '\'' +
              ", name='" + name + '\'' +
              ", addresses=" + addresses +
              ", businessAddresses=" + businessAddresses +
              '}';
   }
}
