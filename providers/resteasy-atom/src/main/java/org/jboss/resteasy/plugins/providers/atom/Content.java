package org.jboss.resteasy.plugins.providers.atom;

import org.jboss.resteasy.plugins.providers.jaxb.JAXBContextFinder;
import org.w3c.dom.Element;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "content")
@XmlAccessorType(XmlAccessType.FIELD)
public class Content extends CommonAttributes
{

   @XmlAttribute
   private String type;

   @XmlTransient
   private MediaType mediaType;

   @XmlTransient
   private String text;

   @XmlTransient
   private Element element;

   @XmlAttribute
   private URI src;

   @XmlAnyElement
   @XmlMixed
   private List<Object> value;

   @XmlTransient
   private Object jaxbObject;

   @XmlTransient
   protected JAXBContextFinder finder;

   protected void setFinder(JAXBContextFinder finder)
   {
      this.finder = finder;
   }

   public MediaType getType()
   {
      if (mediaType == null)
      {
         if (type.equals("html")) mediaType = MediaType.TEXT_HTML_TYPE;
         else if (type.equals("text")) mediaType = MediaType.TEXT_PLAIN_TYPE;
         else if (type.equals("xhtml")) mediaType = MediaType.APPLICATION_XHTML_XML_TYPE;
         else mediaType = MediaType.valueOf(type);
      }
      return mediaType;
   }

   public void setType(MediaType type)
   {
      mediaType = type;
      this.type = type.toString();
   }

   public String getText()
   {
      if (value == null) return null;
      if (value.size() == 0) return null;
      if (text != null) return text;
      StringBuffer buf = new StringBuffer();
      for (Object obj : value)
      {
         if (obj instanceof String) buf.append(obj.toString());
      }
      text = buf.toString();
      return text;
   }

   public void setText(String text)
   {
      if (value == null) value = new ArrayList();
      value.add(text);
   }

   public Element getElement()
   {
      if (value == null) return null;
      if (element != null) return element;
      for (Object obj : value)
      {
         if (obj instanceof Element)
         {
            element = (Element) obj;
            return element;
         }
      }
      return null;
   }

   public <T> T getJAXBObject(Class<T> clazz) throws JAXBException
   {
      JAXBContext ctx = null;
      if (finder != null)
      {
         ctx = finder.findCachedContext(clazz, MediaType.APPLICATION_XML_TYPE, null);
      }
      else
      {
         ctx = JAXBContext.newInstance(clazz);
      }
      return (T) ctx.createUnmarshaller().unmarshal(getElement());
   }

   public Object getJAXBObject()
   {
      return jaxbObject;
   }

   public void setJAXBObject(Object obj)
   {
      if (value == null) value = new ArrayList();
      value.add(obj);
      jaxbObject = obj;
   }

}
