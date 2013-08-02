package org.jboss.resteasy.cdi.interceptors;

import java.util.ArrayList;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 21, 2012
 */
public class VisitList
{
   static private ArrayList<Object> visitList = new ArrayList<Object>();
   
   static public void add(Object interceptor)
   {
      visitList.add(interceptor);
   }
   
   static public ArrayList<Object> getList()
   {
      return new ArrayList<Object>(visitList);
   }
}

