package org.jboss.resteasy.links.impl;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.links.AddLinks;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.DecoratorProcessor;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Marshaller.Listener;
import java.lang.annotation.Annotation;

public class LinkDecorator implements DecoratorProcessor<Marshaller, AddLinks> {

	public Marshaller decorate(Marshaller target, final AddLinks annotation,
			Class type, Annotation[] annotations, MediaType mediaType) {
		target.setListener(new Listener() {
			@Override
			public void beforeMarshal(Object entity) {
				UriInfo uriInfo = ResteasyProviderFactory.getContextData(UriInfo.class);
				ResourceMethodRegistry registry = (ResourceMethodRegistry) ResteasyProviderFactory.getContextData(Registry.class);

				// find all rest service classes and scan them
				RESTUtils.addDiscovery(entity, uriInfo, registry);
			}
		});
		return target;
	}
}
