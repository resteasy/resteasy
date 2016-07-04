package org.jboss.resteasy.cdi.generic;

import java.lang.reflect.Type;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Dec 14, 2012
 */
public class LowerBoundHierarchyHolder<T extends HierarchyHolder<? super Primate>>
{
   private Class<?> clazz;
   
   public LowerBoundHierarchyHolder(Class<?> clazz)
   {
      this.clazz = clazz;
   }

   Type getTypeArgument()
   {
      return clazz;
   }
}
