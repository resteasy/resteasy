package org.jboss.resteasy.plugins.providers.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "collection", namespace = "http://jboss.org/resteasy")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbCollection
{
   @XmlAnyElement
   private List<Object> value = new ArrayList<Object>();

   public List<Object> getValue()
   {
      return value;
   }
}
