package org.jboss.resteasy.examples.flickr;

import java.util.List;
import java.util.concurrent.FutureTask;

import javax.swing.ImageIcon;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "rsp")
public class FlickrResponse {
	@XmlElementWrapper(name = "photos")
	public List<Photo> photo;
}

class Photo {
	@XmlAttribute
	public String server, id, secret, title, owner;
	@XmlTransient
	public FutureTask<ImageIcon> image;

	public String getPublicURL() {
		return UriBuilder.fromUri("http://www.flickr.com/photos/").path(
				"/{owner}/{id}").build(owner, id).toString();
	}
}
