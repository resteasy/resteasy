package org.hornetq.rest.queue.push.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "link")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class XmlLink implements Serializable
{
   protected String method;
   protected String className;
   protected String rel;
   protected String type;
   protected String href;

   @XmlAttribute(name = "class")
   public String getClassName()
   {
      return className;
   }

   public void setClassName(String className)
   {
      this.className = className;
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
      return rel;
   }

   public void setRelationship(String relationship)
   {
      rel = relationship;
   }

   @XmlAttribute
   public String getHref()
   {
      return href;
   }

   public void setHref(String href)
   {
      this.href = href;
   }

   @XmlAttribute
   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   @Override
   public String toString()
   {
      return "XmlLink{" +
              "className='" + className + '\'' +
              ", rel='" + rel + '\'' +
              ", href='" + href + '\'' +
              ", type='" + type + '\'' +
              ", method='" + method + '\'' +
              '}';
   }
}
