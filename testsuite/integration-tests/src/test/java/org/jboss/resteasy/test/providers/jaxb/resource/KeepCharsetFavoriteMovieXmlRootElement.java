package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class KeepCharsetFavoriteMovieXmlRootElement {
    private String _title;

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }
}
