package org.jboss.resteasy.test.i18n;

import java.lang.reflect.TypeVariable;
import java.util.Locale;

import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;
import org.junit.Assert;

import org.jboss.resteasy.rxjava2.i18n.Messages;
import org.junit.Test;

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
   protected String BASE = String.format("0%5s", Messages.BASE).substring(0, 4);
   protected String BASE3 = BASE.substring(0, 3);
   protected TypeVariable<?> var;
   protected MediaType mediaType = new MediaType("xx", "yy", "zz");
   
   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/rxjava2/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         LOG.info(getClass() + ": " + filename + " not found.");
         return;
      }
      
      Assert.assertEquals(getExpected(BASE + "00", "expectedClientInvocationBuilder", "abc"), Messages.MESSAGES.expectedClientInvocationBuilder("abc"));   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
