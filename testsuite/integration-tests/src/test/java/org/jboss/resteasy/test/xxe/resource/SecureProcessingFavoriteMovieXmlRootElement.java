package org.jboss.resteasy.test.xxe.resource;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SecureProcessingFavoriteMovieXmlRootElement {
    private String _title;

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }
}
