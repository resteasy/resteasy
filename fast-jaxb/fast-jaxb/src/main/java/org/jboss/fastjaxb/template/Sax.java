package org.jboss.fastjaxb.template;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import java.util.Stack;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 24, 2009
 * Time: 10:01:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class Sax extends DefaultHandler implements ParentCallback
{
   protected Stack<Handler> current = new Stack<Handler>();
   protected Map<String, Handler> handlers = new HashMap<String, Handler>();
   protected Object root;

   public Sax() {}

   public Sax(Map<String, Handler> handlers)
   {
      this.handlers = handlers;
   }

   public Stack<Handler> getCurrent()
   {
      return current;
   }

   public void add(Object obj) throws SAXException
   {
      this.root = obj;
   }

   public void endChild()
   {
      
   }

   public Map<String, Handler> getHandlers()
   {
      return handlers;
   }

   public Object getRoot()
   {
      return root;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
   {
      Handler handler = null;
      if (!current.isEmpty()) handler = current.peek();
      if (handler != null)
      {
         handler.startElement(uri, localName, qName, attributes);
         return;
      }
      if (uri == null) uri = "";
      handler = handlers.get(uri);
      if (handler == null) throw new SAXException("Unable to find handler for namespace uri:" + uri + " l:" + localName + " q:" + qName);
      handler = handler.newInstance();
      handler.setTop(this);
      handler.setParentCallback(this);
      handler.start(attributes, qName);
   }

   public void characters(char[] ch, int start, int length) throws SAXException
   {
      Handler handler = null;
      if (!current.isEmpty()) handler = current.peek();
      if (handler != null)
      {
         handler.characters(ch, start, length);
         return;
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException
   {
      Handler handler = null;
      if (!current.isEmpty()) handler = current.peek();
      if (handler != null)
      {
         handler.endElement(uri, localName, qName);
         return;
      }
   }

   @Override
   public void endPrefixMapping(String s) throws SAXException
   {
      super.endPrefixMapping(s);
   }

   @Override
   public void startPrefixMapping(String s, String s1) throws SAXException
   {
      super.startPrefixMapping(s, s1);
   }
}
