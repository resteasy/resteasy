/**
 * 
 */
package com.damnhandy.resteasy.handler;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.damnhandy.resteasy.common.MediaTypeScanner;

/**
 * A Factory class that is resposible for managing the different RepresentationHandlers
 * @author Ryan J. McDonough
 * @since 1.0
 * 
 */
public class RepresentationHandlerFactory {
	private static final Logger logger = Logger.getLogger(RepresentationHandlerFactory.class);
	/**
	 * 
	 */
	private static RepresentationHandlerFactory instance;
	/**
	 * 
	 */
	private Map<Integer,RepresentationHandler> handlerMap = new HashMap<Integer,RepresentationHandler>();
	
	/**
	 * 
	 */
	private Map<String,Integer> mimeTypes = new HashMap<String,Integer>();
	
	/**
	 * 
	 */
	private Map<String,Integer> extention = new HashMap<String,Integer>();

	
	/**
	 * 
	 * @return
	 */
	public static synchronized RepresentationHandlerFactory instance() {
		if(instance == null) {
			instance = new RepresentationHandlerFactory();
			instance.loadAnnotatedRepresentationHandlers();
		}
		return instance;
	}
	/**
	 * 
	 * @param mimeType
	 * @return
	 */
	public RepresentationHandler getHandlerByMimeType(String mimeType) {
		Integer position = mimeTypes.get(mimeType);
		return handlerMap.get(position);
	}
	
	/**
	 * 
	 * @param ext
	 * @return
	 */
	public RepresentationHandler getHandlerByExtention(String ext) {
		Integer position = extention.get(ext);
		return handlerMap.get(position);
	}

	/**
	 * 
	 * @param collection
	 */
	public void loadAnnotatedRepresentationHandlers() {
		Set<Class<Object>> scannedClasses = new LinkedHashSet<Class<Object>>();
		scannedClasses.addAll(new MediaTypeScanner("resteasy.properties").getClasses());
		scannedClasses.addAll(new MediaTypeScanner("META-INF/resteasy.properties").getClasses());
		scannedClasses.addAll(new MediaTypeScanner("resteasy.cfg.xml").getClasses());
		scannedClasses.addAll(new MediaTypeScanner("META-INF/resteasy.cfg.xml").getClasses());
		scannedClasses.addAll(new MediaTypeScanner("WEB-INF/resteasy.cfg.xml").getClasses());
		int counter = 0;
		try {
			for (Class<Object> handlerClass : scannedClasses) {
				if (handlerClass.isAnnotationPresent(MediaTypes.class)) {
					MediaType[] resources = handlerClass.getAnnotation(MediaTypes.class).types();
					int position = this.initializeHandlerInstance(handlerClass, counter++);
					for (int i = 0; i < resources.length; i++) {
						MediaType mediaType = resources[i];
						mapHandlerTypes(position,mediaType);
					}
				} else {
					MediaType mediaType = handlerClass.getAnnotation(MediaType.class);
					int position = this.initializeHandlerInstance(handlerClass, counter++);
					mapHandlerTypes(position,mediaType);
				}
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Initialized "+handlerMap.size()+" handlers");
	}
	
	/**
	 * 
	 * @param position
	 * @param mediaType
	 */
	private void mapHandlerTypes(int position,MediaType mediaType) {
		mimeTypes.put(mediaType.type(), position);
		String[] extentions = mediaType.extentions();
		for(int e = 0; e < extentions.length; e++) {
			extention.put(extentions[e], position);
		}
	}

	/**
	 * 
	 * @param handlerClass
	 * @param handlerList
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private int initializeHandlerInstance(Class handlerClass,Integer counter) 
		throws InstantiationException, IllegalAccessException {
		RepresentationHandler handler =  (RepresentationHandler) handlerClass.newInstance();
		handlerMap.put(counter, handler);
		logger.info("Loaded RepresentationHandler: "+ handlerClass.getSimpleName());
		return counter;
	}
}
