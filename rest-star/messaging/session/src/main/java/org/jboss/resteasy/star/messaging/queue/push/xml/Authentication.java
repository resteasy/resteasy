package org.jboss.resteasy.star.messaging.queue.push.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Authentication implements Serializable
{
   private AuthenticationType type;

   @XmlElementRef
   public AuthenticationType getType()
   {
      return type;
   }

   public void setType(AuthenticationType type)
   {
      this.type = type;
   }
}
