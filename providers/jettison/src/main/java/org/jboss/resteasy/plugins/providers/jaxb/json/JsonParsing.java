package org.jboss.resteasy.plugins.providers.jaxb.json;

import org.jboss.resteasy.plugins.providers.jaxb.JAXBUnmarshalException;
import org.jboss.resteasy.plugins.providers.jaxb.json.i18n.Messages;

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

      int i = reader.read();
      char c = (char) i;
      StringBuffer buffer = new StringBuffer();
      if (c != '{') throw new JAXBUnmarshalException(Messages.MESSAGES.expectingLeftBraceJsonMap());
      
      buffer.append(c);
      do
      {
         i = reader.read();
         if (i == -1) throw new JAXBUnmarshalException(Messages.MESSAGES.unexpectedEndOfStream());
         c = (char) i;
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
      } while (openBrace > 0);
      return buffer.toString();
   }

   public static String getJsonString(Reader reader) throws IOException
   {
      boolean quote = true;
      boolean backslash = false;

      int i = reader.read();
      char c = (char) i;
      StringBuffer buffer = new StringBuffer();
      if (c != '"') throw new JAXBUnmarshalException(Messages.MESSAGES.expectingQuote());

      do
      {
         i = reader.read();
         if (i == -1) throw new JAXBUnmarshalException(Messages.MESSAGES.unexpectedEndOfStream());
         c = (char) i;
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
      } while (quote);
      return buffer.toString();
   }

   protected static char eatWhitspace(Reader buffer, boolean reset)
           throws IOException
   {
      int i;
      char c;
      do
      {
         buffer.mark(2);
         i = buffer.read();
         if (i == -1) throw new JAXBUnmarshalException(Messages.MESSAGES.unexpectedEndOfJsonInput());
         c = (char) i;
      } while (Character.isWhitespace(c));
      if (reset) buffer.reset();
      return c;
   }
}
