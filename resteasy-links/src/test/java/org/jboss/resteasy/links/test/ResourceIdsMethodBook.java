package org.jboss.resteasy.links.test;

import jakarta.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.links.ResourceIDs;

@XmlRootElement
@ResourceIDs({ "namea", "nameb" })
public class ResourceIdsMethodBook extends IdBook {

    private String _namea;
    private String _nameb;

    public ResourceIdsMethodBook() {
    }

    public ResourceIdsMethodBook(final String namea, final String nameb) {
        this._namea = namea;
        this._nameb = nameb;
    }

    public String getNamea() {
        return _namea;
    }

    public void setNamea(String namea) {
        this._namea = namea;
    }

    public String getNameb() {
        return _nameb;
    }

    public void setNameb(String nameb) {
        this._nameb = nameb;
    }

}
