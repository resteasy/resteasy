/**
 * 
 */
package com.damnhandy.resteasy.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;

import com.damnhandy.resteasy.annotations.HttpMethod;
import com.damnhandy.resteasy.common.HttpHeaderNames;
import com.damnhandy.resteasy.common.HttpHeaders;
import com.damnhandy.resteasy.exceptions.HttpMethodInvocationException;
import com.damnhandy.resteasy.exceptions.RespresentationHandlerException;
import com.damnhandy.resteasy.handler.RepresentationHandler;
import com.damnhandy.resteasy.handler.RepresentationHandlerFactory;
import com.damnhandy.resteasy.helper.URITemplateHelper;


/**
 * The ResourceInvoker is used to invoke a Java method from an URI, HTTP method, and 
 * mime type. 
 * 
 * 
 * @author Ryan J. McDonough
 * @since 1.0
 * Jan 21, 2007
 *
 */

public abstract class ResourceInvoker {
	private static final Logger logger = Logger.getLogger(ResourceInvoker.class);
	/**
	 * The Class that will be invoked
	 */
	private Class<?> targetClass;
    /**
     * A Mapping of HTTP methods to Java methods
     */
	private Map<MethodKey,MethodMapping> methods = new HashMap<MethodKey,MethodMapping>();
    /**
     * Stores the position of the URI Template parameter name
     */
	private Map<String,Integer> uriTemplateNamePositions;
	
	/**
	 * Stores the type of the URI parameter in order to generate the 
	 * correct regex pattern
	 */
	private Map<String,Class<?>> uriTemplateParamTypes = new HashMap<String, Class<?>>();
	/**
	 * The regex representing the URI Template
	 */
    private String requestPatternString;
    /**
     * A compiled version of the URI template
     */
    private PatternKey patternKey;
    
    /**
     * Returns the instance that contains the method that will be executed
     * by this invoker.
     * @return
     */
    public abstract Object getTargetInstance();
	
    
    
    /**
	 * @return the uriTemplateParamTypes
	 */
	protected Map<String, Class<?>> getUriTemplateParamTypes() {
		return uriTemplateParamTypes;
	}


	/**
	 * 
	 * @param name
	 * @param type
	 */
	protected void addUriTemplateParamType(String name,Class<?> type) {
		uriTemplateParamTypes.put(name,type);
	}



	/**
     * 
	 * @return the methods
	 */
    protected final Map<MethodKey, MethodMapping> getMethods() {
		return methods;
	}
	
	/**
	 * 
	 * @param httpMethod
	 * @param mapping
	 */
	protected void addMethodMapping(MethodKey key, MethodMapping mapping) {
		methods.put(key, mapping);
	}
	/**
	 * @return the pathParamNames
	 */
	protected Map<String, Integer> getUriTemplateNamePositions() {
		return uriTemplateNamePositions;
	}
	/**
	 * @param pathParamNames the pathParamNames to set
	 */
	protected void setUriTemplateNamePositions(Map<String, Integer> pathParamNames) {
		this.uriTemplateNamePositions = pathParamNames;
	}
	
	/**
	 * @return the requestPatternString
	 */
	public String getRequestPatternString() {
		return requestPatternString;
	}
	/**
	 * @param requestPatternString the requestPatternString to set
	 */
	protected void setRequestPatternString(String requestPatternString) {
		this.requestPatternString = requestPatternString;
	}
	/**
	 * @return the targetClass
	 */
	public Class<?> getTargetClass() {
		return targetClass;
	}
	/**
	 * @param targetClass the targetClass to set
	 */
	protected void setTargetClass(final Class<?> targetClass) {
		this.targetClass = targetClass;
	}
	
	
	/**
	 * Extracts the values required by the Java method from the HTTP request. Method values
	 * maybe extracted from the following places:
	 * <ul>
	 * 	<li>The map of query parameters</li>
	 *  <li>URI Template parameters</li>
	 *  <li>The request body</li>
	 *  <li>HTTP headers</li>
	 * </li>
	 * 
	 * All name/value pairs are collected in a Map that will be used as the methdo inputs.
	 * 
	 * @param request
	 * @param mapping
	 * @return
	 */
	protected final Map<String,Object> extractInputsFromRequest(HttpServletRequest request,
															   MethodMapping mapping) {
        /*
         * A map that holds the method parameter name and value
         */
		Map<String,Object> inputs = new HashMap<String,Object>();
        /*
         * Find any request parameters that are needed by this method
         */
        mergeQueryParameters(inputs, request, mapping);
        /*
         * Extract any template parameters from the URL
         */
        Map<Integer,String> pathValues = 
        	URITemplateHelper.extractURLParameterValues(request.getPathInfo(), getPatternKey().getPattern());
        /*
         * Merge the template params into the inputs map
         */
        if(pathValues.size() > 0) {
            mergeURITemplateValues(inputs,pathValues);
        }
        readRequest(request,mapping,inputs);
        return inputs;
	}
	/**
	 * Returns the MethodMapping instance for the requested HTTP operation.
	 * @param request
	 * @return the MethodMapping
	 */
	protected final MethodMapping findMethodMapping(HttpServletRequest request) {
		String discriminator = request.getParameter(HttpMethod.DISCRIMINATOR_KEY);
    	MethodKey key = new MethodKey(request.getMethod(),discriminator);
    
        MethodMapping mapping = getMethods().get(key);
        if(mapping == null) {
        	throw new HttpMethodInvocationException("The method is not allowed for this resource.",HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        } 
        return mapping;
	}
	
	/**
     * Invokes the method marked with the {@link HttpMethod} annotation. If the
     * method has a return value, that value will be written to the response.
     * 
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     */
    public void invoke(HttpServletRequest request, HttpServletResponse response)
        throws HttpMethodInvocationException {
    	
    	HttpHeaders headers = extractHttpHeadersFromRequest(request);
    	MethodMapping mapping = findMethodMapping(request);
		Map<String,Object> inputs = extractInputsFromRequest(request,mapping);
    	try {
			Object result = invokeMethod(mapping,inputs,getTargetInstance());
			writeResponse(request,response,result,mapping);
		} finally {
			inputs.clear();
			mapping = null;
		}
    }

    
    private HttpHeaders extractHttpHeadersFromRequest(HttpServletRequest request) {
    	HttpHeaders headers = new HttpHeaders();
    	Enumeration headerNames = request.getHeaderNames();
    	while(headerNames.hasMoreElements()) {
    		String headerName = (String) headerNames.nextElement();
    		String headerValue = request.getHeader(headerName);
    		headers.addHeader(headerName, headerValue);
    	}
    	return headers;
    }
    /**
     * Parses the input stream to extract the input data and Unmarshall it
     * to it Java type
     * @param request
     * @param mapping
     * @param inputs
     */
    protected void readRequest(HttpServletRequest request,MethodMapping mapping,Map<String,Object> inputs) {
    	String method = request.getMethod();
    	if((method.equals("PUT") || 
            method.equals("POST"))) {
    		/*
    		 * Check the the media types match
    		 */
    						
			if(request.getContentType() != null &&  
			   request.getContentType().equals(mapping.getRequestMediaType())) {
				RepresentationHandlerFactory factory = RepresentationHandlerFactory.instance();
				RepresentationHandler<?> requestHandler = factory.getHandlerByMimeType(request.getContentType());
				if(requestHandler == null) {
					throw new HttpMethodInvocationException("The input media type "+mapping.getRequestMediaType()+
															" is not supported for this operation,",
															 HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
				}
				if(logger.isDebugEnabled()) {
					logger.debug("Handing "+request.getMethod()+" method...");
				}
				try {
					Object requestValue =
							requestHandler.handleRequest(request.getInputStream(), 
														 mapping.getRequestRespresentationType());
					inputs.put(mapping.getRequestRespresentationId(),requestValue);
				} catch (IOException e) {
					throw new RespresentationHandlerException("",e);
				}
			}
			/*
			 * If the requested content type does not match the methods media type, 
			 * a 415 error is thrown.
			 */
			else {
				throw new HttpMethodInvocationException("The server is refusing to service the request because the entity " +
						"of the request is in a format ("+request.getContentType()
						+")not supported by the requested resource for the requested method.",
							HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
			}
    	}
    } 
    
        
    

    /**
     * 
     * @param response
     * @param result
     * @param mediaType
     */
    protected void writeResponse(HttpServletRequest request,
    							 HttpServletResponse response,
    							 Object result,
    							 MethodMapping<?> mapping) {
    	try {
    		String mediaType = mapping.getResponseMediaType();
			/*
			 * If the result is null, there is no need to write a response.
			 */
			if(result != null) {
				/*
				 * If the Response code indicates that the new resources was created
				 * and the return type is a URL, we issue a 204 and return the URL
				 * in the Location header.
				 */
				if(result instanceof URL) {
					URL location = (URL) result;
					response.setStatus(HttpServletResponse.SC_CREATED);
					response.setHeader(HttpHeaderNames.LOCATION, location.toString());
				} 
				/*
				 * If the result is not null and not a URL, we look up the
				 * desired return media type to find an appropriate RepresentationHandler.
				 */
				else {
					RepresentationHandlerFactory factory = RepresentationHandlerFactory.instance();
					RepresentationHandler handler = factory.getHandlerByMimeType(mediaType);
					if(handler == null) {
						throw new HttpMethodInvocationException("The response media type "+mediaType+
																" is not supported for this operation,",
																 HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
					}
					response.setContentType(mediaType);
				    handler.handleResponse(response.getOutputStream(), result);
				    response.setStatus(HttpServletResponse.SC_OK);
				}
			}
			/*
			 * If the result is null, it is most likely that the requested ID does not exist 
			 * and a 404 should be returned.
			 */
			else if(result == null && request.getMethod().equals("GET")) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
			/*
			 * If the method has not return value and does not return a new
			 * location, the server should return a 204
			 */
			else if(result == null && 
					(request.getMethod().equals("POST") ||
					 request.getMethod().equals("PUT"))) {
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			}
		} catch (IOException e) {
			throw new RespresentationHandlerException("",e);
		}
    }
    

    /**
     * Merge an Query Parameters into the inputs map.
     * @param inputs
     * @param request
     * @param mapping
     */
    private void mergeQueryParameters(Map<String,Object> inputs,HttpServletRequest request,MethodMapping<?> mapping) {
       Set<String> paramNames = mapping.getParameterMap().keySet();
        for(String paramName : paramNames) {
        	Object value = request.getParameter(paramName);
        	if(paramName != HttpMethod.DISCRIMINATOR_KEY || 
        	   value != null) {
        		inputs.put(paramName, value);
        	}
        }
    }
    
    /**
     * Merges the URI Template values into the inputs Map.
     * @param inputs
     * @param pathValues
     */
    private void mergeURITemplateValues(Map<String,Object> inputs,Map<Integer,String> pathValues) {
        for(Entry<String, Integer> nameEntry : uriTemplateNamePositions.entrySet()) {
            String key = nameEntry.getKey();
            String value = pathValues.get(nameEntry.getValue());
            inputs.put(key, value);
        }
    }
    
    /**
     *
     * @param methodName
     * @param inputs
     * @return
     * @throws Exception
     */
    protected Object invokeMethod(MethodMapping<?> mapping,Map<String,Object> inputs,Object target)
    	throws HttpMethodInvocationException {
        try {
        	Method method = mapping.getMethod();
            Map<String,Class<?>> paramMap = mapping.getParameterMap();
            Object[] args = new Object[paramMap.size()];
            int counter = 0;
            for(Map.Entry<String,Class<?>> entry : paramMap.entrySet()) {
            	String name = entry.getKey();
                Object value = inputs.get(name);
                args[counter++] = convertType(value,entry.getValue(),mapping);
            }
            return method.invoke(target, args);
        } catch (IllegalArgumentException e) {
            throw new HttpMethodInvocationException("",HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
        } catch (IllegalAccessException e) {
            throw new HttpMethodInvocationException("",HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
        } catch (InvocationTargetException e) {
            throw new HttpMethodInvocationException("InvocationTargetException when invoking service: "+e.getMessage() +" target exception: "+e.getTargetException().getMessage(),HttpServletResponse.SC_NOT_IMPLEMENTED,e);
        } catch (SecurityException e) {
        	throw new HttpMethodInvocationException("",HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
		} catch (ClassCastException e) {
        	throw new HttpMethodInvocationException("",HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
        	
		} 
    }
    
    /**
    *
    * @param input
    * @param targetType
    * @return
    * @throws IllegalArgumentException
    * @throws IllegalAccessException
    * @throws InvocationTargetException
    * @throws NoSuchMethodException
    */
   private Object convertType(Object input, Class targetType,MethodMapping<?> mapping)
   throws HttpMethodInvocationException {
	   if(input == null) {
		   return input;
	   }
	   if(targetType.isInstance(input)) {
           return input;
       } 
       else if(mapping.getRequestRespresentationType() != null &&
                 mapping.getRequestRespresentationType().isInstance(input)) {
           return input;
       } 
       else if(input instanceof JAXBElement) {
           Object inputValue = ((JAXBElement) input).getValue();
           if(mapping.getRequestRespresentationType().isInstance(inputValue)) {
               return inputValue;
           } 
           else {
               throw new HttpMethodInvocationException("type mismatch!",HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
           }
           
       }
       else {
           try {
        	   Method valueOf =
                       targetType.getDeclaredMethod("valueOf",new Class[] { String.class });
               Object value = valueOf.invoke(null, input);
               return value;
           } catch (SecurityException e) {
               throw new HttpMethodInvocationException("SecurityException: "+e.getMessage(),HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
           } catch (IllegalArgumentException e) {
               throw new HttpMethodInvocationException(""+e.getMessage(),HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
           } catch (NoSuchMethodException e) {
        	   throw new HttpMethodInvocationException(""+e.getMessage(),HttpServletResponse.SC_NOT_IMPLEMENTED,e);
           } catch (IllegalAccessException e) {
               throw new HttpMethodInvocationException(""+e.getMessage(),HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
           } catch (InvocationTargetException e) {
               throw new HttpMethodInvocationException("",HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e);
           }
           
       }
   }
	/**
	 * @return the patternKey
	 */
	protected PatternKey getPatternKey() {
		return patternKey;
	}
	/**
	 * @param patternKey the patternKey to set
	 */
	protected void setPatternKey(PatternKey patternKey) {
		this.patternKey = patternKey;
	}
	
}
