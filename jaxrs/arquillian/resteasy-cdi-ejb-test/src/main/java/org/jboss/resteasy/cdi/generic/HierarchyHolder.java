package org.jboss.resteasy.cdi.generic;

import java.lang.reflect.Type;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 14, 2012
 */
public class HierarchyHolder<T>
{
   private Class<T> clazz;
   
   public HierarchyHolder()
   {
   }
   
   public HierarchyHolder(Class<T> clazz)
   {
      this.clazz = clazz;
   }
   
   public Type getTypeArgument()
   {
      return clazz;
   }
}
