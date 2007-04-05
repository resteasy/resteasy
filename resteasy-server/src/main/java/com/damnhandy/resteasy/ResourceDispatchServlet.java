package com.damnhandy.resteasy;


import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.damnhandy.resteasy.core.ResourceDispatcher;
import com.damnhandy.resteasy.core.ResourceInvoker;
import com.damnhandy.resteasy.core.RestEasy;

/**
 * A controller servlet which routes requests to the appropriate WebResource
 *
 * @author Ryan J. McDonough
 * @since 1.0
 */
public class ResourceDispatchServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(ResourceDispatchServlet.class);
    /**
     *
     */
    private static final long serialVersionUID = 8821236352822311415L;
    
    /**
     * 
     *
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    @SuppressWarnings("unchecked")
	public void init(ServletConfig config) throws ServletException {
    	super.init(config);
		RestEasy re = RestEasy.instance();
		String jndiPattern = config.getInitParameter(RestEasy.JNDI_PATTERN);
		String entityManagerJndiName = config.getInitParameter(RestEasy.ENTITYMANAGER_JNDI_NAME);
		if(jndiPattern != null) {
			re.setJndiPattern(jndiPattern);
		}
		if(entityManagerJndiName != null) {
			re.setEntityManagerJndiName(entityManagerJndiName);
		}
		re.init(this.getServletContext());
    }
    
    /**
     * 
     *
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException,IOException {
    	
    	/*
    	 * Look up the resource for the specified path na,e
    	 */
    	logger.debug("Executing Method: "+request.getMethod()+" for path "+request.getPathInfo());
    	ResourceInvoker invoker = ResourceDispatcher.getInstance().findResourceInvoker(request.getPathInfo());
        if(invoker != null) {
        	invoker.invoke(request, response);
        } else {
            logger.error("Invalid resource path: "+request.getPathInfo());
        	response.sendError(HttpServletResponse.SC_NOT_FOUND,
                               "The resource  for "+request.getPathInfo()
                               +" was not found.");
        }
    }
    

   
    /**
     * 
     *
     * @see javax.servlet.GenericServlet#destroy()
     */
	public void destroy() {
        super.destroy();
    }
}
