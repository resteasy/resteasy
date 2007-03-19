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

import com.damnhandy.resteasy.common.HttpHeaders;
import com.damnhandy.resteasy.common.ResponseCodes;
import com.damnhandy.resteasy.exceptions.RespresentationHandlerException;
import com.damnhandy.resteasy.representation.Representation;

/**
 *
 * @author Ryan J. McDonough
 * @since 1.0
 */
@MediaType(type="text/plain",extentions={"txt","text"})
public class PlainTextRepresentationHandler implements RepresentationHandler<String> {
    
    /** Creates a new instance of StringRepresentationHandler */
    public PlainTextRepresentationHandler() {
    }
    
    
    /**
     *
     * @see com.damnhandy.resteasy.handler.RepresentationHandler#handleRequest(javax.servlet.http.HttpServletRequest, java.lang.Class)
     */
    public String handleRequest(InputStream in, Class<String> c)
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
                throw new RespresentationHandlerException("The input type is not a text value.",ResponseCodes.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            throw new RespresentationHandlerException("",e);
        }
    }
    
    /**
     *
     * @see com.damnhandy.resteasy.handler.RepresentationHandler#handleResponse(javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    public void handleResponse(OutputStream out, String result)
    throws RespresentationHandlerException {
        try {
            out.write(result.getBytes());
        } catch (IOException e) {
            throw new RespresentationHandlerException("",e);
        }
        
    }


	public Representation<String> handleRequest(InputStream in, Class<String> c, HttpHeaders headers) throws RespresentationHandlerException {
		// TODO Auto-generated method stub
		return null;
	}
}
