package org.jboss.resteasy.links;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * This holds a list of atom links describing the REST service discovered. This will
 * be injected by RESTEasy on any entity in the response if the JAX-RS method was
 * annotated with {@link AddLinks @AddLinks} if your entity declares a field of this
 * type.
 * </p>
 * <p>
 * For this to work you need to add {@link LinkResource @LinkResource} annotations on
 * all the JAX-RS methods you want to be discovered.
 * </p>
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class RESTServiceDiscovery extends ArrayList<RESTServiceDiscovery.AtomLink>{

	/**
	 * An Atom link
	 * @author Stéphane Épardaud <stef@epardaud.fr>
	 */
	@XmlRootElement(name = "link", namespace = "http://www.w3.org/2005/Atom")
	@XmlAccessorType(XmlAccessType.NONE)
	public static class AtomLink {
		@XmlAttribute
		String rel;
		@XmlAttribute
		String href;
		@XmlAttribute
		String type;
		@XmlAttribute
		String hreflang;
		@XmlAttribute
		String title;
		@XmlAttribute
		String length;

		public AtomLink() {
		}

		public AtomLink(String href, String rel) {
			this.href = href;
			this.rel = rel;
		}

		public String getRel() {
			return rel;
		}

		public void setRel(String rel) {
			this.rel = rel;
		}

		public String getHref() {
			return href;
		}

		public void setHref(String href) {
			this.href = href;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getHreflang() {
			return hreflang;
		}

		public void setHreflang(String hreflang) {
			this.hreflang = hreflang;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getLength() {
			return length;
		}

		public void setLength(String length) {
			this.length = length;
		}
	}

	public void addLink(URI uri, String rel) {
		add(new AtomLink(uri.toString(), rel));
	}
	
	public AtomLink getLinkForRel(String rel){
		for(AtomLink link : this){
			if(rel.equals(link.getRel()))
				return link;
		}
		return null;
	}
}
