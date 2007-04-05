package com.damnhandy.resteasy.core;

import com.damnhandy.resteasy.annotations.HttpMethod;
import com.damnhandy.resteasy.annotations.RepresentationIn;
import com.damnhandy.resteasy.annotations.RepresentationOut;
import com.damnhandy.resteasy.annotations.Type;
import com.damnhandy.resteasy.annotations.URIParam;
import com.damnhandy.resteasy.annotations.WebResource;
import com.damnhandy.resteasy.representation.Representation;

@WebResource(value="/foo/{fooId}")
@RepresentationIn("application/xml")
@RepresentationOut("application/xml")
public class DummyResource {

	@HttpMethod(HttpMethod.GET)
	public Representation<Object> findFoo(@URIParam("fooId") Long id) {
		return null;
	}

	@HttpMethod(HttpMethod.GET)
	@RepresentationOut("application/json")
	public Representation<Object> findFooAsJSON(@URIParam("fooId") Long id) {
		return null;
	}
	
	
	@HttpMethod(HttpMethod.POST)
	public Representation<Object> updateFoo(@URIParam("fooId") Long id,@Type(Object.class) Representation<Object> foo) {
		return null;
	}
	
	@HttpMethod(HttpMethod.PUT)
	public Representation<Object> createFoo(@Type(Object.class) Representation<Object> foo, @URIParam("fooId") Long id) {
		return null;
	}


}
