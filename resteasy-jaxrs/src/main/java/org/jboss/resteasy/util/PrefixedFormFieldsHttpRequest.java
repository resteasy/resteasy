package org.jboss.resteasy.util;

import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.spi.HttpRequest;

public class PrefixedFormFieldsHttpRequest extends DelegatingHttpRequest {

	private final String prefix;

	public PrefixedFormFieldsHttpRequest(String prefix, HttpRequest request) {
		super(request);
		this.prefix = prefix;
      	}

	@Override
	public MultivaluedMap<String, String> getDecodedFormParameters() {
		return new PrefixedMultivaluedMap<String>(prefix, super.getDecodedFormParameters());
	}

   }