package org.hornetq.rest.topic;

import org.hornetq.rest.queue.push.FilePushStore;
import org.hornetq.rest.queue.push.xml.PushRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FileTopicPushStore extends FilePushStore implements TopicPushStore
{
   public FileTopicPushStore(String dirname)
           throws Exception
   {
      super(dirname);
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
