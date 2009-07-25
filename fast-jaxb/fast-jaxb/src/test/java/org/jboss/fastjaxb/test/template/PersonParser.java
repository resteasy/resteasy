package org.jboss.fastjaxb.test.template;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.jboss.fastjaxb.template.ParentCallback;
import org.jboss.fastjaxb.template.Handler;
import org.jboss.fastjaxb.template.Sax;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 24, 2009
 * Time: 10:04:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class PersonParser extends DefaultHandler implements ParentCallback, Handler
{
   protected Sax top;
   protected ParentCallback handler;
   protected String tempVal;
   protected Person person;
   protected State state;
   protected String qName;

   private static enum State
   {
      NAME,
      ADDRESSES,
      BUSINESSADDRESSES
   }

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
      return new PersonParser();
   }

   public void endChild()
   {
      state = null;
   }

   public void start(Attributes attributes, String qName)
   {
      this.qName = qName;
      person = new Person();
      person.setId(attributes.getValue("id"));
      person.setAddresses(new ArrayList<Address>());
      person.setBusinessAddresses(new ArrayList<Address>());
      top.getCurrent().push(this);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
   {
      if (qName.equalsIgnoreCase("name")) state = State.NAME;
      else if(qName.equalsIgnoreCase("address"))
      {
         state = State.ADDRESSES;
         AddressParser addressParser = new AddressParser();
         addressParser.setTop(top);
         addressParser.setParentCallback(this);
         addressParser.start(attributes, qName);
      }
      else if(qName.equalsIgnoreCase("business-address"))
      {
         state = State.BUSINESSADDRESSES;
         AddressParser addressParser = new AddressParser();
         addressParser.setTop(top);
         addressParser.setParentCallback(this);
         addressParser.start(attributes, qName);
      }
      else
      {
         throw new SAXException("Unknown elemement: " + qName);
      }

   }

   public void add(Object obj) throws SAXException
   {
      if (state == State.ADDRESSES)
      {
         person.getAddresses().add((Address)obj);
      }
      else if (state == State.BUSINESSADDRESSES)
      {
         person.getBusinessAddresses().add((Address)obj);
      }
      else throw new SAXException("Unknown state");
   }

   @Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch,start,length);
	}

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException
   {
      if (state == null && qName.equalsIgnoreCase(this.qName))
      {
         handler.add(person);
         person = null;
         state = null;
         tempVal = null;
         top.getCurrent().pop();
         return;
      }

      if (state == State.NAME)
      {
         person.setName(tempVal);
      }
      else
      {
         throw new SAXException("unknown end element: " + qName);
      }
      state = null;

   }


}
