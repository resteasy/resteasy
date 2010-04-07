package org.jboss.resteasy.star.messaging;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface MessagePublisher
{
   public void publish(Message message) throws Exception;
}
