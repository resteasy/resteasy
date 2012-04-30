package org.jboss.resteasy.tests;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "xformat")
public class XFormat
{
   protected String id;
   protected String bean;

   public XFormat()
   {
   }

   public XFormat(String id)
   {
      this.id = id;
   }

   public XFormat(String id, String bean)
   {
      this.id = id;
      this.bean = bean;
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

   @XmlAttribute
   public String getBean()
   {
      return bean;
   }

   public void setBean(String bean)
   {
      this.bean = bean;
   }
}
