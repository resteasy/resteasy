package org.jboss.resteasy.resteasy1101;

import javax.ejb.Stateless;

import org.jboss.resteasy.spi.validation.ValidateRequest;


/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Oct 6, 2014
 */
@Stateless
//@Path("test")
@ValidateRequest
public class SessionResourceImpl implements SessionResourceLocal, SessionResourceRemote
{
   public String test(String param)
   {
      System.out.println("entering SessionResourceImpl.test()");
      return param;
   }
}
