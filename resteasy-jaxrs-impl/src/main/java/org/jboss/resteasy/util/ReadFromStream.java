package org.jboss.resteasy.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ReadFromStream
{
   /**
    * Stuff the contents of a InputStream into a byte buffer.  Reads until EOF (-1).
    *
    * @param bufferSize buffer size
    * @param entityStream entity input stream
    * @return bytes read
    * @throws IOException if I/O error occurred
    */
   public static byte[] readFromStream(int bufferSize, InputStream entityStream)
           throws IOException
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      byte[] buffer = new byte[bufferSize];
      int wasRead = 0;
      do
      {
         wasRead = entityStream.read(buffer);
         if (wasRead > 0)
         {
            baos.write(buffer, 0, wasRead);
         }
      } while (wasRead > -1);
      return baos.toByteArray();
   }
}
