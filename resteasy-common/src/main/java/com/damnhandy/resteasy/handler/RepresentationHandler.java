package com.damnhandy.resteasy.handler;


import java.io.InputStream;
import java.io.OutputStream;

import com.damnhandy.resteasy.common.HttpHeaders;
import com.damnhandy.resteasy.exceptions.RespresentationHandlerException;
import com.damnhandy.resteasy.representation.Representation;

/**
 * Interface which handles the serialization of the Representaions media type
 *
 * @author Ryan J. McDonough
 * @since 1.0
 */
public interface RepresentationHandler<T> {
    
    /**
     *
     * @param response
     * @param result
     */
    public abstract void handleResponse(OutputStream out,T result)
    	throws RespresentationHandlerException;
    
    /**
     *
     * @param request
     * @param c
     * @return
     */
    public abstract Object handleRequest(InputStream in, Class<T> c)
    	throws RespresentationHandlerException;
    
    /**
     * 
     * @param in
     * @param c
     * @param headers
     * @return
     * @throws RespresentationHandlerException
     */
    public Representation<T> handleRequest(InputStream in,Class<T> c,HttpHeaders headers)
		throws RespresentationHandlerException;
    
}