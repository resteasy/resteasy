/**
 * 
 */
package com.damnhandy.resteasy.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.Local;
import javax.ejb.MessageDriven;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.jms.Message;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.damnhandy.resteasy.ConfigurationException;
import com.damnhandy.resteasy.annotations.HttpMethod;
import com.damnhandy.resteasy.annotations.HttpMethods;
import com.damnhandy.resteasy.annotations.QueryParam;
import com.damnhandy.resteasy.annotations.RepresentationIn;
import com.damnhandy.resteasy.annotations.RepresentationOut;
import com.damnhandy.resteasy.annotations.Type;
import com.damnhandy.resteasy.annotations.URIParam;
import com.damnhandy.resteasy.annotations.WebResource;
import com.damnhandy.resteasy.entity.DefaultEJBResourceManager;
import com.damnhandy.resteasy.entity.DefaultEJBResourceManagerBean;
import com.damnhandy.resteasy.helper.ClassUtils;
import com.damnhandy.resteasy.helper.URITemplateHelper;
import com.damnhandy.resteasy.representation.Representation;

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
	private static final String REPRESENTATION = "representation";
	
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
		prepareInvoker(resource.id(),resource.value(),resourceClass,invoker);
		return invoker;
	}
	
	/**
	 * 
	 * @param persisterClass
	 * @param entityClass
	 * @param entity
	 * @return
	 */
	public static ResourceInvoker createEntityResourceInvoker(Class<?> entityClass, 
															  Class<?> resourceClass,
															  String id,
															  String path) {
		
		ResourceInvoker invoker = null;
		if(ClassUtils.isEJB(resourceClass)) {
			Class<?> localInterface = getLocalInterfaceClass(resourceClass);
			String jndiName = getJndiName(resourceClass);
			invoker = new EJBResourceInvoker(resourceClass,
											 localInterface,
											 jndiName);
		} 
		else {
			throw new ConfigurationException("Sorry, non-EJB ResourceManagers are not suppored yet.");
		}
		prepareInvoker(id,path,resourceClass,invoker);
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
		if(beanClass.equals(DefaultEJBResourceManagerBean.class)) {
			return DefaultEJBResourceManager.class;
		}
		
		Class<?>[] interfaces = beanClass.getInterfaces();
		/*
		 * Get the interface marked with @Local, there "should" be only one
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
	 * @param representationIn
	 * @return
	 */
	private static String getInputType(String defaultValue,RepresentationIn representationIn) {
		if (representationIn != null) {
			return representationIn.value();
		}
		return defaultValue;
	}
	
	/**
	 * 
	 * @param representationOut
	 * @return
	 */
	private static QualityValue getOutputType(RepresentationOut representationOut) {
		if(representationOut != null) {
			return new QualityValue(representationOut.value(),representationOut.qs());
		}
		return null;
	}
	
	/**
	 * 
	 * @param resource
	 * @param resourceClass
	 * @param invoker
	 */
	private static void prepareInvoker(String id,
									   String path,
									   Class<?> resourceClass,
									   ResourceInvoker invoker) {
		SortedSet<QualityValue> qualityOfSource = new TreeSet<QualityValue>();
		invoker.setUriTemplateNamePositions(URITemplateHelper.extractURLTemplateNames(path));
		String inputType = getInputType(ContentNegotiator.DEFAULT_TYPE,
										resourceClass.getAnnotation(RepresentationIn.class));
		RepresentationOut outputType = resourceClass.getAnnotation(RepresentationOut.class);
		QualityValue qualityValue;
		if(outputType == null) {
			qualityValue = new QualityValue(ContentNegotiator.DEFAULT_TYPE,1.0f);
		}
		qualityValue = getOutputType(resourceClass.getAnnotation(RepresentationOut.class));
		qualityOfSource.add(qualityValue);
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
            		processMethod(httpMethods[m],method,invoker,id,inputType,qualityOfSource,qualityValue);
            	}
            } else if(method.isAnnotationPresent(HttpMethod.class)) {
            	HttpMethod resourceMethod = method.getAnnotation(HttpMethod.class);
            	processMethod(resourceMethod,method,invoker,id,inputType,qualityOfSource,qualityValue);
            }
        }
        String uriPattern = 
        	URITemplateHelper.replaceURLTemplateIDs(path,invoker.getUriTemplateParamTypes());
        invoker.setRequestPatternString(uriPattern);
        invoker.setPatternKey(new PatternKey(invoker.getRequestPatternString()));
        QualityValue[] qualityValues = qualityOfSource.toArray(new QualityValue[qualityOfSource.size()]);
        invoker.setQualityOfSource(qualityValues);
        if(invoker.getMethods().size() == 0) {
        	throw new ConfigurationException("A WebResource must define 1 or more HttpMethods. The class "
        			+invoker.getTargetClass().getSimpleName()+" defines none.");
        } 
        /*
         * Print the configuration to the Log
         */
        else {
        	for(Map.Entry<MethodKey,MethodMapping> entry : invoker.getMethods().entrySet()) {
        		logger.info(invoker.getTargetClass().getSimpleName()+": bound "+
        				    entry.getKey().toString()+" to URL pattern "+
        				    invoker.getRequestPatternString()+
        				    " using Method "+entry.getValue().toString());
        	}
        }
	}
	
	/**
	 * 
	 * @param resourceMethod
	 * @param method
	 * @param invoker
	 * @param resourceId
	 */
	@SuppressWarnings("unchecked")
	private static void processMethod(HttpMethod resourceMethod,
									  Method method,
									  ResourceInvoker invoker,
									  String resourceId,
									  String parentInputType,
									  SortedSet<QualityValue> qualityOfSource,
									  QualityValue defaultQS) {
		/*
		 * A GET method should not be mapped to a method that returns void.
		 */
		if(resourceMethod.value().equals(HttpMethod.GET) && 
		   method.getReturnType() == null) {
			throw new ConfigurationException("HTTP GET methods must have a return value. The method "
					+method.getName()+" does not specify a return value.");
		}
		/*
		 * Make sure the resource ID matches the ID of the WebResource
		 */
		if(resourceMethod.resourceId().equals(resourceId)) {
			
			QualityValue qualityFactor = getOutputType(method.getAnnotation(RepresentationOut.class));
			if(qualityFactor != null) {
				if(qualityFactor.getMediaType().equals("application/pdf")) {
					System.out.println("Type");
				}
				qualityOfSource.add(qualityFactor);
			} else {
				qualityFactor = defaultQS;
			}
            String httpMethod = resourceMethod.value();
            String disriminator = resourceMethod.discriminator();
            if(disriminator.length() == 0) {
            	disriminator = null;
            }
            Map<String,Class<?>> paramMappings = new LinkedHashMap<String,Class<?>>();
            Annotation[][] params = method.getParameterAnnotations();
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?> requestRespresentationType = null;
            /*
             * Scan for annotated parametr types
             */
            boolean hasInputRepresentation = false;
            for(int p = 0; p < parameterTypes.length; p++) {
            	for(int h = 0; h < params[p].length; h++) {
                    Annotation annotation = params[p][h];
                    if(annotation instanceof QueryParam) {
                        QueryParam pathParam = (QueryParam) annotation;
                        paramMappings.put(pathParam.value(), method.getParameterTypes()[p]);
                    } else if(annotation instanceof URIParam) {
                    	URIParam urlParam = (URIParam) annotation;
                        paramMappings.put(urlParam.value(), method.getParameterTypes()[p]);
                        /*
                         * Add a URI parameter type value so that the proper regex can
                         * be generated after the methods have been processed.
                         */
                        invoker.addUriTemplateParamType(urlParam.value(), 
                        								method.getParameterTypes()[p]);
                    } else if(annotation instanceof Type &&
                    		  (Representation.class.isAssignableFrom(parameterTypes[p]) ||
                    		   Message.class.isAssignableFrom(parameterTypes[p]))) {
                    	Type type = (Type) annotation;
                        paramMappings.put(REPRESENTATION, type.value());
                        requestRespresentationType = type.value();
                        hasInputRepresentation = true;
                    } 
                }
            }
            MethodMapping mapping = new MethodMapping(method,paramMappings);
            String requestMediaType = null;
			if(hasInputRepresentation) {
				requestMediaType = getInputType(parentInputType,
							method.getAnnotation(RepresentationIn.class));
				mapping.setRequestMediaType(requestMediaType);
	            mapping.setRequestRespresentationType(requestRespresentationType);
	            mapping.setRequestRespresentationId(REPRESENTATION);
			}
            mapping.setResponseMediaType(qualityFactor.getMediaType());
            MethodKey key = new MethodKey(httpMethod,disriminator,requestMediaType,qualityFactor.getMediaType());
            invoker.addMethodMapping(key,mapping);
            
        }	
	}
	
}
