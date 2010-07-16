package org.jboss.resteasy.star.messaging.topic;

import org.jboss.resteasy.star.messaging.queue.push.FilePushStore;
import org.jboss.resteasy.star.messaging.queue.push.xml.PushRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FileTopicPushStore extends FilePushStore implements TopicPushStore
{
   public FileTopicPushStore(String filename)
           throws Exception
   {
      super(filename);
   }

   @Override
   public synchronized List<PushTopicRegistration> getByTopic(String topic)
   {
      List<PushTopicRegistration> list = new ArrayList<PushTopicRegistration>();
      for (PushRegistration reg : map.values())
      {
         PushTopicRegistration topicReg = (PushTopicRegistration) reg;
         if (topicReg.getTopic().equals(topic))
         {
            list.add(topicReg);
         }
      }
      return list;
   }

}
