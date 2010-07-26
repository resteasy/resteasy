package org.hornetq.rest.queue.push.xml;

import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.spi.Link;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "link")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class XmlLink implements Serializable
{
   protected Link delegate;
   protected String method;

   public XmlLink(Link delegate)
   {
      this.delegate = delegate;
   }

   public XmlLink()
   {
   }

   @XmlAttribute
   public String getMethod()
   {
      return method;
   }

   public void setMethod(String method)
   {
      this.method = method;
   }

   @XmlAttribute(name = "rel")
   public String getRelationship()
   {
      return getDelegate().getRelationship();
   }

   public void setRelationship(String relationship)
   {
      getDelegate().setRelationship(relationship);
   }

   @XmlAttribute
   public String getHref()
   {
      return getDelegate().getHref();
   }

   public void setHref(String href)
   {
      getDelegate().setHref(href);
   }

   @XmlAttribute
   public String getType()
   {
      return getDelegate().getType();
   }

   public void setType(String type)
   {
      getDelegate().setType(type);
   }

   @XmlAttribute
   public String getTitle()
   {
      return getDelegate().getTitle();
   }

   public void setTitle(String title)
   {
      getDelegate().setTitle(title);
   }

   @XmlTransient
   public ClientExecutor getExecutor()
   {
      return getDelegate().getExecutor();
   }

   public void setExecutor(ClientExecutor executor)
   {
      getDelegate().setExecutor(executor);
   }

   public ClientRequest request()
   {
      return getDelegate().request();
   }

   public ClientRequest request(ClientExecutor executor)
   {
      return getDelegate().request(executor);
   }

   @Override
   public String toString()
   {
      return "XmlLink{" +
              "delegate=" + delegate +
              ", method='" + method + '\'' +
              '}';
   }

   public Link getDelegate()
   {
      if (delegate == null) delegate = new Link();
      return delegate;
   }


}
