package org.jboss.resteasy.cdi.inheritence;

/**
 * An instance of this class will be injected into the Book injection point in InheritenceResource
 * if no alternatives are enabled.
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 5, 2012
 */
@SelectBook
public class Book
{
   public Class<?> getType()
   {
      return this.getClass();
   }
}
