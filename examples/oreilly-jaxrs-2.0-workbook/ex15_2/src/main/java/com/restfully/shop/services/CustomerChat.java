package com.restfully.shop.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("chat")
public class CustomerChat
{
   class Message
   {
      String id;
      String message;
      Message next;
   }

   protected AtomicLong counter = new AtomicLong(0);
   protected int maxMessages = 100;
   protected Message first;
   protected Message last;
   protected LinkedHashMap<String, Message> messages = new LinkedHashMap<String, Message>()
   {
      @Override
      protected boolean removeEldestEntry(Map.Entry<String, Message> eldest)
      {
         boolean remove = size() > maxMessages;
         if (remove) first = eldest.getValue().next;
         return remove;
      }
   };

   LinkedList<AsyncResponse> listeners = new LinkedList<AsyncResponse>();

   ExecutorService writer = Executors.newSingleThreadExecutor();

   @Context
   protected UriInfo uriInfo;

   @POST
   @Consumes("text/plain")
   public void post(final String text)
   {
      final UriBuilder base = uriInfo.getBaseUriBuilder();
      writer.submit(new Runnable()
      {
         @Override
         public void run()
         {
            synchronized (messages)
            {
               Message message = new Message();
               message.id = Long.toString(counter.incrementAndGet());
               message.message = text;

               if (messages.size() == 0)
               {
                  first = message;
               }
               else
               {
                  last.next = message;
               }
               messages.put(message.id, message);
               last = message;

               for (AsyncResponse async : listeners)
               {
                  try
                  {
                     send(base, async, message);
                  }
                  catch (Exception e)
                  {
                     e.printStackTrace();
                  }
               }
               listeners.clear();
            }
         }
      });
   }

   @GET
   public void get(@QueryParam("current") String next, @Suspended AsyncResponse async)
   {
      final UriBuilder base = uriInfo.getBaseUriBuilder();
      Message message = null;
      synchronized (messages)
      {
         Message current = messages.get(next);
         if (current == null) message = first;
         else message = current.next;

         if (message == null) {
            queue(async);
         }
      }
      // do this outside of synchronized block to reduce lock hold time
      if (message != null) send(base, async, message);
    }

   protected void queue(AsyncResponse async)
   {
      listeners.add(async);
   }

   protected void send(UriBuilder base, AsyncResponse async, Message message)
   {
      URI nextUri = base.clone().path(CustomerChat.class)
              .queryParam("current", message.id).build();
      Link next = Link.fromUri(nextUri).rel("next").build();
      Response response = Response.ok(message.message, MediaType.TEXT_PLAIN_TYPE).links(next).build();
      async.resume(response);
   }
}
