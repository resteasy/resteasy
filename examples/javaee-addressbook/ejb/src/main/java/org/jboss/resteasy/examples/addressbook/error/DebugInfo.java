/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.error;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * A DebugInfo.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "debugInfo")
public class DebugInfo
{

   @XmlElementWrapper(name = "stackTrace")
   @XmlElement(name = "stackTraceElement", required = true, nillable = true)
   @XmlJavaTypeAdapter(StackTraceAdapter.class)
   private StackTraceElement[] stackTrace;

   /**
    * Get the stackTrace.
    * 
    * @return the stackTrace.
    */
   public StackTraceElement[] getStackTrace()
   {
      return stackTrace;
   }

   /**
    * Set the stackTrace.
    * 
    * @param stackTrace The stackTrace to set.
    */
   public void setStackTrace(StackTraceElement[] stackTrace)
   {
      this.stackTrace = stackTrace;
   }
}
