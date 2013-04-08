package org.jboss.resteasy.cdi.injection;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Book is 
 * 
 * 1) a JAXB class, suitable for transport over the network, and
 * 2) an @Entity class, suitable for JPA storage
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1 $
 */
@Entity
@XmlRootElement(name = "book")
@XmlAccessorType(XmlAccessType.FIELD)
public class Book
{
   @XmlElement
   private int id;
   
   @XmlElement
   @NotNull
   @Size(min = 1, max = 25)
   private String name;

   public Book()
   {
   }
   public Book(String name)
   {
      this.name = name;
   }
   public Book(int id, String name)
   {
      this.id = id;
      this.name = name;
   }
   
   @Id
   public int getId()
   {
       return id;
   }
   public void setId(int id)
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
   
   public String toString()
   {
      return "Book[" + id + "," + name + "]";
   }
   
   public boolean equals(Object o)
   {
      if (o == null || ! (o instanceof Book))
      {
         return false;
      }
      return name.equals(((Book) o).name);
   }
}
