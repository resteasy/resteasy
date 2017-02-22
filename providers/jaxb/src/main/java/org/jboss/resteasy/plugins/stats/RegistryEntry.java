package org.jboss.resteasy.plugins.stats;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * <resource uriTemplate="/foo/bar{fff:.*}/x/a">
 *     <get method="org.blah.MyResource.method()" invocations="5555">
 *         <produces>application/xml</produces>
 *         <produces>application/json</produces>
 *     </get>
 *     <post method="org.blah.MyResource.post()">
 *         <produces>application/xml</produces>
 *     </post>
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 2 $
 */
@XmlRootElement(name = "resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistryEntry implements Comparable<RegistryEntry>
{
   @XmlAttribute
   private String uriTemplate;

   @XmlElementRef
   private List<ResourceMethodEntry> methods = new ArrayList<ResourceMethodEntry>();

   @XmlElementRef
   private SubresourceLocator locator;

   public String getUriTemplate()
   {
      return uriTemplate;
   }

   public SubresourceLocator getLocator()
   {
      return locator;
   }

   public void setLocator(SubresourceLocator locator)
   {
      this.locator = locator;
   }

   public void setUriTemplate(String uriTemplate)
   {
      this.uriTemplate = uriTemplate;
   }

   public List<ResourceMethodEntry> getMethods()
   {
      return methods;
   }

    @Override
    public int compareTo(RegistryEntry o)
    {
        if (this.getUriTemplate() == null)
            return o.getUriTemplate() == null ? 0 : 1;

        return this.getUriTemplate().compareTo(o.getUriTemplate());
    }
}
