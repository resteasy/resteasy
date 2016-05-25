package org.jboss.resteasy.test.i18n;

import java.lang.reflect.TypeVariable;
import java.util.Locale;

import javax.ws.rs.core.MediaType;

import org.junit.Assert;

import org.jboss.resteasy.client.jaxrs.i18n.Messages;
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
   protected String BASE = String.format("00%4s", Messages.BASE).substring(0, 4);
   protected String BASE3 = BASE.substring(0, 3);
   protected TypeVariable<?> var;
   protected MediaType mediaType = new MediaType("xx", "yy", "zz");
   
   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/client/jaxrs/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         System.out.println(getClass() + ": " + filename + " not found.");
         return;
      }
      
      Assert.assertEquals(getExpected(BASE + "00", "canOnlySetOneLinkHeaderParam", "abc", "xyz"), Messages.MESSAGES.canOnlySetOneLinkHeaderParam("abc", "xyz"));
      Assert.assertEquals(getExpected(BASE + "20", "couldNotCreateURL", "xx", "yy", "zz"), Messages.MESSAGES.couldNotCreateURL("xx", "yy", "zz"));
      Assert.assertEquals(getExpected(BASE + "40", "doesNotSpecifyTypeParameter", var), Messages.MESSAGES.doesNotSpecifyTypeParameter(var));
      Assert.assertEquals(getExpected(BASE + "60", "failedToBufferAbortedResponseNoWriter", mediaType, "class"), Messages.MESSAGES.failedToBufferAbortedResponseNoWriter(mediaType, "class"));
      Assert.assertEquals(getExpected(BASE3 + "675", "verificationPathNotImplemented"), Messages.MESSAGES.verificationPathNotImplemented());
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
