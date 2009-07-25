package org.jboss.fastjaxb.test.template;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.jboss.fastjaxb.template.Handler;
import org.jboss.fastjaxb.template.Sax;
import org.jboss.fastjaxb.template.ParentCallback;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 24, 2009
 * Time: 10:48:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class AddressParser extends DefaultHandler implements Handler
{

   private static enum State
   {
      STREET,
      CITY
   }

   protected Sax top;
   protected String tempVal;
   protected Address address;
   protected State state;
   protected ParentCallback handler;
   protected String qName;

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
      return new AddressParser();
   }

   public void start(Attributes attributes, String qName)
   {
      this.qName = qName;
      address = new Address();
      top.getCurrent().push(this);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
   {
      if (qName.equalsIgnoreCase("street")) state = State.STREET;
      else if(qName.equalsIgnoreCase("city")) state = State.CITY;
   }

   @Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch,start,length);
	}

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException
   {
      if (state == null && qName.equalsIgnoreCase(qName))
      {
         handler.add(address);
         handler.endChild();
         top.getCurrent().pop();
         return;
      }
      if (state == State.STREET)
      {
         address.setStreet(tempVal);
      }
      else if (state == State.CITY) {
         address.setCity(tempVal);
      }
      else
      {
         throw new SAXException("unknown end element: " + qName);
      }
      state = null;
   }
}
