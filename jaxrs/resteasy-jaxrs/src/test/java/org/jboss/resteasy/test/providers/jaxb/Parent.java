/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.test.providers.jaxb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A Parent.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "parentType")
public class Parent
{
   private String name;

   private Date date = new Date();

   @XmlElementWrapper(name = "children")
   @XmlElement(name = "child")
   private List<Child> children = new ArrayList<Child>();

   public Parent()
   {

   }

   public Parent(String name)
   {
      this.name = name;
   }

   /**
    * Get the name.
    * 
    * @return the name.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the name.
    * 
    * @param name The name to set.
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * Get the date.
    * 
    * @return the date.
    */
   public final Date getDate()
   {
      return date;
   }

   /**
    * Set the date.
    * 
    * @param date The date to set.
    */
   public void setDate(final Date date)
   {
      this.date = date;
   }

   /**
    * Get the children.
    * 
    * @return the children.
    */
   public List<Child> getChildren()
   {
      return children;
   }

   /**
    * Set the children.
    * 
    * @param children The children to set.
    */
   public void setChildren(List<Child> children)
   {
      this.children = children;
   }

   public void addChild(Child child)
   {
      child.setParent(this);
      this.children.add(child);
   }

   public static Parent createTestParent(String name)
   {
      Parent parent = new Parent(name);
      parent.addChild(new Child("Child 1"));
      parent.addChild(new Child("Child 2"));
      parent.addChild(new Child("Child 3"));
      return parent;
   }
}
