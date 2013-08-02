package se.unlogic.eagledns.zoneproviders.file;

import java.net.URL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathZoneProvider extends FileZoneProvider
{

   public void setZoneFilePath(String path)
   {
      URL url = Thread.currentThread().getContextClassLoader().getResource(path);
      if (url == null) throw new RuntimeException("Path not found: " + path);
      setZoneFileDirectory(url.getFile());
   }
}
