/*
 * StringRepresentationHandler.java
 *
 * Created on November 17, 2006, 8:39 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.damnhandy.resteasy.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import com.damnhandy.resteasy.RespresentationHandlerException;
import com.damnhandy.resteasy.annotations.MediaType;

/**
 *
 * @author Ryan J. McDonough
 * @since 1.0
 */
@MediaType("text/plain")
public class PlainTextRepresentationHandler implements RepresentationHandler {
    
    /** Creates a new instance of StringRepresentationHandler */
    public PlainTextRepresentationHandler() {
    }
    
    
    /**
     *
     * @see com.damnhandy.resteasy.handler.RepresentationHandler#handleRequest(javax.servlet.http.HttpServletRequest, java.lang.Class)
     */
    public Object handleRequest(InputStream in, Class c)
    throws RespresentationHandlerException {
        try {
            if(CharSequence.class.isAssignableFrom(c)) {
                StringWriter writer = new StringWriter();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = reader.readLine();
                while((line != null)) {
                    writer.write(line);
                    line = reader.readLine();
                }
                return writer.toString();
            } else {
                throw new RespresentationHandlerException("The input type is not a text value.",HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            throw new RespresentationHandlerException("",e);
        }
    }
    
    /**
     *
     * @see com.damnhandy.resteasy.handler.RepresentationHandler#handleResponse(javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    public void handleResponse(OutputStream out, Object result)
    throws RespresentationHandlerException {
        try {
            out.write(result.toString().getBytes());
        } catch (IOException e) {
            throw new RespresentationHandlerException("",e);
        }
        
    }
}
