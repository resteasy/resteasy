package com.damnhandy.resteasy.handler;


import java.io.InputStream;
import java.io.OutputStream;

import com.damnhandy.resteasy.RespresentationHandlerException;

/**
 * Interface which handles the serialization of the Representaions media type
 *
 * @author Ryan J. McDonough
 * @since 1.0
 */
public interface RepresentationHandler {
    
    /**
     *
     * @param response
     * @param result
     */
    public abstract void handleResponse(OutputStream out,Object result)
    	throws RespresentationHandlerException;
    
    /**
     *
     * @param request
     * @param c
     * @return
     */
    public abstract Object handleRequest(InputStream in, Class c)
    	throws RespresentationHandlerException;
    
}