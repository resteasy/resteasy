package org.jboss.resteasy.plugins.providers.atom;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MediaTypeAdapter extends XmlAdapter<String, MediaType>
{
   public MediaType unmarshal(String s) throws Exception
   {
      if (s == null) return null;
      return MediaType.valueOf(s);
   }

   public String marshal(MediaType mediaType) throws Exception
   {
      if (mediaType == null) return null;
      return mediaType.toString();
   }
}
