package com.damnhandy.resteasy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.damnhandy.resteasy.common.HttpHeaderNames;
import com.damnhandy.resteasy.grammar.Fault;
import com.damnhandy.resteasy.representation.JAXBRepresentation;
import com.damnhandy.resteasy.representation.Representation;

/**
 * 
 * @author Ryan J. McDonough Feb 12, 2007
 * @since 1.0
 * 
 */
public class ErrorHandlerServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2417042977608589767L;
	/**
	 * 
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Fault fault = new Fault();
		Object e = request.getAttribute(Fault.EXCEPTION);
		if(e != null && e instanceof Exception) {
			Exception exception = (Exception) e;
			fault.setServerStackTrace(printStackTrace(exception));
		}
		Class<?> type = (Class<?>) request.getAttribute(Fault.EXCEPTION_TYPE);
		if(type != null) {
			fault.setExceptionType(type.getCanonicalName());
		}
		fault.setMessage((String) request.getAttribute(Fault.MESSAGE));
		fault.setRequestUri((String) request.getAttribute(Fault.REQUEST_URI));
		fault.setStatusCode((Integer) request.getAttribute(Fault.ERROR_STATUS));
		response.setDateHeader(HttpHeaderNames.DATE, System.currentTimeMillis());
		Representation<Fault> representation = new JAXBRepresentation<Fault>(fault);
		representation.writeTo(response.getOutputStream());
	}
	/**
	 * 
	 * @param e
	 * @return
	 */
	private String printStackTrace(Exception e) {
		StringBuilder b = new StringBuilder();
		StackTraceElement[] trace = e.getStackTrace();
		for(int j = 0; j < trace.length; j++) {
			StackTraceElement t = trace[j];
			b.append(t.getClassName()).append(t.getMethodName()).append(t.getLineNumber()).append("\n");
		}
		return b.toString();
	}
}
