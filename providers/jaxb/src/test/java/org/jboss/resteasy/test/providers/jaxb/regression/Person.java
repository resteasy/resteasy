package org.jboss.resteasy.test.providers.jaxb.regression;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Person
{
   @XmlAttribute(name = "id", required = true)
   protected String id;

   @XmlElement
   protected String name;

   public Person(String id, String name)
   {
      this.id = id;
      this.name = name;
   }

   public Person()
   {
   }

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }
}
