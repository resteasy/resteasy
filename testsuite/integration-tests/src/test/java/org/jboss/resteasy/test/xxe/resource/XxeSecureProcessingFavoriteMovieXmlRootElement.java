package org.jboss.resteasy.test.xxe.resource;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class XxeSecureProcessingFavoriteMovieXmlRootElement {
    private String _title;

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }
}
