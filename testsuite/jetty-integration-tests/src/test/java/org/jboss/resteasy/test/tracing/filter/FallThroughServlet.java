/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.tracing.filter;

import java.io.IOException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Receives the requests that the RESTEasy filter does not match and passes down the filter chain.
 */
public class FallThroughServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    static final String BODY = "fell through to the servlet";

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().write(BODY);
    }
}
