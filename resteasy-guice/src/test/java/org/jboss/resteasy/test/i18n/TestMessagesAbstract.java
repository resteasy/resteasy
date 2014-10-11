package org.jboss.resteasy.test.i18n;

import java.util.Locale;

import junit.framework.Assert;

import org.jboss.resteasy.guice.i18n.Messages;
import org.jboss.resteasy.test.resteasy_jaxrs.i18n.TestMessagesParent;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Oct 11, 2014
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{
   protected static final String BASE = String.format("00%4s", Messages.BASE).substring(0, 4);

   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/guice/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         System.out.println(getClass() + ": " + filename + " not found.");
         return;
      }
      
//      Assert.assertEquals(getExpected(BASE + "00", "foundModule", "module"), Messages.MESSAGES.foundModule("module"));
      Assert.assertEquals(getExpected(BASE + "05", "injectorStageNotProperlyDefined", "stage"), Messages.MESSAGES.injectorStageNotProperlyDefined("stage"));
      Assert.assertEquals(getExpected(BASE + "10", "registeringFactory", "factory"), Messages.MESSAGES.registeringFactory("factory"));
      Assert.assertEquals(getExpected(BASE + "15", "registeringProviderInstance", "provider"), Messages.MESSAGES.registeringProviderInstance("provider"));
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
