package org.jboss.resteasy.plugins.stats;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 2 $
 */
@XmlRootElement(name = "registry")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistryData
{
   @XmlElementRef
   private Set<RegistryEntry> entries = new TreeSet<RegistryEntry>();

   public Set<RegistryEntry> getEntries()
   {
      return entries;
   }
}
