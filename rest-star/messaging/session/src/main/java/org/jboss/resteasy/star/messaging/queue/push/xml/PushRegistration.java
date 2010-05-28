package org.jboss.resteasy.star.messaging.queue.push.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "push-registration")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"subscription", "durable", "target", "authenticationMechanism", "headers"})
public class PushRegistration implements Serializable
{
   private boolean durable;
   private XmlLink target;
   private Authentication authenticationMechanism;
   private List<XmlHttpHeader> headers = new ArrayList<XmlHttpHeader>();
   private String subscription;

   @XmlElement
   public String getSubscription()
   {
      return subscription;
   }

   public void setSubscription(String subscription)
   {
      this.subscription = subscription;
   }

   @XmlElement
   public boolean isDurable()
   {
      return durable;
   }

   public void setDurable(boolean durable)
   {
      this.durable = durable;
   }

   @XmlElementRef
   public XmlLink getTarget()
   {
      return target;
   }

   public void setTarget(XmlLink target)
   {
      this.target = target;
   }

   @XmlElementRef
   public Authentication getAuthenticationMechanism()
   {
      return authenticationMechanism;
   }

   public void setAuthenticationMechanism(Authentication authenticationMechanism)
   {
      this.authenticationMechanism = authenticationMechanism;
   }

   @XmlElementWrapper(name = "base-headers")
   public List<XmlHttpHeader> getHeaders()
   {
      return headers;
   }

   public void setHeaders(List<XmlHttpHeader> headers)
   {
      this.headers = headers;
   }

   @Override
   public String toString()
   {
      return "PushRegistration{" +
              "durable=" + durable +
              ", target=" + target +
              ", authenticationMechanism=" + authenticationMechanism +
              ", headers=" + headers +
              ", subscription='" + subscription + '\'' +
              '}';
   }
}
