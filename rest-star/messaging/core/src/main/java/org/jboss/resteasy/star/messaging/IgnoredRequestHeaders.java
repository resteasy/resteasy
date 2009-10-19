package org.jboss.resteasy.star.messaging;

import java.util.ArrayList;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class IgnoredRequestHeaders extends ArrayList<String>
{
   public IgnoredRequestHeaders()
   {
      this.add("Accept");
      this.add("Accept-Charset");
      this.add("Accept-Encoding");
      this.add("Accept-Language");
      this.add("From");
      this.add("Host");
      this.add("If-Modified-Since");
      this.add("If-Match");
      this.add("If-None-Match");
      this.add("If-Unmodified-Since");
      this.add("Max-Forwards");
      this.add("Proxy-Authorization");
      this.add("Range");
      this.add("Referer");
      this.add("User-Agent");
      this.add("Cache-Control");
      this.add("Connection");
      this.add("Date");
      this.add("Pragma");
      this.add("Transfer-Encoding");
      this.add("Upgrade");
      this.add("Via");
   }
}
