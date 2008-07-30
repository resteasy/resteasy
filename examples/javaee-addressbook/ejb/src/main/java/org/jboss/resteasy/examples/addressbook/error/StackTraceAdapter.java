/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.error;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * A StackTraceAdapter.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class StackTraceAdapter extends XmlAdapter<String, StackTraceElement>
{

   @Override
   public String marshal(StackTraceElement stackTrace) throws Exception
   {
      // FIXME marshal
      return stackTrace.toString();
   }

   @Override
   public StackTraceElement unmarshal(String v) throws Exception
   {
      throw new UnsupportedOperationException("");
   }

}
