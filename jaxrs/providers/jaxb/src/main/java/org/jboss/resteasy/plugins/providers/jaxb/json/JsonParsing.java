package org.jboss.resteasy.plugins.providers.jaxb.json;

import org.jboss.resteasy.plugins.providers.jaxb.JAXBUnmarshalException;

import java.io.IOException;
import java.io.Reader;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JsonParsing
{
   public static String extractJsonMapString(Reader reader) throws IOException
   {
      int openBrace = 1;
      boolean quote = false;
      boolean backslash = false;

      char c = (char) reader.read();
      StringBuffer buffer = new StringBuffer();
      if (c != '{') throw new JAXBUnmarshalException("Expecting '{' in json map");

      buffer.append(c);
      do
      {
         c = (char) reader.read();
         if (c == -1) throw new JAXBUnmarshalException("Unexpected end of stream");
         buffer.append(c);
         if (backslash)
         {
            backslash = false;
         }
         else
         {
            switch (c)
            {
               case '"':
               {
                  quote = !quote;
                  break;
               }
               case '{':
               {
                  if (!quote) openBrace++;
                  break;
               }
               case '}':
               {
                  if (!quote) openBrace--;
                  break;
               }
               case '\\':
               {
                  backslash = true;
                  break;
               }
            }
         }
      } while (openBrace > 0 && c != -1);
      return buffer.toString();
   }

   public static String getJsonString(Reader reader) throws IOException
   {
      boolean quote = true;
      boolean backslash = false;

      char c = (char) reader.read();
      StringBuffer buffer = new StringBuffer();
      if (c != '"') throw new JAXBUnmarshalException("Expecting '\"' in json map key");

      do
      {
         c = (char) reader.read();
         if (c == -1) throw new JAXBUnmarshalException("Unexpected end of stream");
         if (backslash)
         {
            buffer.append(c);
            backslash = false;
         }
         else
         {
            switch (c)
            {
               case '"':
               {
                  quote = false;
                  break;
               }
               case '\\':
               {
                  backslash = true;
                  break;
               }
               default:
                  buffer.append(c);
                  break;

            }
         }
      } while (quote && c != -1);
      return buffer.toString();
   }

   protected static char eatWhitspace(Reader buffer, boolean reset)
           throws IOException
   {
      char c;
      do
      {
         buffer.mark(2);
         c = (char) buffer.read();
         if (c == -1) new JAXBUnmarshalException("Unexpected end of json input");
      } while (Character.isWhitespace(c));
      if (reset) buffer.reset();
      return c;
   }
}
