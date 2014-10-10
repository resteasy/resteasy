package org.jboss.resteasy.test.i18n;

import java.util.Locale;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import junit.framework.Assert;

import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.util.ByteSequence;
import org.jboss.resteasy.plugins.providers.multipart.AbstractMultipartWriter;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartReader;
import org.jboss.resteasy.plugins.providers.multipart.MultipartWriter;
import org.jboss.resteasy.providers.multipart.i18n.Messages;
import org.jboss.resteasy.test.resteasy_jaxrs.i18n.TestMessagesParent;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Oct 8, 2014
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{
   protected String BASE = String.format("00%4s", Messages.BASE).substring(0, 4);
   protected Field field = new Field()
   {
      @Override
      public String getName()
      {
         return "foo";
      }
      @Override
      public String getBody()
      {
         return "bar";
      }
      @Override
      public ByteSequence getRaw()
      {
         return null;
      }
   };
   protected MessageBodyReader<?> reader = new MultipartReader();
   protected AbstractMultipartWriter writer = new MultipartWriter();
   protected MultipartOutput multipartOutput = new MultipartOutput();
   
   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/providers/multipart/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         System.out.println(getClass() + ": " + filename + " not found.");
         return;
      }

      Assert.assertEquals(getExpected(BASE + "00", "couldFindNoContentDispositionHeader"), Messages.MESSAGES.couldFindNoContentDispositionHeader());
      Assert.assertEquals(getExpected(BASE + "05", "couldNotParseContentDisposition", field), Messages.MESSAGES.couldNotParseContentDisposition(field));
      Assert.assertEquals(getExpected(BASE + "20", "hadToWriteMultipartOutput", multipartOutput, writer, getClass()),
                          Messages.MESSAGES.hadToWriteMultipartOutput(multipartOutput, writer, getClass()));
      Assert.assertEquals(getExpected(BASE + "35", "receivedGenericType", reader, getClass().getGenericSuperclass(), getClass()),
                          Messages.MESSAGES.receivedGenericType(reader, getClass().getGenericSuperclass(), getClass()));
      Assert.assertEquals(getExpected(BASE + "55", "urlEncoderDoesNotSupportUtf8"), Messages.MESSAGES.urlEncoderDoesNotSupportUtf8());
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   abstract protected Locale getLocale();
}
