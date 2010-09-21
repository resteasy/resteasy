package org.jboss.resteasy.jsapi;

import java.lang.reflect.Method;

import org.jboss.resteasy.core.ResourceLocator;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocatorMetaData {
	
	private final static Logger logger = LoggerFactory.getLogger(LocatorMetaData.class);

	private LocatorMetaData parent;
	private ResourceLocator resourceLocator;
	private ResourceMethodRegistry registry;
	
	public LocatorMetaData(ResourceLocator locator, ResourceMethodRegistry registry) {
		this.resourceLocator = locator;
		this.registry = registry;
		
	}
	
}
