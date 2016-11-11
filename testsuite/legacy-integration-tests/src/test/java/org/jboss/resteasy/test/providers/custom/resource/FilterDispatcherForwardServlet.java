package org.jboss.resteasy.test.providers.custom.resource;

import org.jboss.logging.Logger;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

public class FilterDispatcherForwardServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(FilterDispatcherForwardServlet.class);

    private static final long serialVersionUID = 1L;

    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        logger.info("enterng ForwardServlet.service()");
        res.getOutputStream().write("forward".getBytes());
    }
}
