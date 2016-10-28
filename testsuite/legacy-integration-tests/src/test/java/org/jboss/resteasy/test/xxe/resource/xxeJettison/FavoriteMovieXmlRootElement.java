package org.jboss.resteasy.test.xxe.resource.xxeJettison;

import org.jboss.resteasy.annotations.providers.NoJackson;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@NoJackson
public class FavoriteMovieXmlRootElement {

    private String _title;

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }
}