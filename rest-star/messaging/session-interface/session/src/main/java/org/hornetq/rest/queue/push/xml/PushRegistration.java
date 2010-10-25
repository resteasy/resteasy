package org.hornetq.rest.queue.push.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
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
@XmlType(propOrder = {"destination", "durable", "selector", "target", "authenticationMechanism", "headers"})
public class PushRegistration implements Serializable
{
   private String id;
   private boolean durable;
   private XmlLink target;
   private Authentication authenticationMechanism;
   private List<XmlHttpHeader> headers = new ArrayList<XmlHttpHeader>();
   private String destination;
   private Object loadedFrom;
   private String selector;

   @XmlTransient
   public Object getLoadedFrom()
   {
      return loadedFrom;
   }

   public void setLoadedFrom(Object loadedFrom)
   {
      this.loadedFrom = loadedFrom;
   }

   @XmlAttribute
   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   @XmlElement
   public String getDestination()
   {
      return destination;
   }

   public void setDestination(String destination)
   {
      this.destination = destination;
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

   public String getSelector()
   {
      return selector;
   }

   public void setSelector(String selector)
   {
      this.selector = selector;
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

   @XmlElementRef
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
              ", destination='" + destination + '\'' +
              '}';
   }
}
