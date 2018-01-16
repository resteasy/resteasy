package org.jboss.resteasy.util;

/**
 * A CharSequence backed by a char[] (no copy on creation)
 *
 */
public class ArrayCharSequence implements CharSequence
{
   private final char[] buf;
   private final int offset;
   private final int count;

   public ArrayCharSequence(char[] buff)
   {
      this(buff, 0, buff.length);
   }

   public ArrayCharSequence(char[] buff, int count)
   {
      this(buff, 0, count);
   }

   public ArrayCharSequence(char[] buff, int offset, int count)
   {
      this.buf = buff;
      this.offset = offset;
      this.count = count;
   }

   public char charAt(int index)
   {
      if (index < 0 || index >= count)
      {
         throw new StringIndexOutOfBoundsException(index);
      }
      return buf[offset + index];
   }

   public int length()
   {
      return count;
   }

   public CharSequence subSequence(int beginIndex, int endIndex)
   {
      if (beginIndex < 0)
      {
         throw new StringIndexOutOfBoundsException(beginIndex);
      }
      if (endIndex > count)
      {
         throw new StringIndexOutOfBoundsException(endIndex);
      }
      if (beginIndex > endIndex)
      {
         throw new StringIndexOutOfBoundsException(endIndex - beginIndex);
      }
      return ((beginIndex == 0) && (endIndex == count))
            ? this
            : new ArrayCharSequence(buf, offset + beginIndex, endIndex - beginIndex);
   }

   public String toString()
   {
      return new String(this.buf, this.offset, this.count);
   }
}