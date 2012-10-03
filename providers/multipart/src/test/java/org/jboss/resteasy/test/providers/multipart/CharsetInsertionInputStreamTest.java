package org.jboss.resteasy.test.providers.multipart;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.jboss.resteasy.plugins.providers.multipart.CharsetInsertionInputStream;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Test;

/**
 * RESTEASY-723
 * 
 * @author Attila Kiraly
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 */
public class CharsetInsertionInputStreamTest extends
      BaseResourceTest
{
   protected static final String TEST_URI = TestPortProvider.generateURL("");
   protected static final String UTF_16 = "UTF-16";
   protected static final String TEXT_PLAIN = "text/plain";
   protected static final String TEXT_HTTP = "text/http";
   protected static final String TEXT_PLAIN_WITH_CHARSET_US_ASCII = normalize("text/plain; charset=US-ASCII");
   protected static final String TEXT_PLAIN_WITH_CHARSET_UTF_16 = normalize("text/plain; charset=UTF-16");
   protected static final String TEXT_HTTP_WITH_CHARSET_US_ASCII = normalize("text/http; charset=US-ASCII");
   protected static final String TEXT_HTTP_WITH_CHARSET_UTF_16 = normalize("text/http; charset=UTF-16");
   protected static final String TEXT_HTTP_WITH_CHARSET_ISO_8859_1 = normalize("text/http; charset=ISO-8859-1");
   protected static final byte[] abc_us_ascii = new byte[] { 0x61, 0x62, 0x63 };
   protected static final byte[] abc_utf16 = new byte[] { 00, 0x61, 00, 0x62, 00, 0x63 };
   
   @Test
   public void test_TEXT_PLAIN_WITH_CHARSET_US_ASCII() throws Exception
   {
      String message = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n\r\n" // empty header: should add default content-type
            + "abc0\r\n"
            + "--boo\r\n"     // missing content-type: should add default content-type
            + "x: 8\r\n\r\n"
            + "abc1\r\n"
            + "--boo\r\n"     // should leave content-type unchanged
            + "Content-Type: " + TEXT_HTTP_WITH_CHARSET_ISO_8859_1 + "\r\n\r\n"
            + "abc2\r\n"
            + "--boo\r\n"     // should add default charset
            + "Content-Type: " + TEXT_HTTP + "\r\n\r\n"
            + "abc3\r\n"
            + "--boo--\r\n";
      String expected = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "Content-Type: " + TEXT_PLAIN_WITH_CHARSET_US_ASCII + "\r\n\r\n"
            + "abc0\r\n"
            + "--boo\r\n"
            + "x: 8\r\n"
            + "Content-Type: " + TEXT_PLAIN_WITH_CHARSET_US_ASCII + "\r\n\r\n"
            + "abc1\r\n"
            + "--boo\r\n"
            + "Content-Type: " + TEXT_HTTP_WITH_CHARSET_ISO_8859_1 + "\r\n\r\n"
            + "abc2\r\n"
            + "--boo\r\n"
            + "Content-Type: " + TEXT_HTTP_WITH_CHARSET_US_ASCII + "\r\n\r\n"
            + "abc3\r\n"
            + "--boo--\r\n";
      InputStream is = new CharsetInsertionInputStream(new ByteArrayInputStream(message.getBytes()), MediaType.valueOf(TEXT_PLAIN_WITH_CHARSET_US_ASCII));
      StringBuffer sb = new StringBuffer();
      int c = is.read();
      while (c != -1)
      {
//         System.out.println(c + ": " + (char) c);
         sb.append((char)c);
         c = is.read();
      }
      Assert.assertEquals(expected, sb.toString());
   }
   
   @Test
   public void test_TEXT_HTTP_WITH_CHARSET_UTF_16() throws Exception
   {
      String part1 = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n\r\n"; // empty header: should add preprocessor's default content-type
      String part2 = ""
            + "--boo\r\n"     // missing content-type: should add preprocessor's default content-type
            + "x: 8\r\n\r\n";
      String part3 = ""
            + "--boo\r\n"     // should leave content-type unchanged
            + "Content-Type: " + TEXT_HTTP_WITH_CHARSET_ISO_8859_1 + "\r\n\r\n";
      String part4 = ""
            + "--boo\r\n"     // should add preprocessor's default charset
            + "Content-Type: " + TEXT_PLAIN + "\r\n\r\n";
      String part5 = ""
            + "--boo--\r\n";
      int pos1 = part1.length();
      int pos2 = pos1 + 6;
      int pos3 = pos2 + part2.length();
      int pos4 = pos3 + 6;
      int pos5 = pos4 + part3.length();
      int pos6 = pos5 + 6;
      int pos7 = pos6 + part4.length();
      int pos8 = pos7 + 6;
      byte[] message = new byte[part1.length() + part2.length() + part3.length() + part4.length() + part5.length() + 4 * 6];
      System.arraycopy(part1.getBytes(), 0, message, 0, part1.getBytes().length);
      System.arraycopy(abc_utf16,        0, message, pos1, 6);
      System.arraycopy(part2.getBytes(), 0, message, pos2, part2.getBytes().length);
      System.arraycopy(abc_utf16,        0, message, pos3, 6);
      System.arraycopy(part3.getBytes(), 0, message, pos4, part3.getBytes().length);
      System.arraycopy(abc_utf16,        0, message, pos5, 6);
      System.arraycopy(part3.getBytes(), 0, message, pos4, part3.getBytes().length);
      System.arraycopy(abc_utf16,        0, message, pos5, 6);
      System.arraycopy(part4.getBytes(), 0, message, pos6, part4.getBytes().length);
      System.arraycopy(abc_utf16,        0, message, pos7, 6);
      System.arraycopy(part5.getBytes(), 0, message, pos8, part5.getBytes().length);
      
      String epart1 = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "Content-Type: " + TEXT_HTTP_WITH_CHARSET_UTF_16 + "\r\n\r\n";
      String epart2 = ""
            + "--boo\r\n"
            + "x: 8\r\n"
            + "Content-Type: " + TEXT_HTTP_WITH_CHARSET_UTF_16 + "\r\n\r\n";
      String epart3 = ""
            + "--boo\r\n"
            + "Content-Type: " + TEXT_HTTP_WITH_CHARSET_ISO_8859_1 + "\r\n\r\n";
      String epart4 = ""
            + "--boo\r\n"
            + "Content-Type: " + TEXT_PLAIN_WITH_CHARSET_UTF_16 + "\r\n\r\n";
      String epart5 = ""
            + "--boo--\r\n";
      int epos1 = epart1.length();
      int epos2 = epos1 + 6;
      int epos3 = epos2 + epart2.length();
      int epos4 = epos3 + 6;
      int epos5 = epos4 + epart3.length();
      int epos6 = epos5 + 6;
      int epos7 = epos6 + epart4.length();
      int epos8 = epos7 + 6;
      byte[] expected = new byte[epart1.length() + epart2.length() + epart3.length() + epart4.length() + epart5.length() + 4 * 6];
      System.arraycopy(epart1.getBytes(), 0, expected, 0,     epart1.getBytes().length);
      System.arraycopy(abc_utf16,         0, expected, epos1, 6);
      System.arraycopy(epart2.getBytes(), 0, expected, epos2, epart2.getBytes().length);
      System.arraycopy(abc_utf16,         0, expected, epos3, 6);
      System.arraycopy(epart3.getBytes(), 0, expected, epos4, epart3.getBytes().length);
      System.arraycopy(abc_utf16,         0, expected, epos5, 6);
      System.arraycopy(epart4.getBytes(), 0, expected, epos6, epart4.getBytes().length);
      System.arraycopy(abc_utf16,         0, expected, epos7, 6);
      System.arraycopy(epart5.getBytes(), 0, expected, epos8, epart5.getBytes().length);

      InputStream is = new CharsetInsertionInputStream(new ByteArrayInputStream(message), MediaType.valueOf(TEXT_HTTP_WITH_CHARSET_UTF_16));
      StringBuffer sb = new StringBuffer();
      int c = is.read();
      while (c != -1)
      {
//         System.out.println(c + ": " + (char) c);
         sb.append((char)c);
         c = is.read();
      }
      byte[] result = sb.toString().getBytes();
      for (int i = 0; i < expected.length; i++)
      {
         Assert.assertEquals("error at byte " + i, expected[i], result[i]);
      }
   }
   
   @Test
   public void testSpacesAndTabs() throws Exception
   {
      String message = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + " \t\t Content-Type \t\t : \t\t text/plain; \t\t charset=us-ascii \t\t \r\n\r\n"
            + "abc\r\n"
            + "--boo--\r\n";
      String expected = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + " \t\t Content-Type \t\t : \t\t text/plain; \t\t charset=us-ascii \t\t \r\n\r\n"
            + "abc\r\n"
            + "--boo--\r\n";
      InputStream is = new CharsetInsertionInputStream(new ByteArrayInputStream(message.getBytes()), MediaType.valueOf(TEXT_PLAIN_WITH_CHARSET_US_ASCII));
      StringBuffer sb = new StringBuffer();
      int c = is.read();
      while (c != -1)
      {
//         System.out.println(c + ": " + (char) c);
         sb.append((char)c);
         c = is.read();
      }
      Assert.assertEquals(expected, sb.toString());
   }
   
   @Test
   public void testNoSpaces() throws Exception
   {
      String message = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "Content-Type:text/plain;charset=us-ascii\r\n\r\n"
            + "abc\r\n"
            + "--boo--\r\n";
      String expected = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "Content-Type:text/plain;charset=us-ascii\r\n\r\n"
            + "abc\r\n"
            + "--boo--\r\n";
      InputStream is = new CharsetInsertionInputStream(new ByteArrayInputStream(message.getBytes()), MediaType.valueOf(TEXT_PLAIN_WITH_CHARSET_US_ASCII));
      StringBuffer sb = new StringBuffer();
      int c = is.read();
      while (c != -1)
      {
//         System.out.println(c + ": " + (char) c);
         sb.append((char)c);
         c = is.read();
      }
      Assert.assertEquals(expected, sb.toString());
   }
   
   @Test
   public void testComplicatedContent() throws Exception
   {
      String message = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "Content-Type: text/plain; charset=us-ascii\r\n\r\n"
            + "abc\ndef\r\nghiContent-Type\r\n\r\ncharset\t xyz"
            + "--boo--\r\n";
      String expected = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "Content-Type: text/plain; charset=us-ascii\r\n\r\n"
            + "abc\ndef\r\nghiContent-Type\r\n\r\ncharset\t xyz"
            + "--boo--\r\n";
      InputStream is = new CharsetInsertionInputStream(new ByteArrayInputStream(message.getBytes()), MediaType.valueOf(TEXT_PLAIN_WITH_CHARSET_US_ASCII));
      StringBuffer sb = new StringBuffer();
      int c = is.read();
      while (c != -1)
      {
//         System.out.println(c + ": " + (char) c);
         sb.append((char)c);
         c = is.read();
      }
      Assert.assertEquals(expected, sb.toString());
   }
   
   @Test
   public void testContentTypeMissingChars() throws Exception
   {
      String message = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "A: b\r\n"
            + "Content-Typ: " + TEXT_HTTP_WITH_CHARSET_ISO_8859_1 + "\r\n\r\n"
            + "abc\r\n"
            + "--boo\r\n"
            + "ontent-Type: " + TEXT_HTTP_WITH_CHARSET_ISO_8859_1 + "\r\n"
            + "A: b\r\n\r\n"
            + "abc\r\n"
            + "--boo--\r\n";
      String expected = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "A: b\r\n"
            + "Content-Typ: " + TEXT_HTTP_WITH_CHARSET_ISO_8859_1 + "\r\n"
            + "Content-Type: " + TEXT_PLAIN_WITH_CHARSET_US_ASCII + "\r\n\r\n"
            + "abc\r\n"
            + "--boo\r\n"
            + "ontent-Type: " + TEXT_HTTP_WITH_CHARSET_ISO_8859_1 + "\r\n"
            + "A: b\r\n"
            + "Content-Type: " + TEXT_PLAIN_WITH_CHARSET_US_ASCII + "\r\n\r\n"
            + "abc\r\n"
            + "--boo--\r\n";
      InputStream is = new CharsetInsertionInputStream(new ByteArrayInputStream(message.getBytes()), MediaType.valueOf(TEXT_PLAIN_WITH_CHARSET_US_ASCII));
      StringBuffer sb = new StringBuffer();
      int c = is.read();
      while (c != -1)
      {
//         System.out.println(c + ": " + (char) c);
         sb.append((char)c);
         c = is.read();
      }
      Assert.assertEquals(expected, sb.toString());
   }
   
   @Test
   public void testContentTypeExtraChars() throws Exception
   {
      String message = ""
            + "Content-Type: multipart/form-data;boundary= \t\t \"boo\" \t\t \r\n\r\n"
            + "--boo\r\n"
            + "A: b\r\n"
            + "Content-Typex: " + TEXT_HTTP_WITH_CHARSET_ISO_8859_1 + "\r\n\r\n"
            + "abc\r\n"
            + "--boo\r\n"
            + "xContent-Type: " + TEXT_HTTP_WITH_CHARSET_ISO_8859_1 + "\r\n"
            + "A: b\r\n\r\n"
            + "abc\r\n"
            + "--boo--\r\n";
      String expected = ""
            + "Content-Type: multipart/form-data;boundary= \t\t \"boo\" \t\t \r\n\r\n"
            + "--boo\r\n"
            + "A: b\r\n"
            + "Content-Typex: " + TEXT_HTTP_WITH_CHARSET_ISO_8859_1 + "\r\n"
            + "Content-Type: " + TEXT_PLAIN_WITH_CHARSET_US_ASCII + "\r\n\r\n"
            + "abc\r\n"
            + "--boo\r\n"
            + "xContent-Type: " + TEXT_HTTP_WITH_CHARSET_ISO_8859_1 + "\r\n"
            + "A: b\r\n"
            + "Content-Type: " + TEXT_PLAIN_WITH_CHARSET_US_ASCII + "\r\n\r\n"
            + "abc\r\n"
            + "--boo--\r\n";
      InputStream is = new CharsetInsertionInputStream(new ByteArrayInputStream(message.getBytes()), MediaType.valueOf(TEXT_PLAIN_WITH_CHARSET_US_ASCII));
      StringBuffer sb = new StringBuffer();
      int c = is.read();
      while (c != -1)
      {
//         System.out.println(c + ": " + (char) c);
         sb.append((char)c);
         c = is.read();
      }
      Assert.assertEquals(expected, sb.toString());
   }
   
   @Test
   public void testCharsetMissingChars() throws Exception
   {
      String message = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "A: b\r\n"
            + "Content-Type: text/plain; charse=xyz\r\n\r\n"
            + "abc\r\n"
            + "--boo\r\n"
            + "Content-Type: text/plain; harset=xyz\r\n"
            + "A: b\r\n\r\n"
            + "abc\r\n"
            + "--boo--\r\n";
      String expected = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "A: b\r\n"
            + "Content-Type: text/plain; charse=xyz;charset=us-ascii\r\n\r\n"
            + "abc\r\n"
            + "--boo\r\n"
            + "Content-Type: text/plain; harset=xyz;charset=us-ascii\r\n"
            + "A: b\r\n\r\n"
            + "abc\r\n"
            + "--boo--\r\n";
      InputStream is = new CharsetInsertionInputStream(new ByteArrayInputStream(message.getBytes()), MediaType.valueOf(TEXT_PLAIN_WITH_CHARSET_US_ASCII));
      StringBuffer sb = new StringBuffer();
      int c = is.read();
      while (c != -1)
      {
//         System.out.println(c + ": " + (char) c);
         sb.append((char)c);
         c = is.read();
      }
      Assert.assertEquals(expected, sb.toString());
   }
   
   @Test
   public void testCharsetExtraChars() throws Exception
   {
      String message = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "A: b\r\n"
            + "Content-Type: text/plain; charsetx=xyz\r\n\r\n"
            + "abc\r\n"
            + "--boo\r\n"
            + "Content-Type: text/plain; xcharset=xyz\r\n"
            + "A: b\r\n\r\n"
            + "abc\r\n"
            + "--boo--\r\n";
      String expected = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "A: b\r\n"
            + "Content-Type: text/plain; charsetx=xyz;charset=us-ascii\r\n\r\n"
            + "abc\r\n"
            + "--boo\r\n"
            + "Content-Type: text/plain; xcharset=xyz;charset=us-ascii\r\n"
            + "A: b\r\n\r\n"
            + "abc\r\n"
            + "--boo--\r\n";
      InputStream is = new CharsetInsertionInputStream(new ByteArrayInputStream(message.getBytes()), MediaType.valueOf(TEXT_PLAIN_WITH_CHARSET_US_ASCII));
      StringBuffer sb = new StringBuffer();
      int c = is.read();
      while (c != -1)
      {
//         System.out.println(c + ": " + (char) c);
         sb.append((char)c);
         c = is.read();
      }
      Assert.assertEquals(expected, sb.toString());
   }
   
   @Test
   public void testMissingColon() throws Exception
   {
      String message = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "Content-Type text/plain; charset=us-ascii\r\n\r\n"
            + "abc\r\n"
            + "--boo--\r\n";
      String expected = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "Content-Type text/plain; charset=us-ascii\r\n"
            + "Content-Type: " + TEXT_PLAIN_WITH_CHARSET_US_ASCII + "\r\n\r\n"
            + "abc\r\n"
            + "--boo--\r\n";
      InputStream is = new CharsetInsertionInputStream(new ByteArrayInputStream(message.getBytes()), MediaType.valueOf(TEXT_PLAIN_WITH_CHARSET_US_ASCII));
      StringBuffer sb = new StringBuffer();
      int c = is.read();
      while (c != -1)
      {
//         System.out.println(c + ": " + (char) c);
         sb.append((char)c);
         c = is.read();
      }
      Assert.assertEquals(expected, sb.toString());
   }
   
   @Test
   public void testMissingSemicolon() throws Exception
   {
      String message = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "Content-Type: text/plain charset=us-ascii\r\n\r\n"
            + "abc\r\n"
            + "--boo--\r\n";
      String expected = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "Content-Type: text/plain charset=us-ascii;charset=us-ascii\r\n\r\n"
            + "abc\r\n"
            + "--boo--\r\n";
      InputStream is = new CharsetInsertionInputStream(new ByteArrayInputStream(message.getBytes()), MediaType.valueOf(TEXT_PLAIN_WITH_CHARSET_US_ASCII));
      StringBuffer sb = new StringBuffer();
      int c = is.read();
      while (c != -1)
      {
//         System.out.println(c + ": " + (char) c);
         sb.append((char)c);
         c = is.read();
      }
      Assert.assertEquals(expected, sb.toString());
   }
   
   @Test
   public void testIncorrectEol() throws Exception
   {
      String message = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "Content-Type: text/plain charset=us-ascii\r\n\r"
            + "abc\r\n"
            + "--boo--\r\n";
      String expected = ""
            + "Content-Type: multipart/form-data;boundary=\"boo\"\r\n\r\n"
            + "--boo\r\n"
            + "Content-Type: text/plain charset=us-ascii;charset=us-ascii\r\n\r"
            + "abc\r\n"
            + "--boo--\r\n";
      InputStream is = new CharsetInsertionInputStream(new ByteArrayInputStream(message.getBytes()), MediaType.valueOf(TEXT_PLAIN_WITH_CHARSET_US_ASCII));
      StringBuffer sb = new StringBuffer();
      int c = is.read();
      while (c != -1)
      {
//         System.out.println(c + ": " + (char) c);
         sb.append((char)c);
         c = is.read();
      }
      Assert.assertEquals(expected, sb.toString());
   }
   
   @Test
   public void testBoundaryErrorExtraChars() throws Exception
   {
      String message = ""
            + "Content-Type: multipart/form-data;boundary=\"bo\"\r\n\r\n"
            + "--boo\r\n\r\n"
            + "abc0\r\n"
            + "--boo\r\n"
            + "x: 8\r\n\r\n"
            + "abc1\r\n"
            + "--boo\r\n"
            + "Content-Type: " + TEXT_HTTP_WITH_CHARSET_ISO_8859_1 + "\r\n\r\n"
            + "abc2\r\n"
            + "--boo\r\n"
            + "Content-Type: " + TEXT_HTTP + "\r\n\r\n"
            + "abc3\r\n"
            + "--boo--\r\n";
      InputStream is = new CharsetInsertionInputStream(new ByteArrayInputStream(message.getBytes()), MediaType.valueOf(TEXT_PLAIN_WITH_CHARSET_US_ASCII));
      StringBuffer sb = new StringBuffer();
      int c = is.read();
      while (c != -1)
      {
//         System.out.println(c + ": " + (char) c);
         sb.append((char)c);
         c = is.read();
      }
      Assert.assertEquals(message, sb.toString()); // No change because of boundary error.
   }
   
   @Test
   public void testBoundaryErrorMissingChars() throws Exception
   {
      String message = ""
            + "Content-Type: multipart/form-data;boundary=\"booo\"\r\n\r\n"
            + "--boo\r\n\r\n"
            + "abc0\r\n"
            + "--boo\r\n"
            + "x: 8\r\n\r\n"
            + "abc1\r\n"
            + "--boo\r\n"
            + "Content-Type: " + TEXT_HTTP_WITH_CHARSET_ISO_8859_1 + "\r\n\r\n"
            + "abc2\r\n"
            + "--boo\r\n"
            + "Content-Type: " + TEXT_HTTP + "\r\n\r\n"
            + "abc3\r\n"
            + "--boo--\r\n";
      InputStream is = new CharsetInsertionInputStream(new ByteArrayInputStream(message.getBytes()), MediaType.valueOf(TEXT_PLAIN_WITH_CHARSET_US_ASCII));
      StringBuffer sb = new StringBuffer();
      int c = is.read();
      while (c != -1)
      {
//         System.out.println(c + ": " + (char) c);
         sb.append((char)c);
         c = is.read();
      }
      Assert.assertEquals(message, sb.toString()); // No change because of boundary error.
   }
   
   static private String normalize(String s)
   {
      String sl = s.toLowerCase();
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < s.length(); i++)
      {
         if (sl.charAt(i) != ' ' && sl.charAt(i) != '"')
         {
            sb.append(sl.charAt(i));
         }
      }
      return sb.toString();
   }
}
