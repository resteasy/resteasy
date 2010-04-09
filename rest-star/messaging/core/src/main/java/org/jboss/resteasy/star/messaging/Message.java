package org.jboss.resteasy.star.messaging;

import org.jboss.resteasy.util.CaseInsensitiveMap;

import javax.ws.rs.core.MultivaluedMap;
import java.io.Serializable;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Message implements Serializable
{
   private String id = "";
   private CaseInsensitiveMap<String> headers = new CaseInsensitiveMap<String>();
   private byte[] body;

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public MultivaluedMap<String, String> getHeaders()
   {
      return headers;
   }

   public void setHeaders(MultivaluedMap<String, String> headers)
   {
      this.headers.putAll(headers);
   }

   public byte[] getBody()
   {
      return body;
   }

   public void setBody(byte[] body)
   {
      this.body = body;
   }

}
