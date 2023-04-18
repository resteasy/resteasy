package org.jboss.resteasy.test.cdi.basic.resource;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "xformat")
public class BeanReaderWriterXFormat {
    protected String id;
    protected String bean;

    public BeanReaderWriterXFormat() {
    }

    public BeanReaderWriterXFormat(final String id) {
        this.id = id;
    }

    public BeanReaderWriterXFormat(final String id, final String bean) {
        this.id = id;
        this.bean = bean;
    }

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute
    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }
}
