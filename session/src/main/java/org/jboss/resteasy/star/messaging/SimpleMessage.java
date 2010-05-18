package org.jboss.resteasy.star.messaging;

import org.hornetq.api.core.client.ClientMessage;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.util.CaseInsensitiveMap;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import java.io.Serializable;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SimpleMessage implements Serializable
{
   private MultivaluedMapImpl<String, String> headerCopy = new MultivaluedMapImpl<String, String>();
   ;
   private byte[] body;

   public SimpleMessage(HttpHeaders headers, byte[] body)
   {
      MultivaluedMap<String, String> hdrs = headers.getRequestHeaders();
      for (String key : hdrs.keySet())
      {
         String lowerKey = key.toLowerCase();
         if (lowerKey.toLowerCase().startsWith("content")
                 || lowerKey.toLowerCase().equals("link"))
         {
            headerCopy.put(key, hdrs.get(key));
         }
      }

      this.body = body;
   }

   public MultivaluedMap<String, String> getHeaders()
   {
      return headerCopy;
   }

   public byte[] getBody()
   {
      return body;
   }

}
