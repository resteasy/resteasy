package org.jboss.resteasy.links.test;


public class IDServiceTestBean implements IDServiceTest {
	public JpaIdBook getJpaIdBook(String name){
		return new JpaIdBook(name);
	}

	public XmlIdBook getXmlIdBook(String name){
		return new XmlIdBook(name);
	}

	public ResourceIdBook getResourceIdBook(String name){
		return new ResourceIdBook(name);
	}

	public ResourceIdsBook getResourceIdsBook(String namea, String nameb){
		return new ResourceIdsBook(namea, nameb);
	}

	public ResourceIdMethodBook getResourceIdMethodBook(String name) {
		return new ResourceIdMethodBook(name);
	}

	public ResourceIdsMethodBook getResourceIdsMethodBook(String namea,
			String nameb) {
		return new ResourceIdsMethodBook(namea, nameb);
	}

}
