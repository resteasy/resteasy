package org.jboss.resteasy.plugins.providers.atom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Attributes common across all atom types
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CommonAttributes
{
   @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
   private String language;
   @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace")
   private URI base;


   @XmlAnyAttribute
   private Map extensionAttributes = new HashMap();

   public String getLanguage()
   {
      return language;
   }

   public void setLanguage(String language)
   {
      this.language = language;
   }

   public URI getBase()
   {
      return base;
   }

   public void setBase(URI base)
   {
      this.base = base;
   }

   public Map getExtensionAttributes()
   {
      return extensionAttributes;
   }
}
