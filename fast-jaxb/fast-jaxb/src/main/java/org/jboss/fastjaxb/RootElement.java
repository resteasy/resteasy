package org.jboss.fastjaxb;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 24, 2009
 * Time: 2:25:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class RootElement
{
   protected Class rootClass;
   protected Map<String, Property> properties = new LinkedHashMap<String, Property>();
   protected Map<String, Property> attributes = new LinkedHashMap<String, Property>();
   protected Map<String, Property> elements = new LinkedHashMap<String, Property>();
   protected String elementName;
   protected String namespace;

   public Class getRootClass()
   {
      return rootClass;
   }

   public void setRootClass(Class rootClass)
   {
      this.rootClass = rootClass;
   }

   public Map<String, Property> getProperties()
   {
      return properties;
   }

   public Map<String, Property> getAttributes()
   {
      return attributes;
   }

   public Map<String, Property> getElements()
   {
      return elements;
   }

   public String getElementName()
   {
      return elementName;
   }

   public void setElementName(String elementName)
   {
      this.elementName = elementName;
   }

   public String getNamespace()
   {
      return namespace;
   }

   public void setNamespace(String namespace)
   {
      this.namespace = namespace;
   }
}
