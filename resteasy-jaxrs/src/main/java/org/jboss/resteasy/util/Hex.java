package org.jboss.resteasy.util;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

public class Hex
{

   /**
    * Used building output as Hex
    */
   private static final char[] DIGITS = {
           '0', '1', '2', '3', '4', '5', '6', '7',
           '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
   };

   /**
    * Converts an array of characters representing hexidecimal values into an
    * array of bytes of those same values. The returned array will be half the
    * length of the passed array, as it takes two characters to represent any
    * given byte. An exception is thrown if the passed char array has an odd
    * number of elements.
    *
    * @param str An array of characters containing hexidecimal digits
    * @return A byte array containing binary data decoded from
    *         the supplied char array.
    * @throws RuntimeException Thrown if an odd number or illegal of characters
    *                          is supplied
    */
   public static byte[] decodeHex(String str)
   {
      char[] data = str.toCharArray();
      int len = data.length;

      if ((len & 0x01) != 0)
      {
         throw new RuntimeException(Messages.MESSAGES.oddNumberOfCharacters());
      }

      byte[] out = new byte[len >> 1];

      // two characters form the hex value.
      for (int i = 0, j = 0; j < len; i++)
      {
         int f = toDigit(data[j], j) << 4;
         j++;
         f = f | toDigit(data[j], j);
         j++;
         out[i] = (byte) (f & 0xFF);
      }

      return out;
   }

   /**
    * Converts a hexadecimal character to an integer.
    *
    * @param ch    A character to convert to an integer digit
    * @param index The index of the character in the source
    * @return An integer
    */
   protected static int toDigit(char ch, int index)
   {
      int digit = Character.digit(ch, 16);
      if (digit == -1)
      {
         throw new RuntimeException(Messages.MESSAGES.illegalHexadecimalCharacter(ch, index));
      }
      return digit;
   }

   /**
    * Converts an array of bytes into an array of characters representing the hexidecimal values of each byte in order.
    * The returned array will be double the length of the passed array, as it takes two characters to represent any
    * given byte.
    *
    * @param data a byte[] to convert to Hex characters
    * @return A char[] containing hexidecimal characters
    */
   public static String encodeHex(byte[] data)
   {

      int l = data.length;

      char[] out = new char[l << 1];

      // two characters form the hex value.
      for (int i = 0, j = 0; i < l; i++)
      {
         out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
         out[j++] = DIGITS[0x0F & data[i]];
      }
      return new String(out);
   }

}