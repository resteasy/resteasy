package org.jboss.resteasy.test.providers.multipart.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "soup")
@XmlAccessorType(XmlAccessType.FIELD)
public class Soup {
    @XmlElement
    private String id;

    public Soup(){}
    public Soup(final String id){
        this.id = id;
    }
    public String getId(){
        return id;
    }
}
