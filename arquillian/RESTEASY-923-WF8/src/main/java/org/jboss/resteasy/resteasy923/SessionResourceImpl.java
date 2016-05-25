package org.jboss.resteasy.resteasy923;

import javax.ejb.Stateless;


/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 26, 2014
 */
@Stateless
//@Path("test")
public class SessionResourceImpl implements SessionResourceLocal, SessionResourceRemote
{
   public String test(String param)
   {
      System.out.println("entering SessionResourceImpl.test()");
      return param;
   }
}
