package org.jboss.fastjaxb.generated.basic;


import org.jboss.fastjaxb.spi.Handler;
import org.jboss.fastjaxb.spi.ParentCallback;
import org.jboss.fastjaxb.spi.Sax;
import org.jboss.fastjaxb.test.basic.PhoneNumber;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PhoneNumber_Parser extends DefaultHandler implements ParentCallback, Handler
{

   private static enum State
   {
      NUMBER
   }

   protected Sax top;
   protected ParentCallback handler;
   protected String tempVal;
   protected State state;
   protected String qName;
   protected PhoneNumber target;

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
      return new PhoneNumber_Parser();
   }

   public void endChild()
   {
      state = null;
   }

   public void start(Attributes attributes, String qName)
   {
      this.qName = qName;
      this.target = new PhoneNumber();
      top.getCurrent().push(this);
   }

   public void add(Object obj) throws SAXException
   {
   }

   @Override
   public void characters(char[] ch, int start, int length) throws SAXException
   {
      tempVal = new String(ch, start, length);
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
   {
      if (qName.equalsIgnoreCase("number"))
      {
         this.state = State.NUMBER;
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
      if (state == State.NUMBER)
      {
         if (tempVal != null)
         {
            this.target.setNumber(tempVal);
         }
      }
      else
      {
         throw new SAXException("Unknown end elemement: " + qName);
      }
      state = null;
      tempVal = null;
   }
}
