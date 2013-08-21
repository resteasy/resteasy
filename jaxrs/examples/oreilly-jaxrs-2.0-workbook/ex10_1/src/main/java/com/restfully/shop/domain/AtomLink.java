package com.restfully.shop.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "link")
public class AtomLink
{
   protected String relationship;
   protected String href;
   protected String type;

   public AtomLink()
   {
   }

   public AtomLink(String relationship, String href, String type)
   {
      this.relationship = relationship;
      this.href = href;
      this.type = type;
   }

   @XmlAttribute(name = "rel")
   public String getRelationship()
   {
      return relationship;
   }

   public void setRelationship(String relationship)
   {
      this.relationship = relationship;
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
}
