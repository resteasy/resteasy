package org.jboss.fastjaxb;

import javax.xml.bind.annotation.*;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

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
         if (property.isAnnotationPresent(XmlAttribute.class))
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
            String qName = attr.name();
            if (qName == null || qName.equals("") || qName.equals("##default"))
            {
               qName = property.getName();
            }
            if (attr.type() != null)
            {
               property.setBaseType(attr.type());
            }
            rootElement.getElements().put(qName, property);
            if (isValueType(property.getBaseType()) == false)
            {
               createMap(property.getBaseType());
            }
         }
         else if (property.isAnnotationPresent(XmlElementRef.class))
         {
            XmlElementRef attr = property.getAnnotation(XmlElementRef.class);
            if (attr.type() != null)
            {
               property.setBaseType(attr.type());
            }
            Class<?> baseType = property.getBaseType();
            String qName = null;
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
            rootElement.getElements().put(qName, property);
            if (isValueType(property.getBaseType()) == false)
            {
               createMap(property.getBaseType());
            }
         }
         else
         {
            // assume it is an element
            rootElement.getElements().put(property.getName(), property);
            boolean b = isValueType(property.getBaseType());
            if (b)
            {
            }
            else
            {
               createMap(property.getBaseType());
            }
         }

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
