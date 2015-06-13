package org.jboss.resteasy.resteasy1119;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/** 
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 10, 2015
 */
@XmlRootElement(name = "name")
@XmlAccessorType(XmlAccessType.FIELD)
public class Name
{
   @XmlElement
   private String name;

   public Name()
   {
   }

   public Name(String name)
   {
      this.name = name;
   }

   public String getName()
   {
      return name;
   }
   
   public boolean equals(Object o)
   {
      if (o == null)
         return false;
      if (!(o instanceof Name))
         return false;
      return name.equals(((Name) o).getName());
   }
}
