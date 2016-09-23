package se.unlogic.eagledns.zoneproviders.file;

import java.net.URL;

import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathZoneProvider extends FileZoneProvider
{
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of provider : se.unlogic.eagledns.zoneproviders.file.PathZoneProvider , method call : setZoneFilePath .")
   public void setZoneFilePath(String path)
   {
      URL url = Thread.currentThread().getContextClassLoader().getResource(path);
      if (url == null) throw new RuntimeException("Path not found: " + path);
      setZoneFileDirectory(url.getFile());
   }
}
