package org.jboss.fastjaxb;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 24, 2009
 * Time: 9:56:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class Introspector
{
   protected final Map<Class, RootElement> rootElements = new HashMap<Class, RootElement>();
   protected final Set<Class> valueTypes = new HashSet<Class>();
   protected final Set<String> ignoredMethods = new HashSet<String>();

   public Introspector()
   {
      ignoredMethods.add("getClass");
      valueTypes.add(String.class);
   }

   public Set<String> getIgnoredMethods()
   {
      return ignoredMethods;
   }

   public Set<Class> getValueTypes()
   {
      return valueTypes;
   }

   public Map<Class, RootElement> getRootElements()
   {
      return rootElements;
   }


   public boolean isValueType(Class clazz)
   {
      if (clazz.isPrimitive()) return true;
      if (isJaxbElement(clazz)) return false;
      return true;

   }

   private boolean isJaxbElement(Class clazz)
   {
      if (clazz.isAnnotationPresent(XmlRootElement.class)) return true;
      if (clazz.isAnnotationPresent(XmlType.class)) return true;
      return false;
   }

   public void createMap(Class rootClass)
   {
      if (rootElements.containsKey(rootClass)) return;
      if (!rootClass.isAnnotationPresent(XmlRootElement.class) && !rootClass.isAnnotationPresent(XmlType.class))
      {
         throw new RuntimeException(rootClass.getName() + " is not annotated with @XmlRootElement");
      }
      RootElement rootElement = introspect(rootClass);
      rootElements.put(rootClass, rootElement);
      for (Property property : rootElement.getProperties().values())
      {
         if (property.isAnnotationPresent(XmlAnyElement.class))
         {
            rootElement.setAnyProperty(property);
         }
         if (property.isAnnotationPresent(XmlValue.class))
         {
            rootElement.setValueProperty(property);
         }
         else if (property.isAnnotationPresent(XmlAttribute.class))
         {
            XmlAttribute attr = property.getAnnotation(XmlAttribute.class);
            String qName = attr.name();
            if (qName == null || qName.equals("") || qName.equals("##default"))
            {
               qName = property.getName();
            }
            rootElement.getAttributes().put(qName, property);
         }
         else if (property.isAnnotationPresent(XmlElement.class))
         {
            XmlElement attr = property.getAnnotation(XmlElement.class);
            processXmlElement(rootElement, property, attr);
         }
         else if (property.isAnnotationPresent(XmlElementRef.class))
         {
            XmlElementRef attr = property.getAnnotation(XmlElementRef.class);
            processXmlElementRef(rootElement, property, attr);
         }
         else if (property.isAnnotationPresent(XmlElements.class))
         {
            XmlElements refs = property.getAnnotation(XmlElements.class);
            for (XmlElement ref : refs.value())
            {
               Property cloned = property.clone();
               processXmlElement(rootElement, cloned, ref);
            }
         }
         else if (property.isAnnotationPresent(XmlElementRefs.class))
         {
            XmlElementRefs refs = property.getAnnotation(XmlElementRefs.class);
            for (XmlElementRef ref : refs.value())
            {
               Property cloned = property.clone();
               processXmlElementRef(rootElement, cloned, ref);
            }
         }
         else
         {
            // assume it is an element
            rootElement.getElements().put(property.getName(), property);
            processRootElement(property);
         }

      }
   }

   private void processXmlElementRef(RootElement rootElement, Property property, XmlElementRef attr)
   {
      if (attr.type() != null && !attr.type().equals(XmlElementRef.DEFAULT.class))
      {
         property.setBaseType(attr.type());
      }
      String qName = extractQName(property, attr);
      rootElement.getElements().put(qName, property);
      processRootElement(property);
   }

   private void processXmlElement(RootElement rootElement, Property property, XmlElement attr)
   {
      String qName = extractQName(property, attr);
      if (attr.type() != null && !attr.type().equals(XmlElement.DEFAULT.class))
      {
         property.setBaseType(attr.type());
      }
      rootElement.getElements().put(qName, property);
      processRootElement(property);
   }

   private String extractQName(Property property, XmlElementRef attr)
   {
      String qName = attr.name();
      if (qName == null || qName.equals("") || qName.equals("##default"))
      {
         Class<?> baseType = property.getBaseType();
         if (baseType.isAnnotationPresent(XmlRootElement.class))
         {
            XmlRootElement re = baseType.getAnnotation(XmlRootElement.class);
            qName = re.name();
            if (qName == null || qName.equals("") || qName.equals("##default"))
            {
               qName = null;
            }

         }
         if (qName == null)
         {
            qName = baseType.getSimpleName().toLowerCase();
         }
      }
      return qName;
   }

   private String extractQName(Property property, XmlElement attr)
   {
      String qName = attr.name();
      if (qName == null || qName.equals("") || qName.equals("##default"))
      {
         qName = property.getName();
      }
      return qName;
   }

   private void processRootElement(Property property)
   {
      if (isJaxbElement(property.getBaseType()))
      {
         createMap(property.getBaseType());
      }
   }

   public RootElement introspect(Class<?> rootClass)
   {
      RootElement root = new RootElement();
      XmlRootElement re = rootClass.getAnnotation(XmlRootElement.class);
      if (re != null)
      {
         root.setElementName(re.name());
         root.setNamespace(re.namespace());
      }

      for (Method method : rootClass.getMethods())
      {
         if (method.isAnnotationPresent(XmlTransient.class)) continue;
         if (ignoredMethods.contains(method.getName())) continue;

         if (method.getName().startsWith("get") && method.getName().length() > 3 && method.getParameterTypes().length == 0)
         {
            String propertyName = extractPropertyName(method);
            Property property = root.getProperties().get(propertyName);
            if (property != null)
            {
               property.setGetter(method);
               continue;
            }
            property = new Property(propertyName);
            property.setGetter(method);
            root.getProperties().put(propertyName, property);
         }
         else if (method.getName().startsWith("set") && method.getName().length() > 3 && method.getParameterTypes().length == 1)
         {
            String propertyName = extractPropertyName(method);
            Property property = root.getProperties().get(propertyName);
            if (property != null)
            {
               property.setSetter(method);
               continue;
            }
            property = new Property(propertyName);
            property.setSetter(method);
            root.getProperties().put(propertyName, property);
         }
      }
      return root;
   }

   private String extractPropertyName(Method method)
   {
      String propertyName = method.getName().substring(3);
      StringBuffer buf = new StringBuffer(propertyName);
      buf.setCharAt(0, Character.toLowerCase(buf.charAt(0)));
      propertyName = buf.toString();
      return propertyName;
   }
}
