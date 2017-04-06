package org.jboss.resteasy.test.i18n;

import java.util.Locale;

import org.junit.Assert;

import org.jboss.resteasy.auth.oauth.i18n.Messages;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 31, 2015
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{
   protected static final String BASE = String.format("0%5s", Messages.BASE).substring(0, 3);

   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/auth/oauth/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         System.out.println(getClass() + ": " + filename + " not found.");
         return;
      }
      
      Assert.assertEquals(getExpected(BASE + "500", "accessToken"), Messages.MESSAGES.accessToken());
      Assert.assertEquals(getExpected(BASE + "510", "addingParameter", "x", "y"), Messages.MESSAGES.addingParameter("x", "y"));
      Assert.assertEquals(getExpected(BASE + "565", "errorHttpCode", "333", "wha?"), Messages.MESSAGES.errorHttpCode(333, "wha?"));
      Assert.assertEquals(getExpected(BASE + "590", "invalidTimestampLong", 1357), Messages.MESSAGES.invalidTimestampLong(1357));
      Assert.assertEquals(getExpected(BASE + "700", "wrongURIScope"), Messages.MESSAGES.wrongURIScope());   
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
