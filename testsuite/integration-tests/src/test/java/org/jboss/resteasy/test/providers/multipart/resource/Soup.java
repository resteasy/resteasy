package org.jboss.resteasy.test.providers.multipart.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;

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
