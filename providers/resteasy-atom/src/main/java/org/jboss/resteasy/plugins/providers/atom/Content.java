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
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Represents an atom:content element.</p>
 * <p>Per RFC4287:</p>
 * <pre>
 *  The "atom:content" element either contains or links to the content of
 *  the entry.  The content of atom:content is Language-Sensitive.
 *
 *  atomInlineTextContent =
 *     element atom:content {
 *        atomCommonAttributes,
 *        attribute type { "text" | "html" }?,
 *        (text)*
 *     }
 *
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
 *
 *  atomOutOfLineContent =
 *     element atom:content {
 *        atomCommonAttributes,
 *        attribute type { atomMediaType }?,
 *        attribute src { atomUri },
 *        empty
 *     }
 *
 *  atomContent = atomInlineTextContent
 *   | atomInlineXHTMLContent
 *   | atomInlineOtherContent
 *   | atomOutOfLineContent
 *
 * </pre>
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "content")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Content extends CommonAttributes
{

   private String type;

   private MediaType mediaType;

   private String text;

   private Element element;

   private URI src;

   private List<Object> value;

   private Object jaxbObject;

   protected JAXBContextFinder finder;

   protected void setFinder(JAXBContextFinder finder)
   {
      this.finder = finder;
   }

   @XmlAnyElement
   @XmlMixed
   public List<Object> getValue()
   {
      return value;
   }

   public void setValue(List<Object> value)
   {
      this.value = value;
   }

   @XmlAttribute
   public URI getSrc()
   {
      return src;
   }

   public void setSrc(URI src)
   {
      this.src = src;
   }

   /**
    * Mime type of the content.
    *
    * @return media type
    */
   @XmlTransient
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
      if (type.equals(MediaType.TEXT_PLAIN_TYPE)) this.type = "text";
      else if (type.equals(MediaType.TEXT_HTML_TYPE)) this.type = "html";
      else if (type.equals(MediaType.APPLICATION_XHTML_XML_TYPE)) this.type = "xhtml";
      else this.type = type.toString();
   }

   @XmlAttribute(name = "type")
   public String getRawType()
   {
      return type;
   }


   public void setRawType(String type)
   {
      this.type = type;
   }


   /**
    * If content is text, return it as a String.  Otherwise, if content is not text this will return null.
    *
    * @return text
    */
   @XmlTransient
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
    * Set content as text.
    *
    * @param text text
    */
   public void setText(String text)
   {
      if (value == null) value = new ArrayList<Object>();
      if (this.text != null && value != null) value.clear();
      this.text = text;
      value.add(text);
   }

   /**
    * Get content as an XML Element if the content is XML.  Otherwise, this will just return null.
    *
    * @return {@link Element}
    */
   @XmlTransient
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
    * Set the content to an XML Element.
    *
    * @param element {@link Element}
    */
   public void setElement(Element element)
   {
      if (value == null) value = new ArrayList<Object>();
      if (this.element != null && value != null) value.clear();
      this.element = element;
      value.add(element);

   }

   /**
    * Extract the content as the provided JAXB annotated type.
    * <p>
    * This method will use a cached JAXBContext used by the Resteasy JAXB providers
    * or, if those are not existent, it will create a new JAXBContext from scratch
    * using the class.
    *
    * @param <T> type
    * @param clazz                class type you are expecting
    * @param otherPossibleClasses Other classe you want to create the JAXBContext with
    * @return null if there is no XML content
    * @throws JAXBException jaxb exception
    */
   @SuppressWarnings(value = "unchecked")
   public <T> T getJAXBObject(Class<T> clazz, Class... otherPossibleClasses) throws JAXBException
   {
      JAXBContext ctx = null;
      Class[] classes = {clazz};
      if (otherPossibleClasses != null && otherPossibleClasses.length > 0)
      {
         classes = new Class[1 + otherPossibleClasses.length];
         classes[0] = clazz;
         for (int i = 0; i < otherPossibleClasses.length; i++) classes[i + 1] = otherPossibleClasses[i];
      }
      if (finder != null)
      {
         ctx = finder.findCacheContext(MediaType.APPLICATION_XML_TYPE, null, classes);
      }
      else
      {
         ctx = JAXBContext.newInstance(classes);
      }
      if (getElement() == null) return null;
      Object obj = null;

      if (System.getSecurityManager() == null) {
         obj = ctx.createUnmarshaller().unmarshal(getElement());
      }
      else
      {
         final JAXBContext smCtx = ctx;
         try {
            obj = AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
               @Override
               public Object run() throws Exception {
                  return smCtx.createUnmarshaller().unmarshal(getElement());
               }
            });
         } catch (PrivilegedActionException pae)
         {
            throw new JAXBException(pae);
         }
      }

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
    * Returns previous extracted jaxbobject from a call to getJAXBObject(Class{@literal <}T{@literal >} clazz)
    * or value passed in through a previous setJAXBObject().
    *
    * @return jaxb object
    */
   @XmlTransient
   public Object getJAXBObject()
   {
      return jaxbObject;
   }

   public void setJAXBObject(Object obj)
   {
      if (value == null) value = new ArrayList<Object>();
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
