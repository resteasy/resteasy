package com.damnhandy.resteasy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.damnhandy.resteasy.core.ResourceDispatcher;
import com.damnhandy.resteasy.grammar.Fault;
import com.damnhandy.resteasy.handler.RepresentationHandler;

/**
 * 
 * @author Ryan J. McDonough Feb 12, 2007
 * 
 */
public class ErrorHandlerServlet extends HttpServlet {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ErrorHandlerServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Fault fault = new Fault();
		Class<?> type = (Class<?>) request.getAttribute(Fault.EXCEPTION_TYPE);
		if(type != null) {
			fault.setExceptionType(type.getCanonicalName());
		}
		fault.setMessage((String) request.getAttribute(Fault.MESSAGE));
		fault.setRequestUri((String) request.getAttribute(Fault.REQUEST_URI));
		fault.setStatusCode((Integer) request.getAttribute(Fault.ERROR_STATUS));
		RepresentationHandler handler = ResourceDispatcher.findRepresentationHandler("application/xml");
		response.setContentType("application/xml");
		response.setDateHeader("Date", System.currentTimeMillis());
		handler.handleResponse(response.getOutputStream(), fault);
	}
}
