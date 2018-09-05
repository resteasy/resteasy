package org.jboss.resteasy.test.i18n;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.jaxb.json.i18n.Messages;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.Locale;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 24, 2015
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{
   private static final Logger LOG = Logger.getLogger(TestMessagesAbstract.class);
   protected final String dir = "org/jboss/resteasy/providers/jaxb/json/i18n/Messages.i18n_";
   protected String BASE = String.format("00%4s", Messages.BASE).substring(0, 4);
   
   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/plugins/providers/jaxb/json/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         LOG.info(getClass() + ": " + filename + " not found.");
         return;
      }

      Assert.assertEquals(getExpected(BASE + "00", "expectingColonMap"), Messages.MESSAGES.expectingColonMap());
      Assert.assertEquals(getExpected(BASE + "30", "unableToFindJAXBContext", "xx/yy"), Messages.MESSAGES.unableToFindJAXBContext(new MediaType("xx", "yy")));
      Assert.assertEquals(getExpected(BASE + "40", "unexpectedEndOfStream"), Messages.MESSAGES.unexpectedEndOfStream());
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
