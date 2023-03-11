package org.jboss.resteasy.plugins.stats;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * <pre>
 * {@literal <}resource uriTemplate="/foo/bar{fff:.*}/x/a"{@literal >}
 *     {@literal <}get method="org.blah.MyResource.method()" invocations="5555"{@literal >}
 *         {@literal <}produces{@literal >}application/xml{@literal <}/produces{@literal >}
 *         {@literal <}produces{@literal >}application/json{@literal <}/produces{@literal >}
 *     {@literal <}/get{@literal >}
 *     {@literal <}post method="org.blah.MyResource.post()"{@literal >}
 *         {@literal <}produces{@literal >}application/xml{@literal <}/produces{@literal >}
 *     {@literal <}/post{@literal >}
 * {@literal <}/resource{@literal >}
 * </pre>
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 2 $
 */
@XmlRootElement(name = "resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistryEntry implements Comparable<RegistryEntry> {
    @XmlAttribute
    private String uriTemplate;

    @XmlElementRef
    private List<ResourceMethodEntry> methods = new ArrayList<ResourceMethodEntry>();

    @XmlElementRef
    private SubresourceLocator locator;

    public String getUriTemplate() {
        return uriTemplate;
    }

    public SubresourceLocator getLocator() {
        return locator;
    }

    public void setLocator(SubresourceLocator locator) {
        this.locator = locator;
    }

    public void setUriTemplate(String uriTemplate) {
        this.uriTemplate = uriTemplate;
    }

    public List<ResourceMethodEntry> getMethods() {
        return methods;
    }

    @Override
    public int compareTo(RegistryEntry o) {
        if (this.getUriTemplate() == null)
            return o.getUriTemplate() == null ? 0 : 1;

        return this.getUriTemplate().compareTo(o.getUriTemplate());
    }
}
