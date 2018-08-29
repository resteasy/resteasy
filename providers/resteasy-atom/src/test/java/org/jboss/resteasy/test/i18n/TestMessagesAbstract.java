package org.jboss.resteasy.test.i18n;

import java.util.Locale;

import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;
import org.junit.Assert;

import org.jboss.resteasy.plugins.providers.resteasy_atom.i18n.Messages;
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
   private static final Logger LOG = Logger.getLogger(TestMessagesAbstract.class);
   protected String BASE = String.format("00%4s", Messages.BASE).substring(0, 4);
   
   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/plugins/providers/resteasy_atom/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         LOG.info(getClass() + ": " + filename + " not found.");
         return;
      }
      
      Assert.assertEquals(getExpected(BASE + "00", "consructorMustBeCalled"), Messages.MESSAGES.consructorMustBeCalled());
      Assert.assertEquals(getExpected(BASE + "05", "unableToFindJAXBContext", "xx/yy"), Messages.MESSAGES.unableToFindJAXBContext(new MediaType("xx", "yy")));
      Assert.assertEquals(getExpected(BASE + "10", "unableToMarshal", "xx/yy"), Messages.MESSAGES.unableToMarshal(new MediaType("xx", "yy")));
      Assert.assertEquals(getExpected(BASE + "15", "unableToUnmarshal", "xx/yy"), Messages.MESSAGES.unableToUnmarshal(new MediaType("xx", "yy")));
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
