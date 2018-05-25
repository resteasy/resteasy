package org.jboss.resteasy.links;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.ArrayList;

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
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class RESTServiceDiscovery extends ArrayList<RESTServiceDiscovery.AtomLink>{

	/**
	 * An Atom link
	 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
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
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((href == null) ? 0 : href.hashCode());
			result = prime * result + ((hreflang == null) ? 0 : hreflang.hashCode());
			result = prime * result + ((length == null) ? 0 : length.hashCode());
			result = prime * result + ((rel == null) ? 0 : rel.hashCode());
			result = prime * result + ((title == null) ? 0 : title.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AtomLink other = (AtomLink) obj;
			if (href == null) {
				if (other.href != null)
					return false;
			} else if (!href.equals(other.href))
				return false;
			if (hreflang == null) {
				if (other.hreflang != null)
					return false;
			} else if (!hreflang.equals(other.hreflang))
				return false;
			if (length == null) {
				if (other.length != null)
					return false;
			} else if (!length.equals(other.length))
				return false;
			if (rel == null) {
				if (other.rel != null)
					return false;
			} else if (!rel.equals(other.rel))
				return false;
			if (title == null) {
				if (other.title != null)
					return false;
			} else if (!title.equals(other.title))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}
	}

	public void addLink(URI uri, String rel) {
		AtomLink link = new AtomLink(uri.toString(), rel);
		if (!contains(link)) {
			add(link);
		}
	}
	
	public AtomLink getLinkForRel(String rel){
		for(AtomLink link : this){
			if(rel.equals(link.getRel()))
				return link;
		}
		return null;
	}
}
