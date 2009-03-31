package org.jboss.resteasy.examples.flickr;

import java.util.List;
import java.util.concurrent.FutureTask;

import javax.swing.ImageIcon;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="rsp")
public class FlickrResponse {
	@XmlElementWrapper(name = "photos")
	public List<Photo> photo;
	@XmlTransient
	public String searchTerm;
}

class Photo {
	@XmlAttribute
	public String server, id, secret, title;
	@XmlTransient
	public FutureTask<ImageIcon> image;
}
