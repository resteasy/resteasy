package org.jboss.fastjaxb.generated.basic;


import org.jboss.fastjaxb.template.Sax;
import java.util.List;
import org.jboss.fastjaxb.test.basic.Person;
import java.lang.String;
import org.xml.sax.Attributes;
import org.jboss.fastjaxb.template.ParentCallback;
import org.jboss.fastjaxb.template.Handler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;

public class Person_Parser extends DefaultHandler implements ParentCallback, Handler
{

   private static enum State
   {
       NAME,
       BUSINESSADDRESSES,
       ADDRESSES
   }

   protected Sax top;
   protected ParentCallback handler;
   protected String tempVal;
   protected State state;
   protected String qName;
   protected Person target;

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
      return new Person_Parser();
   }

   public void endChild()
   {
      state = null;
   }

   public void start(Attributes attributes, String qName)
   {
      this.qName = qName;
      this.target = new Person();
      this.target.setId(attributes.getValue("id"));
      this.target.setBusinessAddresses(new java.util.ArrayList());
      this.target.setAddresses(new java.util.ArrayList());
      top.getCurrent().push(this);
   }

   public void add(Object obj) throws SAXException
   {
      if (state == State.BUSINESSADDRESSES)
      {
         this.target.getBusinessAddresses().add((org.jboss.fastjaxb.test.basic.Address)obj);
      }
      else if (state == State.ADDRESSES)
      {
         this.target.getAddresses().add((org.jboss.fastjaxb.test.basic.Address)obj);
      }
      else throw new SAXException("Unknown state for adding a child");
   }

   @Override
   public void characters(char[] ch, int start, int length) throws SAXException {
      tempVal = new String(ch,start,length);
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
   {
      if (qName.equalsIgnoreCase("name"))
      {
         this.state = State.NAME;
      }
      else if (qName.equalsIgnoreCase("business-address"))
      {
         this.state = State.BUSINESSADDRESSES;
         Address_Parser parser = new Address_Parser();
         parser.setTop(top);
         parser.setParentCallback(this);
         parser.start(attributes, qName);
      }
      else if (qName.equalsIgnoreCase("address"))
      {
         this.state = State.ADDRESSES;
         Address_Parser parser = new Address_Parser();
         parser.setTop(top);
         parser.setParentCallback(this);
         parser.start(attributes, qName);
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
      if (state == State.NAME)
      {
               this.target.setName(tempVal);
      }
      else
      {
         throw new SAXException("Unknown end elemement: " + qName);
      }
      state = null;
   }
}
