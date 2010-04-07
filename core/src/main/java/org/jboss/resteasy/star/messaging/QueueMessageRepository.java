package org.jboss.resteasy.star.messaging;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueueMessageRepository implements MessageRepository<QueuedMessage>
{
   private ConcurrentHashMap<Long, QueuedMessage> repository = new ConcurrentHashMap<Long, QueuedMessage>();
   private AtomicLong counter = new AtomicLong(0);
   private String destination;

   public QueueMessageRepository()
   {
   }

   public String getDestination()
   {
      return destination;
   }

   public void setDestination(String destination)
   {
      this.destination = destination;
   }

   public QueuedMessage getMessage(long id)
   {
      return repository.get(id);
   }

   @Override
   public long generateId()
   {
      return counter.getAndIncrement();
   }

   @Override
   public QueuedMessage createMessage(MultivaluedMap<String, String> headers, byte[] body)
   {
      return createMessage(generateId(), headers, body);
   }

   @Override
   public QueuedMessage createMessage(long id, MultivaluedMap<String, String> headers, byte[] body)
   {
      QueuedMessage msg = new QueuedMessage();
      msg.setId(id);
      msg.setBody(body);
      msg.setHeaders(headers);
      repository.put(msg.getId(), msg);
      return msg;
   }

   @Override
   public URI getMessageUri(long id, UriInfo uriInfo)
   {
      UriBuilder builder = uriInfo.getBaseUriBuilder();
      builder.path(DestinationResource.class, "findQueue")
              .path(QueueResource.class, "getMessage");
      return builder.build(destination, Long.toString(id));
   }
}