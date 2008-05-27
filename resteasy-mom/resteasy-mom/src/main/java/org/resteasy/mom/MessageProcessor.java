package org.resteasy.mom;

import org.resteasy.util.HttpHeaderNames;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class MessageProcessor
{
   /**
    * Replaces '-' with '$'
    *
    * @param str
    * @return
    */
   protected String toJavaIdentifier(String str)
   {
      return str.replace('-', '$').toLowerCase();
   }

   protected String toHeaderName(String str)
   {
      return str.replace('$', '-').toLowerCase();
   }

   public String createSelector(HttpHeaders headers)
   {
      StringBuffer selector = new StringBuffer();

      boolean first = true;

      for (MediaType type : headers.getAcceptableMediaTypes())
      {
         if (type.isWildcardType()) return "";
         if (first)
         {
            first = false;
         }
         else
         {
            selector.append(" OR ");
         }
         selector.append(toJavaIdentifier(HttpHeaderNames.CONTENT_TYPE));
         if (type.isWildcardSubtype())
         {
            selector.append(" LIKE '")
                    .append(type.getType())
                    .append("/%'");
         }
         else
         {
            selector.append(" = '")
                    .append(type.toString())
                    .append("'");
         }
      }
      return selector.toString();
   }

   public abstract Message createMessage(HttpHeaders headers, InputStream entityStream, Session session)
           throws JMSException, IOException;

   public abstract Response extractResponse(Message message)
           throws JMSException;

   public abstract byte[] extractBody(Message message)
           throws JMSException;

   public Map<String, String> extractHeaders(Message message) throws JMSException
   {
      Map<String, String> rtn = new HashMap<String, String>();

      Enumeration en = message.getPropertyNames();
      while (en.hasMoreElements())
      {
         String key = (String) en.nextElement();
         if (!key.startsWith("JMS"))
         {
            String value = message.getStringProperty(key);
            //System.out.println("header: " + toHeaderName(key) + " value: " + value);
            rtn.put(toHeaderName(key), value);
         }
      }
      return rtn;
   }

}
