package org.jboss.resteasy.test.nextgen.security;

import org.jboss.resteasy.util.BasicAuthHelper;
import org.junit.Test;

import junit.framework.Assert;

/**
 * RESTEASY-1176
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date May 7, 2016
 */
public class PasswordColonTest
{
   @Test
   public void testPasswordWithColon()
   {
      String header = BasicAuthHelper.createHeader("user", "pass:word");
      String[] credentials = BasicAuthHelper.parseHeader(header);
      System.out.println("user: " + credentials[0]);
      System.out.println("password: " + credentials[1]);
      Assert.assertEquals("user", credentials[0]);
      Assert.assertEquals("pass:word", credentials[1]);
   }
}
