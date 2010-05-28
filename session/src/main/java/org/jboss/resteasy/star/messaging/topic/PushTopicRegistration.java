package org.jboss.resteasy.star.messaging.topic;

import org.jboss.resteasy.star.messaging.queue.push.xml.PushRegistration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "push-topic-registration")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"topic"})
public class PushTopicRegistration extends PushRegistration
{
   private String topic;

   public String getTopic()
   {
      return topic;
   }

   public void setTopic(String topic)
   {
      this.topic = topic;
   }
}
