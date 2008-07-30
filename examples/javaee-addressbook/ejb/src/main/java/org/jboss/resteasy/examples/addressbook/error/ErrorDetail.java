/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.examples.addressbook.error;

import java.io.Serializable;
import java.util.Date;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A ErrorDetail.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "errorDetail")
public class ErrorDetail implements Serializable
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -4021424032960635518L;

   private String message;
   
   @XmlAttribute
   private Status status;
   
   @XmlAttribute
   private Date timeStamp = new Date();

   private DebugInfo debugInfo;
   
   
   private ErrorDetail() {
      
   }
   
   public static ErrorDetail create(Exception exception, Status status) {
      ErrorDetail errorDetail = new ErrorDetail();
      errorDetail.setStatus(status);
      errorDetail.setMessage(exception.getMessage());
      DebugInfo debug = new DebugInfo();
      debug.setStackTrace(exception.getStackTrace());
      errorDetail.setDebugInfo(debug);
      return errorDetail;
   }
   /**
    * Get the debugInfo.
    * 
    * @return the debugInfo.
    */
   public DebugInfo getDebugInfo()
   {
      return debugInfo;
   }

   /**
    * Set the debugInfo.
    * 
    * @param debugInfo The debugInfo to set.
    */
   public void setDebugInfo(DebugInfo debugInfo)
   {
      this.debugInfo = debugInfo;
   }

   /**
    * Get the message.
    * 
    * @return the message.
    */
   public String getMessage()
   {
      return message;
   }

   /**
    * Set the message.
    * 
    * @param message The message to set.
    */
   public void setMessage(String message)
   {
      this.message = message;
   }

   /**
    * Get the status.
    * 
    * @return the status.
    */
   public Status getStatus()
   {
      return status;
   }

   /**
    * Set the status.
    * 
    * @param status The status to set.
    */
   public void setStatus(Status status)
   {
      this.status = status;
   }

   /**
    * Get the timeStamp.
    * 
    * @return the timeStamp.
    */
   public Date getTimeStamp()
   {
      return timeStamp;
   }

   /**
    * Set the timeStamp.
    * 
    * @param timeStamp The timeStamp to set.
    */
   public void setTimeStamp(Date timeStamp)
   {
      this.timeStamp = timeStamp;
   }
   
   
}
