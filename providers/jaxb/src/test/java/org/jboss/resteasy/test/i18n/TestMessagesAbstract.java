package org.jboss.resteasy.test.i18n;

import java.util.Locale;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.jboss.resteasy.providers.jaxb.i18n.Messages;
import org.jboss.resteasy.test.resteasy_jaxrs.i18n.TestMessagesParent;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Sep 27, 2014
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{
   protected String BASE = String.format("00%4s", Messages.BASE).substring(0, 3);
   
   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/providers/jaxb/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         System.out.println(getClass() + ": " + filename + " not found.");
         return;
      }
      
      Assert.assertEquals(getExpected(BASE + "005", "collectionWrappingFailedNamespace", "xyz", "http://localhost"), Messages.MESSAGES.collectionWrappingFailedNamespace("xyz", "http://localhost"));
      Assert.assertEquals(getExpected(BASE + "010", "couldNotFindJAXBContextFinder", "xx/yy"), Messages.MESSAGES.couldNotFindJAXBContextFinder(new MediaType("xx", "yy")));
      Assert.assertEquals(getExpected(BASE + "100", "validXmlRegistryCouldNotBeLocated", getClass()), Messages.MESSAGES.validXmlRegistryCouldNotBeLocated());
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
