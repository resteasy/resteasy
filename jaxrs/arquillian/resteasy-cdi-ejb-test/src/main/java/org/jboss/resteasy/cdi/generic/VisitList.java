package org.jboss.resteasy.cdi.generic;

import java.util.ArrayList;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 14, 2012
 */
public class VisitList
{
   static final public String CONCRETE_DECORATOR_ENTER = "concreteDecoratorEnter";
   static final public String CONCRETE_DECORATOR_LEAVE = "concreteDecoratorLeave";
   static final public String UPPER_BOUND_DECORATOR_ENTER = "upperBoundDecoratorEnter";
   static final public String UPPER_BOUND_DECORATOR_LEAVE = "upperBoundDecoratorLeave";
   static final public String LOWER_BOUND_DECORATOR_ENTER = "lowerBoundDecoratorEnter";
   static final public String LOWER_BOUND_DECORATOR_LEAVE = "lowerBoundDecoratorLeave";
   
   static private ArrayList<String> visitList = new ArrayList<String>();
   
   static public void add(String o)
   {
      visitList.add(o);
   }
   
   static public ArrayList<String> getList()
   {
      return new ArrayList<String>(visitList);
   }
   
   static public void clear()
   {
      visitList.clear();
   }
}

