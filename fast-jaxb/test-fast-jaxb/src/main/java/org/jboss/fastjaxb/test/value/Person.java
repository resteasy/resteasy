package org.jboss.fastjaxb.test.value;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "person", namespace = "urn:person")
public class Person
{
   private String id;
   private String name;
   private Phone home;
   private Phone mobile;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
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

   public Phone getHome()
   {
      return home;
   }

   public void setHome(Phone home)
   {
      this.home = home;
   }

   public Phone getMobile()
   {
      return mobile;
   }

   public void setMobile(Phone mobile)
   {
      this.mobile = mobile;
   }

   @Override
   public String toString()
   {
      return "Person{" +
              "id='" + id + '\'' +
              ", name='" + name + '\'' +
              ", home=" + home +
              ", mobile=" + mobile +
              '}';
   }
}