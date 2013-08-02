package org.jboss.resteasy.plugins.providers;

import java.io.File;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FileRange
{
   private File file;

   public File getFile()
   {
      return file;
   }

   public long getBegin()
   {
      return begin;
   }

   public long getEnd()
   {
      return end;
   }

   private long begin;
   private long end;

   public FileRange(File file, long begin, long end)
   {
      this.file = file;
      this.begin = begin;
      this.end = end;
   }


}
