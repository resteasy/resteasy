package org.jboss.resteasy.plugins.stats;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "registry")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistryData
{
   @XmlElementRef
   private List<RegistryEntry> entries = new ArrayList<RegistryEntry>();

   public List<RegistryEntry> getEntries()
   {
      return entries;
   }
}
