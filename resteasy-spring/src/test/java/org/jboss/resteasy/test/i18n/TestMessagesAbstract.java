package org.jboss.resteasy.test.i18n;

import java.util.Locale;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.jboss.resteasy.plugins.spring.i18n.Messages;
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
   protected static final String BASE = String.format("0%5s", Messages.BASE).substring(0, 4);

   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/plugins/spring/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         System.out.println(getClass() + ": " + filename + " not found.");
         return;
      }
      
      Assert.assertEquals(getExpected(BASE + "00", "cannotUseScanParameters"), Messages.MESSAGES.cannotUseScanParameters());
      Assert.assertEquals(getExpected(BASE + "05", "couldNotConvertBeanToClass", "bean"), Messages.MESSAGES.couldNotConvertBeanToClass("bean"));
      Assert.assertEquals(getExpected(BASE + "10", "couldNotFindMessageBodyReader", getClass().getGenericSuperclass(), new MediaType("xx", "yy")), Messages.MESSAGES.couldNotFindMessageBodyReader(getClass().getGenericSuperclass(), new MediaType("xx", "yy")));
      Assert.assertEquals(getExpected(BASE + "60", "resteasyHandlerMappingHasDefaultOrder"), Messages.MESSAGES.resteasyHandlerMappingHasDefaultOrder());   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
