package org.jboss.resteasy.test.i18n;

import java.util.Locale;

import junit.framework.Assert;

import org.jboss.resteasy.oath.i18n.Messages;
import org.jboss.resteasy.test.resteasy_jaxrs.i18n.TestMessagesParent;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Oct 9, 2014
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{
   protected static final String BASE = String.format("00%4s", Messages.BASE).substring(0, 3);

   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/oath/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         System.out.println(getClass() + ": " + filename + " not found.");
         return;
      }
      
      Assert.assertEquals(getExpected(BASE + "000", "accessToken"), Messages.MESSAGES.accessToken());
      Assert.assertEquals(getExpected(BASE + "010", "addingParameter", "x", "y"), Messages.MESSAGES.addingParameter("x", "y"));
      Assert.assertEquals(getExpected(BASE + "065", "errorHttpCode", "333", "wha?"), Messages.MESSAGES.errorHttpCode(333, "wha?"));
      Assert.assertEquals(getExpected(BASE + "090", "invalidTimestampLong", 1357), Messages.MESSAGES.invalidTimestampLong(1357));
      Assert.assertEquals(getExpected(BASE + "195", "wrongURIScope"), Messages.MESSAGES.wrongURIScope());   
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
