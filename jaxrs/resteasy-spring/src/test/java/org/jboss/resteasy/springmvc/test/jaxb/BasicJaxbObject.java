package org.jboss.resteasy.springmvc.test.jaxb;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BasicJaxbObject {
	private String something;
	private Date someDate;

	
	public BasicJaxbObject() {
		super();
	}

	public BasicJaxbObject(String something, Date someDate) {
		super();
		this.something = something;
		this.someDate = someDate;
	}

	public String getSomething() {
		return something;
	}

	public void setSomething(String something) {
		this.something = something;
	}

	public Date getSomeDate() {
		return someDate;
	}

	public void setSomeDate(Date someDate) {
		this.someDate = someDate;
	}
}
