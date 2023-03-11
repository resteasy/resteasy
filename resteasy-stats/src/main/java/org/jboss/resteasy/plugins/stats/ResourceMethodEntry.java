package org.jboss.resteasy.plugins.stats;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ GetResourceMethod.class, DeleteResourceMethod.class, HeadResourceMethod.class, OptionsResourceMethod.class,
        PostResourceMethod.class, PutResourceMethod.class, TraceResourceMethod.class })
public class ResourceMethodEntry {
    @XmlAttribute(name = "class")
    private String clazz;

    @XmlAttribute
    private String method;

    @XmlAttribute
    private long invocations;

    @XmlElement
    private List<String> produces = new ArrayList<String>();

    @XmlElement
    private List<String> consumes = new ArrayList<String>();

    public String getClazz() {
        return clazz;
    }

    public String getMethod() {
        return method;
    }

    public long getInvocations() {
        return invocations;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setInvocations(long invocations) {
        this.invocations = invocations;
    }

    public List<String> getProduces() {
        return produces;
    }

    public List<String> getConsumes() {
        return consumes;
    }
}
