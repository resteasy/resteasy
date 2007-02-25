/**
 * 
 */
package com.damnhandy.resteasy.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.Local;
import javax.ejb.MessageDriven;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.damnhandy.resteasy.ConfigurationException;
import com.damnhandy.resteasy.annotations.HttpMethod;
import com.damnhandy.resteasy.annotations.HttpMethods;
import com.damnhandy.resteasy.annotations.QueryParam;
import com.damnhandy.resteasy.annotations.Representation;
import com.damnhandy.resteasy.annotations.Response;
import com.damnhandy.resteasy.annotations.URIParam;
import com.damnhandy.resteasy.annotations.WebResource;
import com.damnhandy.resteasy.helper.ClassUtils;
import com.damnhandy.resteasy.helper.URITemplateHelper;

/**
 * A helper class that builds a ResourceInvoker for a given WebResource.
 * 
 * @author Ryan J. McDonough
 * Jan 21, 2007
 *
 */
public class ResourceInvokerBuilder {
	private static final Logger logger = Logger.getLogger(ResourceInvokerBuilder.class);
	private static InitialContext ctx;
	
	/**
	 * 
	 * @param resourceClass
	 * @param resource
	 * @return
	 */
	public static ResourceInvoker createResourceInvoker(Class<?> resourceClass,WebResource resource) {
		ResourceInvoker invoker = null;
		if(ClassUtils.isEJB(resourceClass)) {
			Class<?> localInterface = getLocalInterfaceClass(resourceClass);
			String jndiName = getJndiName(resourceClass);
			invoker = new EJBResourceInvoker(resourceClass,
											 localInterface,
											 jndiName);
		} 
		/*
		 * 
		 */
		else if(ClassUtils.isMDB(resourceClass) &&
				isQueueDestination(resourceClass)) {
			String queueName = getQueueName(resourceClass);
			invoker = new MDBQueueInvoker(resourceClass,queueName,getInitialContext());
		} 
		/*
		 * 
		 */
		else {
			invoker = new POJOResourceInvoker(resourceClass);
		}
		prepareInvoker(resource,resourceClass,invoker);
		return invoker;
	}
	
	/**
	 * 
	 * @param resourceClass
	 * @return
	 */
	private static String getQueueName(Class<?> resourceClass) {
		MessageDriven mdb = resourceClass.getAnnotation(MessageDriven.class);
		ActivationConfigProperty[] config = mdb.activationConfig();
		for(int i = 0; i < config.length; i++) {
			if(config[i].propertyName().equals("destination")) {
				return config[i].propertyValue();
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param resourceClass
	 * @return
	 */
	private static boolean isQueueDestination(Class<?> resourceClass) {
		MessageDriven mdb = resourceClass.getAnnotation(MessageDriven.class);
		ActivationConfigProperty[] config = mdb.activationConfig();
		for(int i = 0; i < config.length; i++) {
			if(config[i].propertyName().equals("destinationType") &&
			   config[i].propertyValue().equals("javax.jms.Queue")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public static InitialContext getInitialContext() {
		if(ctx == null) {
			try {
				ctx = new InitialContext();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ctx;
	}
	
	/**
	 * 
	 * @param beanClass
	 * @return
	 */
	private static String getJndiName(Class<?> beanClass) {
		String jndiName = null;
		if(beanClass.isAnnotationPresent(Stateless.class)) {
			jndiName = beanClass.getAnnotation(Stateless.class).mappedName();
		}
		else if(beanClass.isAnnotationPresent(Stateful.class)) {
			jndiName = beanClass.getAnnotation(Stateful.class).mappedName();
		}
		
		if(jndiName == null || 
		   jndiName.length() == 0) {
			//jndiName = Init.instance().getJndiPattern().replace( "#{ejbName}", Seam.getEjbName(beanClass));
			jndiName = RestEasy.instance().getJndiPattern().replaceAll("\\{ejbName\\}", beanClass.getSimpleName());
		}
		return jndiName;
	}

	/**
	 * 
	 * @param beanClass
	 * @return
	 */
	private static Class<?> getLocalInterfaceClass(Class<?> beanClass) {
		Class<?>[] interfaces = beanClass.getInterfaces();
		/*
		 * Get the interface marked with @Local
		 */
		for(int i = 0; i < interfaces.length; i++) {
			if(interfaces[i].isAnnotationPresent(Local.class)) {
				return interfaces[i];
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param resource
	 * @param resourceClass
	 * @param invoker
	 */
	private static void prepareInvoker(WebResource resource,
									   Class<?> resourceClass,
									   ResourceInvoker invoker) {
		String id = resource.id();
		String path = resource.value();
		invoker.setUriTemplateNamePositions(URITemplateHelper.extractURLTemplateNames(path));
        Class<?> targetClass = invoker.getTargetClass();
        Method[] methods = targetClass.getMethods();
        for(int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            /*
             * The method maybe annotated with one or more HTTP Method annotations
             */
            if(method.isAnnotationPresent(HttpMethods.class)) {
            	HttpMethod[] httpMethods = method.getAnnotation(HttpMethods.class).methods();
            	for(int m = 0; m < httpMethods.length; m++) {
            		processMethod(httpMethods[m],method,invoker,id);
            	}
            } else if(method.isAnnotationPresent(HttpMethod.class)) {
            	HttpMethod resourceMethod = method.getAnnotation(HttpMethod.class);
            	processMethod(resourceMethod,method,invoker,id);
            }
        }
        invoker.setRequestPatternString(URITemplateHelper.replaceURLTemplateIDs(path,invoker.getUriTemplateParamTypes()));
        invoker.setPatternKey(new PatternKey(invoker.getRequestPatternString()));
        if(invoker.getMethods().size() == 0) {
        	throw new ConfigurationException("A WebResource must define 1 or more HttpMethods. The class "
        			+invoker.getTargetClass().getSimpleName()+" defines no HttpMethods.");
        }
	}
	
	/**
	 * 
	 * @param resourceMethod
	 * @param method
	 * @param invoker
	 * @param id
	 */
	private static void processMethod(HttpMethod resourceMethod,Method method,ResourceInvoker invoker,String id) {
		if(resourceMethod.resourceId().equals(id)) {
        	Response responseRepresentation = method.getAnnotation(Response.class);
            String responseMediaType = null;
            if(responseRepresentation != null) {
            	responseMediaType = responseRepresentation.mediaType();
            }
            String httpMethod = resourceMethod.value();
            String disriminator = resourceMethod.discriminator();
            if(disriminator.length() == 0) {
            	disriminator = null;
            }
            Map<String,Class<?>> paramMappings = new LinkedHashMap<String,Class<?>>();
            Annotation[][] params = method.getParameterAnnotations();
            String[] paramNames = new String[params.length];
            Class requestRespresentationType = null;
            String requestMediaType = null;
            String requestName = null;
            for(int j = 0; j < params.length; j++) {
                for(int h = 0; h < params[j].length; h++) {
                    Annotation annotation = params[j][h];
                    if(annotation instanceof QueryParam) {
                        QueryParam pathParam = (QueryParam) annotation;
                        paramMappings.put(pathParam.value(), method.getParameterTypes()[j]);
                        paramNames[j] = pathParam.value();
                    } else if(annotation instanceof URIParam) {
                    	URIParam urlParam = (URIParam) annotation;
                        paramMappings.put(urlParam.value(), method.getParameterTypes()[j]);
                        paramNames[j] = urlParam.value();
                        /*
                         * Add a URI parameter type value
                         */
                        invoker.addUriTemplateParamType(urlParam.value(), method.getParameterTypes()[j]);
                    } 
                    else if(annotation instanceof Representation) {
                        Representation representation = (Representation) annotation;
                        Class type = method.getParameterTypes()[j];
                        if(!representation.type().equals(Representation.class)) {
                        	type = representation.type();
                        }
                        paramNames[j] = representation.value();
                        requestRespresentationType = type;
                        requestMediaType = representation.mediaType();
                        requestName = representation.value();
                        paramMappings.put(representation.value(),type);
                    }
                }
            }
            MethodMapping mapping = new MethodMapping(method,paramMappings);
            mapping.setRequestMediaType(requestMediaType);
            mapping.setRequestRespresentationType(requestRespresentationType);
            mapping.setRequestRespresentationId(requestName);
            mapping.setResponseMediaType(responseMediaType);
            MethodKey key = new MethodKey(httpMethod,disriminator);
            invoker.addMethodMapping(key,mapping);
            logger.info(invoker.getTargetClass().getSimpleName()+": bound "+key.toString()+" to URL pattern "+invoker.getRequestPatternString()+" using Method "+mapping.toString());
        }	
	}
	
}
