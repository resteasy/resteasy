package org.jboss.fastjaxb.template;

import org.xml.sax.SAXException;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 24, 2009
 * Time: 11:02:24 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ParentCallback
{
   void add(Object obj) throws SAXException;
   void endChild();
}
