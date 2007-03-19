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
import com.damnhandy.resteasy.handler.JAXBRepresentationHandler;
import com.damnhandy.resteasy.handler.PlainTextRepresentationHandler;
import com.damnhandy.resteasy.handler.RepresentationHandler;
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
    /**
     *
     */
    private Map<String,RepresentationHandler> handlers =
            new ConcurrentHashMap<String,RepresentationHandler>();
    
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
    	for(Class<Object> resourceClass : scannedClasses) {
    		if(resourceClass.isAnnotationPresent(WebResources.class)) {
    			logger.info("Found Resource Collection: "+resourceClass.getSimpleName());
    			WebResource[] resources = resourceClass.getAnnotation(WebResources.class).resources();
    			for(int i = 0; i < resources.length; i++) {
                    ResourceInvoker invoker = ResourceInvokerBuilder.createResourceInvoker(resourceClass,resources[i]);
                    routes.put(invoker.getPatternKey(), invoker);
    			}
    		} else {
    			logger.info("Found Resource: "+resourceClass.getSimpleName());
    			WebResource resource = resourceClass.getAnnotation(WebResource.class);
                ResourceInvoker invoker = ResourceInvokerBuilder.createResourceInvoker(resourceClass,resource);
                routes.put(invoker.getPatternKey(), invoker);
    		}
        }
        handlers.put("application/xml",new JAXBRepresentationHandler());
        handlers.put("text/plain",new PlainTextRepresentationHandler());
    }
    
    
    /**
     * 
     * @param mediaType
     * @return
     */
    private RepresentationHandler findHandler(String mediaType) {
        if(mediaType == null) {
        	mediaType = "application/xml";
        }
    	RepresentationHandler handler = handlers.get(mediaType);
        if(handler == null) {
            handler = handlers.get("application/xml");
        }
        return handler;
    }
    
    /**
     *
     */
    public static RepresentationHandler findRepresentationHandler(String mediaType) {
        return getInstance().findHandler(mediaType);
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
