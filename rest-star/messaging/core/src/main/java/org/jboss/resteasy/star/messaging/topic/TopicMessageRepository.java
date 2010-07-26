package org.hornetq.rest.topic;

import org.hornetq.rest.DestinationResource;
import org.hornetq.rest.Message;
import org.hornetq.rest.MessageRepository;

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
public class TopicMessageRepository implements MessageRepository<Message>
{
   private ConcurrentHashMap<String, TopicMessageIndex> messageIndex = new ConcurrentHashMap<String, TopicMessageIndex>();
   private ConcurrentHashMap<String, Message> repository = new ConcurrentHashMap<String, Message>();
   private AtomicLong counter = new AtomicLong(0);
   private String destination;

   public TopicMessageRepository()
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

   public String generateId()
   {
      return Long.toString(counter.getAndIncrement());
   }

   public Message createMessage(MultivaluedMap<String, String> headers, byte[] body)
   {
      return createMessage(Long.toString(counter.getAndIncrement()), headers, body);
   }

   public Message createMessage(String id, MultivaluedMap<String, String> headers, byte[] body)
   {
      Message msg = new Message();
      msg.setId(id);
      msg.setBody(body);
      msg.setHeaders(headers);
      repository.put(msg.getId(), msg);
      return msg;
   }

   public URI getMessageUri(String id, UriInfo uriInfo)
   {
      UriBuilder builder = uriInfo.getBaseUriBuilder();
      builder.path(DestinationResource.class, "findTopic")
              .path(TopicResource.class, "poller")
              .path(TopicPollerResource.class, "getMessageResource");
      return builder.build(destination, id);
   }

   public Message getMessage(String id)
   {
      return repository.get(id);
   }

   public TopicMessageIndex getMessageIndex(String id)
   {
      return messageIndex.get(id);
   }

   public TopicMessageIndex addIndex(String id)
   {
      TopicMessageIndex index = new TopicMessageIndex();
      index.setId(id);
      messageIndex.put(id, index);
      return index;
   }

}
