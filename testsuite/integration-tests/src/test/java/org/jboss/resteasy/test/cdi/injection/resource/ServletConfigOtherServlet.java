/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection.resource;

import java.io.IOException;

import jakarta.enterprise.context.Dependent;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A servlet which is not related to the Jakarta REST application. As a CDI bean, it is a bean of type
 * {@code ServletConfig} without the {@code @Context} qualifier.
 */
@Dependent
@WebServlet(name = ServletConfigOtherServlet.NAME, urlPatterns = "/other")
public class ServletConfigOtherServlet extends HttpServlet {

    public static final String NAME = "other-servlet";

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        resp.getWriter().write(NAME);
    }
}
