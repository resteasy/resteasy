/**
 *
 */
package com.damnhandy.resteasy.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.damnhandy.resteasy.annotations.WebResource;
import com.damnhandy.resteasy.annotations.WebResources;
import com.damnhandy.resteasy.scanner.WebResourceScanner;

/**
 * @author ryan
 *
 */
public class ResourceDispatcher {
	private static final Logger logger = Logger.getLogger(ResourceDispatcher.class);
    private static final ResourceDispatcher instance = new ResourceDispatcher();
    /**
     *
     */
    private Map<PatternKey,ResourceInvoker> routes =
            new ConcurrentHashMap<PatternKey,ResourceInvoker>();

    
    private ResourceDispatcher() {
       
    }
    
    public static ResourceDispatcher getInstance() {
        return instance;
    }
    
    /**
     *
     * @param collection
     */
    public void init() {
    	Set<Class<Object>> scannedClasses = new HashSet<Class<Object>>();
        scannedClasses.addAll( new WebResourceScanner("resteasy.properties").getClasses() );
        scannedClasses.addAll( new WebResourceScanner("META-INF/resteasy.properties").getClasses() );
        scannedClasses.addAll( new WebResourceScanner("WEB-INF/resteasy.properties").getClasses() );
    	for(Class<Object> resourceClass : scannedClasses) {
    		/*
    		 * Process a Class that can respond to multiple WebReosurces
    		 */
    		if(resourceClass.isAnnotationPresent(WebResources.class)) {
    			logger.info("Found WebResources Collection: "+resourceClass.getSimpleName());
    			WebResource[] resources = resourceClass.getAnnotation(WebResources.class).resources();
    			for(int i = 0; i < resources.length; i++) {
                    ResourceInvoker invoker = ResourceInvokerBuilder.createResourceInvoker(resourceClass,resources[i]);
                    routes.put(invoker.getPatternKey(), invoker);
    			}
    		} 
    		/*
    		 * Process a singular WebResource
    		 */
    		else if(resourceClass.isAnnotationPresent(WebResource.class)) {
    			logger.info("Found WebResource: "+resourceClass.getSimpleName());
    			WebResource resource = resourceClass.getAnnotation(WebResource.class);
                ResourceInvoker invoker = ResourceInvokerBuilder.createResourceInvoker(resourceClass,resource);
                routes.put(invoker.getPatternKey(), invoker);
    		} 
    		/*
    		 * Process a RESTful Entity Bean
    		 *
    		else if(resourceClass.isAnnotationPresent(RestfulEntity.class)) {
    			logger.info("Found RestfulEntity: "+resourceClass.getSimpleName());
    			RestfulEntity restfulEntity = resourceClass.getAnnotation(RestfulEntity.class);
                
    			ResourceInvoker baseURIInvoker = 
                	ResourceInvokerBuilder.createEntityResourceInvoker(resourceClass, 
                			restfulEntity.resourceManager(),
                			"baseURI",
                			restfulEntity.baseURI());
                routes.put(baseURIInvoker.getPatternKey(), baseURIInvoker);
                
                ResourceInvoker targetURIInvoker = 
                	ResourceInvokerBuilder.createEntityResourceInvoker(resourceClass, 
                			restfulEntity.resourceManager(),
                			"targetURIInvoker",
                			restfulEntity.instanceURI());
                routes.put(targetURIInvoker.getPatternKey(), targetURIInvoker);
    		}*/
        }
    }

    
    /**
     * Finds the resource which matches this path fragment
     * @param url
     * @return the ResourceInvoker for the give path or null if one is not found.
     */
    public ResourceInvoker findResourceInvoker(String path) {
        for(PatternKey key : routes.keySet()) {
            if(key.matches(path)) {
                return routes.get(key);
            }
        }
        return null;
    }
}
