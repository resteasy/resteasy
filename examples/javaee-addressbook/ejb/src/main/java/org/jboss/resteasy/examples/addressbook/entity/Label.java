/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.entity;

/**
 * A Label.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public enum Label {

   HOME("Home"),
   WORK("Work"),
   PERSONAL("Personal"),
   OTHER("Other");
   
   private String name;
   
   private Label(String name) {
      this.name = name;
   }

   
   
   @Override
   public String toString()
   {
     return name;
   }
}
