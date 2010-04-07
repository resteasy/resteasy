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
public class TopicMessageRepository implements MessageRepository<Message>
{
   private ConcurrentHashMap<Long, TopicMessageIndex> messageIndex = new ConcurrentHashMap<Long, TopicMessageIndex>();
   private ConcurrentHashMap<Long, Message> repository = new ConcurrentHashMap<Long, Message>();
   private AtomicLong counter = new AtomicLong(0);


   public long generateId()
   {
      return counter.getAndIncrement();
   }

   public Message createMessage(MultivaluedMap<String, String> headers, byte[] body)
   {
      return createMessage(counter.getAndIncrement(), headers, body);
   }

   public Message createMessage(long id, MultivaluedMap<String, String> headers, byte[] body)
   {
      Message msg = new Message();
      msg.setId(id);
      msg.setBody(body);
      msg.setHeaders(headers);
      repository.put(msg.getId(), msg);
      return msg;
   }

   public URI getMessageUri(long id, UriInfo uriInfo)
   {
      UriBuilder builder = uriInfo.getBaseUriBuilder();
      builder.path(TopicResource.class, "poller")
              .path(TopicPollerResource.class, "getMessageResource");
      return builder.build(Long.toString(id));
   }

   public Message getMessage(long id)
   {
      return repository.get(id);
   }

   public TopicMessageIndex getMessageIndex(long id)
   {
      return messageIndex.get(id);
   }

   public TopicMessageIndex addIndex(long id)
   {
      TopicMessageIndex index = new TopicMessageIndex();
      index.setId(id);
      messageIndex.put(id, index);
      return index;
   }

}
