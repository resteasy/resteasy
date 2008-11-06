package org.jboss.resteasy.plugins.providers.atom;

import org.jboss.resteasy.plugins.providers.jaxb.JAXBContextFinder;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlTypeProvider;
import org.w3c.dom.Element;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Represents an atom:content element.</p>
 * <p/>
 * <p>Per RFC4287:</p>
 * <p/>
 * <pre>
 *  The "atom:content" element either contains or links to the content of
 *  the entry.  The content of atom:content is Language-Sensitive.
 * <p/>
 *  atomInlineTextContent =
 *     element atom:content {
 *        atomCommonAttributes,
 *        attribute type { "text" | "html" }?,
 *        (text)*
 *     }
 * <p/>
 *  atomInlineXHTMLContent =
 *     element atom:content {
 *        atomCommonAttributes,
 *        attribute type { "xhtml" },
 *        xhtmlDiv
 *     }
 *  atomInlineOtherContent =
 *     element atom:content {
 *        atomCommonAttributes,
 *        attribute type { atomMediaType }?,
 *        (text|anyElement)*
 *     }
 * <p/>
 *  atomOutOfLineContent =
 *     element atom:content {
 *        atomCommonAttributes,
 *        attribute type { atomMediaType }?,
 *        attribute src { atomUri },
 *        empty
 *     }
 * <p/>
 *  atomContent = atomInlineTextContent
 *   | atomInlineXHTMLContent
 *   | atomInlineOtherContent
 *   | atomOutOfLineContent
 * <p/>
 * </pre>
 *
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

   /**
    * Mime type of the content
    *
    * @return
    */
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

   /**
    * If content is text, return it as a String.  Otherwise, if content is not text this will return null.
    *
    * @return
    */
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

   /**
    * Set content as text
    *
    * @param text
    */
   public void setText(String text)
   {
      if (value == null) value = new ArrayList();
      if (this.text != null && value != null) value.clear();
      this.text = text;
      value.add(text);
   }

   /**
    * Get content as an XML Element if the content is XML.  Otherwise, this will just return null.
    *
    * @return
    */
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

   /**
    * Set the content to an XML Element
    *
    * @param element
    */
   public void setElement(Element element)
   {
      if (this.element != null && value != null) value.clear();
      this.element = element;
      value.add(element);

   }

   /**
    * Extract the content as the provided JAXB annotated type.
    * <p/>
    * This method will use a cached JAXBContext used by the Resteasy JAXB providers
    * or, if those are not existent, it will create a new JAXBContext from scratch
    * using the class.
    *
    * @param clazz
    * @return null if there is no XML content
    * @throws JAXBException
    */
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
      if (getElement() == null) return null;
      Object obj = ctx.createUnmarshaller().unmarshal(getElement());
      if (obj instanceof JAXBElement)
      {
         jaxbObject = ((JAXBElement) obj).getValue();
         return (T) jaxbObject;
      }
      else
      {
         jaxbObject = obj;
         return (T) obj;
      }
   }

   /**
    * Returns previous extracted jaxbobject from a call to getJAXBObject(Class<T> clazz)
    * or value passed in through a previous setJAXBObject().
    *
    * @return
    */
   public Object getJAXBObject()
   {
      return jaxbObject;
   }

   public void setJAXBObject(Object obj)
   {
      if (value == null) value = new ArrayList();
      if (jaxbObject != null && value != null) value.clear();
      if (!obj.getClass().isAnnotationPresent(XmlRootElement.class) && obj.getClass().isAnnotationPresent(XmlType.class))
      {
         value.add(JAXBXmlTypeProvider.wrapInJAXBElement(obj, obj.getClass()));
      }
      else
      {
         value.add(obj);
      }
      jaxbObject = obj;
   }

}
