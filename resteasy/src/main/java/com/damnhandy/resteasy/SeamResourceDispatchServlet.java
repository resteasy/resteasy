package com.damnhandy.resteasy;


import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;

import com.damnhandy.resteasy.core.ResourceDispatcher;
import com.damnhandy.resteasy.core.ResourceInvoker;
import com.damnhandy.resteasy.core.RestEasy;
import com.damnhandy.resteasy.core.Version;

/**
 * A controller servlet which routes requests to the appropriate WebResource
 *
 * @author Ryan J. McDonough
 * @since 1.0
 */
public class SeamResourceDispatchServlet extends HttpServlet {

	private static final Logger logger = Logger.getLogger(SeamResourceDispatchServlet.class);
    /**
     *
     */
    private static final long serialVersionUID = 8821236352822311415L;
    
    /**
     * Iinitia
     */
    @SuppressWarnings("unchecked")
	public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	logger.info("Starting "+Version.FULL_RELEASE_NAME);
    	RestEasy re = RestEasy.instance();
		String jndiPattern = config.getInitParameter(RestEasy.JNDI_PATTERN);
		String entityManagerJndiName = config.getInitParameter(RestEasy.ENTITYMANAGER_JNDI_NAME);
		if(jndiPattern != null) {
			re.setJndiPattern(jndiPattern);
		}
		logger.info("Set JNDI Pattern using: "+re.getJndiPattern());
		if(entityManagerJndiName != null) {
			re.setEntityManagerJndiName(entityManagerJndiName);
		}
		logger.info("Set EntityManager JNDI Name using: "+re.getEntityManagerJndiName());
		re.init();
    }
    
    /**
     * 
     *
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException,IOException {
    	response.setHeader("RESTEasy-Version", Version.FULL_RELEASE_NAME);
    	
    	ResourceInvoker invoker = ResourceDispatcher.getInstance().findResourceInvoker(request.getPathInfo());
        if(invoker != null) {
        	logger.info("Executing Method: "+request.getMethod());
            try {
            	Context context = Contexts.getEventContext();
                //HttpSession session = request.getSession(true);
                /*Lifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
                Lifecycle.setServletRequest(request);
                Lifecycle.beginRequest(getServletContext(), session, request);*/
				invoker.invoke(request, response);
			} finally {
				/*Lifecycle.endRequest();
			    Lifecycle.setServletRequest(null);
			    Lifecycle.setPhaseId(null);*/
			}
             
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
		Lifecycle.endApplication(getServletContext());
        super.destroy();
    }
}
