package org.jboss.fastjaxb.generated.basic;


import org.jboss.fastjaxb.template.Sax;
import java.lang.String;
import org.jboss.fastjaxb.test.basic.Address;
import org.xml.sax.Attributes;
import org.jboss.fastjaxb.template.ParentCallback;
import org.jboss.fastjaxb.template.Handler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;

public class Address_Parser extends DefaultHandler implements ParentCallback, Handler
{

   private static enum State
   {
       STREET,
       CITY
   }

   protected Sax top;
   protected ParentCallback handler;
   protected String tempVal;
   protected State state;
   protected String qName;
   protected Address target;

   public void setTop(Sax top)
   {
      this.top = top;
   }

   public void setParentCallback(ParentCallback callback)
   {
      handler = callback;
   }

   public Handler newInstance()
   {
      return new Address_Parser();
   }

   public void endChild()
   {
      state = null;
   }

   public void start(Attributes attributes, String qName)
   {
      this.qName = qName;
      this.target = new Address();
      top.getCurrent().push(this);
   }

   public void add(Object obj) throws SAXException
   {
   }

   @Override
   public void characters(char[] ch, int start, int length) throws SAXException {
      tempVal = new String(ch,start,length);
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
   {
      if (qName.equalsIgnoreCase("street"))
      {
         this.state = State.STREET;
      }
      else if (qName.equalsIgnoreCase("city"))
      {
         this.state = State.CITY;
      }
      else
      {
         throw new SAXException("Unknown elemement: " + qName);
      }
   }

   public void endElement(String uri, String localName, String qName) throws SAXException
   {
      if (state == null && qName.equalsIgnoreCase(this.qName))
      {
         handler.add(this.target);
         handler.endChild();
         this.target = null;
         this.state = null;
         this.tempVal = null;
         top.getCurrent().pop();
         return;
      }
      if (state == State.STREET)
      {
               this.target.setStreet(tempVal);
      }
      else if (state == State.CITY)
      {
               this.target.setCity(tempVal);
      }
      else
      {
         throw new SAXException("Unknown end elemement: " + qName);
      }
      state = null;
   }
}
