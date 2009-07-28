package org.jboss.fastjaxb.spi;

import org.xml.sax.Attributes;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 24, 2009
 * Time: 11:46:39 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Handler extends org.xml.sax.EntityResolver, org.xml.sax.DTDHandler, org.xml.sax.ContentHandler, org.xml.sax.ErrorHandler
{
   void setTop(Sax top);

   void setParentCallback(ParentCallback callback);

   void start(Attributes attributes, String qName);

   Handler newInstance();
}
