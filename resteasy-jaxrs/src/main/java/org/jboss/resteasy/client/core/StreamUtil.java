package org.jboss.resteasy.client.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public class StreamUtil
{
   public static byte[] getBytes(InputStream is, boolean closeIn) throws IOException
   {
      /*
      final ByteOutputStream bos = new ByteOutputStream();
      readInto(is, closeIn, bos, false, is.available());
      return bos.getBytes();
      */
      throw new RuntimeException("NOT IMPLEMENTED");
   }

   public static void readInto(InputStream is, boolean closeIn,
                               OutputStream os, boolean closeOut) throws IOException
   {
      readInto(is, closeIn, os, closeOut, 4 << 10);
   }

   private static void readInto(InputStream is, boolean closeIn, OutputStream os,
                                boolean closeOut, final int maxBufferSize) throws IOException
   {
      try
      {
         byte[] buf = new byte[Math.max(is.available(), maxBufferSize)];
         if (buf.length == 0)
         {
            return;
         }
         int read = 0;
         while ((read = is.read(buf, 0, Math.max(is.available(), buf.length))) != -1)
         {
            os.write(buf, 0, read);
         }
      }
      finally
      {
         if (is != null && closeIn)
            is.close();
         if (os != null && closeOut)
            os.close();
      }
   }
}
