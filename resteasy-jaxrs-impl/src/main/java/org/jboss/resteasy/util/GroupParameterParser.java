package org.jboss.resteasy.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple parser intended to parse sequences of name/value pairs.
 * Parameter values are exptected to be enclosed in quotes if they
 * contain unsafe characters, such as '=' characters or separators.
 * Parameter values are optional and can be omitted.
 * <p>
 * <code>param1 = value; param2 = "anything goes; really"; param3</code>
 * </p>
 *
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 */

public class GroupParameterParser
{
   /**
    * String to be parsed.
    */
   private char[] chars = null;

   /**
    * Current position in the string.
    */
   private int pos = 0;

   /**
    * Maximum position in the string.
    */
   private int len = 0;

   /**
    * Start of a token.
    */
   private int i1 = 0;

   /**
    * End of a token.
    */
   private int i2 = 0;

   /**
    * Whether names stored in the map should be converted to lower case.
    */
   private boolean lowerCaseNames = false;

   /**
    * Default ParameterParser constructor.
    */
   public GroupParameterParser()
   {
      super();
   }

   /**
    * Are there any characters left to parse?
    *
    * @return <tt>true</tt> if there are unparsed characters,
    *         <tt>false</tt> otherwise.
    */
   private boolean hasChar()
   {
      return this.pos < this.len;
   }

   /**
    * A helper method to process the parsed token. This method removes
    * leading and trailing blanks as well as enclosing quotation marks,
    * when necessary.
    *
    * @param quoted <tt>true</tt> if quotation marks are expected,
    *               <tt>false</tt> otherwise.
    * @return the token
    */
   private String getToken(boolean quoted)
   {
      // Trim leading white spaces
      while ((i1 < i2) && (Character.isWhitespace(chars[i1])))
      {
         i1++;
      }
      // Trim trailing white spaces
      while ((i2 > i1) && (Character.isWhitespace(chars[i2 - 1])))
      {
         i2--;
      }
      // Strip away quotation marks if necessary
      if (quoted)
      {
         if (((i2 - i1) >= 2)
                 && (chars[i1] == '"')
                 && (chars[i2 - 1] == '"'))
         {
            i1++;
            i2--;
         }
      }
      String result = null;
      if (i2 > i1)
      {
         result = new String(chars, i1, i2 - i1);
      }
      return result;
   }

   /**
    * Tests if the given character is present in the array of characters.
    *
    * @param ch      the character to test for presense in the array of characters
    * @param charray the array of characters to test against
    * @return <tt>true</tt> if the character is present in the array of
    *         characters, <tt>false</tt> otherwise.
    */
   private boolean isOneOf(char ch, final char[] charray)
   {
      boolean result = false;
      for (int i = 0; i < charray.length; i++)
      {
         if (ch == charray[i])
         {
            result = true;
            break;
         }
      }
      return result;
   }

   /**
    * Parses out a token until any of the given terminators
    * is encountered.
    *
    * @param terminators the array of terminating characters. Any of these
    *                    characters when encountered signify the end of the token
    * @return the token
    */
   private String parseToken(final char[] terminators)
   {
      char ch;
      i1 = pos;
      i2 = pos;
      while (hasChar())
      {
         ch = chars[pos];
         if (isOneOf(ch, terminators))
         {
            break;
         }
         i2++;
         pos++;
      }
      return getToken(false);
   }

   /**
    * Parses out a token until any of the given terminators
    * is encountered outside the quotation marks.
    *
    * @param terminators the array of terminating characters. Any of these
    *                    characters when encountered outside the quotation marks signify the end
    *                    of the token
    * @return the token
    */
   private String parseQuotedToken(final char[] terminators)
   {
      char ch;
      i1 = pos;
      i2 = pos;
      boolean quoted = false;
      boolean charEscaped = false;
      while (hasChar())
      {
         ch = chars[pos];
         if (!quoted && isOneOf(ch, terminators))
         {
            break;
         }
         if (!charEscaped && ch == '"')
         {
            quoted = !quoted;
         }
         charEscaped = (!charEscaped && ch == '\\');
         i2++;
         pos++;

      }
      return getToken(true);
   }

   /**
    * Returns <tt>true</tt> if parameter names are to be converted to lower
    * case when name/value pairs are parsed.
    *
    * @return <tt>true</tt> if parameter names are to be
    *         converted to lower case when name/value pairs are parsed.
    *         Otherwise returns <tt>false</tt>
    */
   public boolean isLowerCaseNames()
   {
      return this.lowerCaseNames;
   }

   /**
    * Sets the flag if parameter names are to be converted to lower case when
    * name/value pairs are parsed.
    *
    * @param b <tt>true</tt> if parameter names are to be
    *          converted to lower case when name/value pairs are parsed.
    *          <tt>false</tt> otherwise.
    */
   public void setLowerCaseNames(boolean b)
   {
      this.lowerCaseNames = b;
   }

   /**
    * Extracts a map of name/value pairs from the given string. Names are
    * expected to be unique.
    *
    * @param str       the string that contains a sequence of name/value pairs
    * @param separator the name/value pairs separator
    * @param groupSeparator group separator
    * @return a map of name/value pairs
    */
   public List<Map<String, String>> parse(final String str, char separator, char groupSeparator)
   {
      if (str == null)
      {
         return new ArrayList<Map<String, String>>();
      }
      return parse(str.toCharArray(), separator, groupSeparator);
   }

   /**
    * Extracts a map of name/value pairs from the given array of
    * characters. Names are expected to be unique.
    *
    * @param chars     the array of characters that contains a sequence of
    *                  name/value pairs
    * @param separator the name/value pairs separator
    * @param groupSeparator group separator
    * @return a map of name/value pairs
    */
   public List<Map<String, String>> parse(final char[] chars, char separator, char groupSeparator)
   {
      if (chars == null)
      {
         return new ArrayList<Map<String, String>>();
      }
      return parse(chars, 0, chars.length, separator, groupSeparator);
   }

   /**
    * Extracts a map of name/value pairs from the given array of
    * characters. Names are expected to be unique.
    *
    * @param chars     the array of characters that contains a sequence of
    *                  name/value pairs
    * @param offset    - the initial offset.
    * @param length    - the length.
    * @param separator the name/value pairs separator
    * @param groupSeparator group separator
    * @return a map of name/value pairs
    */
   public List<Map<String, String>> parse(
           final char[] chars,
           int offset,
           int length,
           char separator,
           char groupSeparator)
   {

      if (chars == null)
      {
         return new ArrayList<Map<String, String>>();
      }
      List<Map<String, String>> groups = new ArrayList<Map<String, String>>();
      HashMap<String, String> params = new HashMap<String, String>();
      groups.add(params);
      this.chars = chars;
      this.pos = offset;
      this.len = length;

      String paramName = null;
      String paramValue = null;
      while (hasChar())
      {
         paramName = parseToken(new char[]{
                 '=', separator, groupSeparator});
         paramValue = null;
         if (hasChar() && (chars[pos] == '='))
         {
            pos++; // skip '='
            paramValue = parseQuotedToken(new char[]{
                    separator, groupSeparator});
         }
         if ((paramName != null) && (paramName.length() > 0))
         {
            if (this.lowerCaseNames)
            {
               paramName = paramName.toLowerCase();
            }
            params.put(paramName, paramValue);
         }
         if (hasChar() && (chars[pos] == separator))
         {
            pos++; // skip separator
         }
         else if (hasChar() && (chars[pos] == groupSeparator))
         {
            pos++; // skip separator
            params = new HashMap<String, String>();
            groups.add(params);

         }
      }
      return groups;
   }
}
