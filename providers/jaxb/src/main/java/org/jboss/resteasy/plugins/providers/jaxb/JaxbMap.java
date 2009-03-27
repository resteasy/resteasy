package org.jboss.resteasy.plugins.providers.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbMap
{
   @XmlAnyElement
   List<JAXBElement<Entry>> value = new ArrayList<JAXBElement<Entry>>();

   @XmlTransient
   private String entryName;
   @XmlTransient
   private String keyAttributeName;
   @XmlTransient
   private String namespace;

   public JaxbMap()
   {
   }

   public JaxbMap(String entryName, String keyAttributeName, String namespace)
   {
      this.entryName = entryName;
      this.namespace = namespace;
      this.keyAttributeName = keyAttributeName;
   }

   @XmlAccessorType(XmlAccessType.FIELD)
   public static class Entry
   {
      @XmlAnyElement
      Object value;

      @XmlAnyAttribute
      Map<QName, Object> attribute = new HashMap<QName, Object>();

      @XmlTransient
      private String key;

      @XmlTransient
      private String keyAttributeName;

      public Entry()
      {
      }

      public Entry(String keyAttributeName, String key, Object value)
      {
         this.value = value;
         this.keyAttributeName = keyAttributeName;
         setKey(key);
      }

      public Object getValue()
      {
         return value;
      }

      public void setValue(Object value)
      {
         this.value = value;
      }

      public String getKey()
      {
         if (key != null) return key;
         key = (String) attribute.values().iterator().next();
         return key;
      }

      public void setKey(String keyValue)
      {
         this.key = keyValue;
         attribute.clear();

         QName name = new QName(keyAttributeName);
         attribute.put(name, keyValue);
      }
   }

   public void addEntry(String key, Object val)
   {
      Entry entry = new Entry(keyAttributeName, key, val);
      //JAXBElement<Entry> element = new JAXBElement<Entry>(new QName(namespace, entryName, prefix), Entry.class, entry);
      JAXBElement<Entry> element = new JAXBElement<Entry>(new QName(namespace, entryName), Entry.class, entry);
      //JAXBElement<Entry> element = new JAXBElement<Entry>(new QName(entryName), Entry.class, entry);
      value.add(element);
   }

   public List<JAXBElement<Entry>> getValue()
   {
      return value;
   }
}
