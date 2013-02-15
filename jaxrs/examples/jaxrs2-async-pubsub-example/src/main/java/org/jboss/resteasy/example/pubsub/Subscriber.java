package org.jboss.resteasy.example.pubsub;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedList;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Subscriber
{
   public static class Entry
   {
      MediaType type;
      byte[] content;

      public Entry(MediaType type, byte[] content)
      {
         this.type = type;
         this.content = content;
      }
   }
   protected int size;
   protected LinkedList<Entry> queue = new LinkedList<Entry>();
   protected AsyncResponse response;

   public Subscriber(int size)
   {
      this.size = size;
   }

   public synchronized void post(MediaType type, byte[] content)
   {
      if (response == null)
      {
         if (queue.size() == size)
         {
            // junk oldest.
            queue.removeFirst();
         }
         queue.addLast(new Entry(type, content));
         return;
      }

      response.resume(Response.ok(content, type).build());
      response = null;
   }

   public synchronized void poll(AsyncResponse response)
   {
      if (queue.size() == 0)
      {
         this.response = response;
         return;
      }

      Entry entry = queue.removeFirst();
      response.resume(Response.ok(entry.content, entry.type).build());
   }
}
