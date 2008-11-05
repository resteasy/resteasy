package org.jboss.resteasy.plugins.providers.atom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.net.URI;

/**
 * <p>Per RFC4287</p>
 * <p/>
 * <pre>
 *  atomGenerator = element atom:generator {
 *     atomCommonAttributes,
 *     attribute uri { atomUri }?,
 *     attribute version { text }?,
 *     text
 *  }
 * </pre>
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "generator")
@XmlAccessorType(XmlAccessType.FIELD)
public class Generator
{
   @XmlAttribute
   private URI uri;

   @XmlAttribute
   private String version;

   @XmlValue
   private String text;

   @XmlAttribute(name = "lang")
   private String language;

   @XmlAttribute
   private URI base;

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

   public URI getUri()
   {
      return uri;
   }

   public void setUri(URI uri)
   {
      this.uri = uri;
   }

   public String getVersion()
   {
      return version;
   }

   public void setVersion(String version)
   {
      this.version = version;
   }

   public String getText()
   {
      return text;
   }

   public void setText(String text)
   {
      this.text = text;
   }
}
