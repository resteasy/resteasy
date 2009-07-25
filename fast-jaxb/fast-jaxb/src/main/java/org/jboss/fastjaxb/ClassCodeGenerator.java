package org.jboss.fastjaxb;

import org.jboss.fastjaxb.template.Handler;
import org.jboss.fastjaxb.template.ParentCallback;
import org.jboss.fastjaxb.template.Sax;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.*;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 24, 2009
 * Time: 5:15:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClassCodeGenerator
{
   protected PrintWriter writer;
   protected String packageName;
   protected Class clazz;
   protected RootElement rootElement;
   protected Introspector introspector;

   public Introspector getIntrospector()
   {
      return introspector;
   }

   public void setIntrospector(Introspector introspector)
   {
      this.introspector = introspector;
   }

   public PrintWriter getWriter()
   {
      return writer;
   }

   public void setWriter(PrintWriter writer)
   {
      this.writer = writer;
   }

   public String getPackageName()
   {
      return packageName;
   }

   public void setPackageName(String packageName)
   {
      this.packageName = packageName;
   }

   public Class getClazz()
   {
      return clazz;
   }

   public void setClazz(Class clazz)
   {
      this.clazz = clazz;
   }

   public RootElement getRootElement()
   {
      return rootElement;
   }

   public void setRootElement(RootElement rootElement)
   {
      this.rootElement = rootElement;
   }

   public void generate()
   {
      writer.println("package " + packageName + ";");
      writer.println();
      writer.println();
      writeImports();
      writer.println();
      writer.println("public class " + clazz.getSimpleName() + "_Parser extends DefaultHandler implements ParentCallback, Handler");
      writer.println("{");
      writer.println();
      createEnum();
      writer.println();
      createFields();
      writer.println();
      writeSimpleMethods();
      writer.println();
      writeStart();
      writer.println();
      writeAddMethod();
      writer.println();
      writeCharacters();
      writer.println();
      writeStartElement();
      writer.println();
      writeEndElement();
      writer.println("}");

   }

   public void createEnum()
   {
      if (rootElement.getElements().size() == 0) return;

      writer.println("   private static enum State");
      writer.println("   {");
      boolean first = true;
      for (Property property : rootElement.getElements().values())
      {
         if (first)
         {
            first = false;
         }
         else
         {
            writer.println(",");
         }
         writer.print("       " + property.getName().toUpperCase());
      }
      writer.println();
      writer.println("   }");

   }

   public void createFields()
   {
      writer.println("   protected Sax top;");
      writer.println("   protected ParentCallback handler;");
      writer.println("   protected String tempVal;");
      writer.println("   protected State state;");
      writer.println("   protected String qName;");
      writer.println("   protected " + clazz.getSimpleName() + " target;");
   }

   public void writeImports()
   {
      HashSet<String> imports = new HashSet<String>();
      imports.add(DefaultHandler.class.getName());
      imports.add(Attributes.class.getName());
      imports.add(SAXException.class.getName());
      imports.add(Handler.class.getName());
      imports.add(ParentCallback.class.getName());
      imports.add(Sax.class.getName());
      imports.add(clazz.getName());
      for (Property property : rootElement.getProperties().values())
      {
         if (!property.getType().isArray()) imports.add(property.getType().getName());
         if (!property.getBaseType().isPrimitive()
                 && property.getBaseType().equals(String.class)
                 && property.getBaseType().equals(Object.class))
         {
            imports.add(property.getBaseType().getName());
         }
      }
      for (String imp : imports) writer.println("import " + imp + ";");
   }

   public String generated(Class cls)
   {
      return cls.getSimpleName() + "_Parser";

   }

   public String generated()
   {
      return generated(clazz);
   }

   public void writeSimpleMethods()
   {
      writer.println("   public void setTop(Sax top)");
      writer.println("   {");
      writer.println("      this.top = top;");
      writer.println("   }");
      writer.println();
      writer.println("   public void setParentCallback(ParentCallback callback)");
      writer.println("   {");
      writer.println("      handler = callback;");
      writer.println("   }");
      writer.println();
      writer.println("   public Handler newInstance()");
      writer.println("   {");
      writer.println("      return new " + generated() + "();");
      writer.println("   }");
      writer.println("");
      writer.println("   public void endChild()");
      writer.println("   {");
      writer.println("      state = null;");
      writer.println("   }");
   }

   public void outputPropertySet(String variable, Property property)
   {
      if (property.getType().equals(String.class))
      {
         writer.println("      this.target." + property.getSetter().getName() + "(" + variable + ");");
         return;
      }
      writer.println("      {");
      writer.println("         String   tmp = " + variable + ";");
      if (property.getType().isPrimitive())
      {
         Class primitiveType = property.getType();
         if (primitiveType.equals(boolean.class)) writer.println("         boolean tmp2 = Boolean.valueOf(tmp);");
         if (primitiveType.equals(int.class)) writer.println("         int tmp2 = Integer.valueOf(tmp);");
         if (primitiveType.equals(long.class)) writer.println("         long tmp2 = Long.valueOf(tmp);");
         if (primitiveType.equals(double.class)) writer.println("         double tmp2 = Double.valueOf(tmp);");
         if (primitiveType.equals(float.class)) writer.println("         float tmp2 = Float.valueOf(tmp);");
         if (primitiveType.equals(byte.class)) writer.println("         byte tmp2 = Byte.valueOf(tmp);");
         if (primitiveType.equals(short.class)) writer.println("         short tmp2 = Short.valueOf(tmp);");
      }
      else
      {
         throw new RuntimeException("Type not supported" + property.getType().getName());
      }
      writer.println("      this.target." + property.getSetter().getName() + "(tmp2);");


      writer.println("      }");

   }

   public void writeStart()
   {
      writer.println("   public void start(Attributes attributes, String qName)");
      writer.println("   {");
      writer.println("      this.qName = qName;");
      writer.println("      this.target = new " + clazz.getSimpleName() + "();");

      for (Map.Entry<String, Property> entry : rootElement.getAttributes().entrySet())
      {
         String variable = "attributes.getValue(\"" + entry.getKey() + "\")";
         outputPropertySet(variable, entry.getValue());
      }

      // initialize collection element properties
      for (Property property : rootElement.getElements().values())
      {
         if (!Collection.class.isAssignableFrom(property.getType())) continue;
         Class collectionType = property.getType();
         if (collectionType.equals(List.class)) collectionType = ArrayList.class;
         else if (collectionType.equals(Set.class)) collectionType = HashSet.class;
         else if (collectionType.isInterface()) throw new RuntimeException("Illegal @XmlElement type");
         String variable = "new " + collectionType.getName() + "()";
         writer.println("      this.target." + property.getSetter().getName() + "(" + variable + ");");
      }


      writer.println("      top.getCurrent().push(this);");
      writer.println("   }");
   }

   public void writeCharacters()
   {
      writer.println("   @Override");
      writer.println("   public void characters(char[] ch, int start, int length) throws SAXException {");
      writer.println("      tempVal = new String(ch,start,length);");
      writer.println("   }");
   }

   public boolean isRootElement(Class type)
   {
      return introspector.getRootElements().containsKey(type);
   }

   public void writeAddMethod()
   {
      writer.println("   public void add(Object obj) throws SAXException");
      writer.println("   {");
      boolean first = true;
      for (Property property : rootElement.getElements().values())
      {
         if (!Collection.class.isAssignableFrom(property.getType()))
         {
            continue;
         }
         if (!isRootElement(property.getBaseType())) continue;
         writer.print("      ");
         if (first) first = false;
         else writer.print("else ");
         writer.println("if (state == State." + property.getName().toUpperCase() + ")");
         writer.println("      {");
         writer.println("         this.target." + property.getGetter().getName() +
                 "().add((" + property.getBaseType().getName() + ")obj);");
         writer.println("      }");

      }
      if (!first) writer.println("      else throw new SAXException(\"Unknown state for adding a child\");");
      writer.println("   }");
   }

   public void writeStartElement()
   {
      writer.println("   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException");
      writer.println("   {");
      boolean first = true;
      for (Map.Entry<String, Property> entry : rootElement.getElements().entrySet())
      {
         writer.print("      ");
         if (first) first = false;
         else writer.print("else ");
         writer.println("if (qName.equalsIgnoreCase(\"" + entry.getKey() + "\"))");
         writer.println("      {");
         writer.println("         this.state = State." + entry.getValue().getName().toUpperCase() + ";");
         if (isRootElement(entry.getValue().getBaseType()))
         {
            String parser = generated(entry.getValue().getBaseType());
            writer.println("         " + parser + " parser = new " + parser + "();");
            writer.println("         parser.setTop(top);");
            writer.println("         parser.setParentCallback(this);");
            writer.println("         parser.start(attributes, qName);");
         }
         writer.println("      }");

      }
      if (!first)
      {
         writer.println("      else");
         writer.println("      {");
         writer.println("         throw new SAXException(\"Unknown elemement: \" + qName);");
         writer.println("      }");
      }
      writer.println("   }");
   }

   public void writeEndElement()
   {
      writer.println("   public void endElement(String uri, String localName, String qName) throws SAXException");
      writer.println("   {");
      writer.println("      if (state == null && qName.equalsIgnoreCase(this.qName))");
      writer.println("      {");
      writer.println("         handler.add(this.target);");
      writer.println("         handler.endChild();");
      writer.println("         this.target = null;");
      writer.println("         this.state = null;");
      writer.println("         this.tempVal = null;");
      writer.println("         top.getCurrent().pop();");
      writer.println("         return;");
      writer.println("      }");
      boolean first = true;
      for (Property property : rootElement.getElements().values())
      {
         if (isRootElement(property.getBaseType())) continue;
         writer.print("      ");
         if (first) first = false;
         else writer.print("else ");
         writer.println("if (state == State." + property.getName().toUpperCase() + ")");
         writer.println("      {");
         writer.  print("         ");
         outputPropertySet("tempVal", property);
         writer.println("      }");
      }
      if (!first)
      {
         writer.println("      else");
         writer.println("      {");
         writer.println("         throw new SAXException(\"Unknown end elemement: \" + qName);");
         writer.println("      }");
      }
      writer.println("      state = null;");
      writer.println("   }");
   }
}
