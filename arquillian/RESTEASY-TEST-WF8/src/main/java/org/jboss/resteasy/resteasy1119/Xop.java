package org.jboss.resteasy.resteasy1119;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 10, 2015
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Xop
{
  @XmlMimeType(MediaType.APPLICATION_OCTET_STREAM)
  private byte[] bytes;

  public Xop(byte[] bytes)
  {
     this.bytes = bytes;
  }
  
  public Xop()
  {
  }
  
  public byte[] getBytes()
  {
     return bytes;
  }
  
  public void setBytes(byte[] bytes)
  {
     this.bytes = bytes;
  }
}
