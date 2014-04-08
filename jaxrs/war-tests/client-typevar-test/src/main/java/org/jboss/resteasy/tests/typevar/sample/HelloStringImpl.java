package org.jboss.resteasy.tests.typevar.sample;

public class HelloStringImpl implements HelloString {

	@Override
	public String sayHi(String in) {
		
		return in;
	}

}
