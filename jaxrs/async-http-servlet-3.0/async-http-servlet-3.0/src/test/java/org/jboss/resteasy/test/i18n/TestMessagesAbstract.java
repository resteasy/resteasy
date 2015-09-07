package org.jboss.resteasy.test.i18n;

import java.util.Locale;

import junit.framework.Assert;

import org.jboss.resteasy.plugins.server.servlet.i18n.Messages;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 25, 2015
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{  
   protected static final String BASE = String.format("00%4s", Messages.BASE).substring(0, 4);
   
   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/plugins/server/servlet/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         System.out.println(getClass() + ": " + filename + " not found.");
         return;
      }
      
      Assert.assertEquals(getExpected(BASE + "00", "alreadyCanceled"), Messages.MESSAGES.alreadyCanceled());
      Assert.assertEquals(getExpected(BASE + "20", "cancellingWith503"), Messages.MESSAGES.cancellingWith503());
      Assert.assertEquals(getExpected(BASE + "45", "schedulingTimeout"), Messages.MESSAGES.schedulingTimeout());
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
