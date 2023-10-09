package org.jboss.resteasy.plugins.server.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;

import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.junit.jupiter.api.Test;

public class ServletUtilTest {

    @Test
    public void extractUriInfo_simple() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("");
        when(request.getQueryString()).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8000/app/resource"));
        ResteasyUriInfo rui = ServletUtil.extractUriInfo(request, null);
        assertEquals("", rui.getContextPath());
        assertEquals("/app/resource", rui.getPath());
    }

    @Test
    public void extractUriInfo_simpleWithContextRoot() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("/contextRoot");
        when(request.getQueryString()).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8000/contextRoot/app/resource"));
        ResteasyUriInfo rui = ServletUtil.extractUriInfo(request, null);
        assertEquals("/contextRoot", rui.getContextPath());
        assertEquals("/app/resource", rui.getPath());
    }

    @Test
    public void extractUriInfo_encoded() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("");
        when(request.getQueryString()).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8000/app/resource!"));
        ResteasyUriInfo rui = ServletUtil.extractUriInfo(request, null);
        assertEquals("", rui.getContextPath());
        assertEquals("/app/resource!", rui.getPath());

        request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("");
        when(request.getQueryString()).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8000/app/resource%21"));
        rui = ServletUtil.extractUriInfo(request, null);
        assertEquals("", rui.getContextPath());
        assertEquals("/app/resource!", rui.getPath());

        request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("");
        when(request.getQueryString()).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8000/app!/resource"));
        rui = ServletUtil.extractUriInfo(request, null);
        assertEquals("", rui.getContextPath());
        assertEquals("/app!/resource", rui.getPath());

        request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("");
        when(request.getQueryString()).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8000/app%21/resource"));
        rui = ServletUtil.extractUriInfo(request, null);
        assertEquals("", rui.getContextPath());
        assertEquals("/app!/resource", rui.getPath());
    }

    @Test
    public void extractUriInfo_encodedWithContextRoot() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("/contextRoot");
        when(request.getQueryString()).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8000/contextRoot/app/resource!"));
        ResteasyUriInfo rui = ServletUtil.extractUriInfo(request, null);
        assertEquals("/contextRoot", rui.getContextPath());
        assertEquals("/app/resource!", rui.getPath());

        request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("/contextRoot");
        when(request.getQueryString()).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8000/contextRoot/app/resource%21"));
        rui = ServletUtil.extractUriInfo(request, null);
        assertEquals("/contextRoot", rui.getContextPath());
        assertEquals("/app/resource!", rui.getPath());

        request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("/contextRoot");
        when(request.getQueryString()).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8000/contextRoot/app!/resource"));
        rui = ServletUtil.extractUriInfo(request, null);
        assertEquals("/contextRoot", rui.getContextPath());
        assertEquals("/app!/resource", rui.getPath());

        request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("/contextRoot");
        when(request.getQueryString()).thenReturn(null);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8000/contextRoot/app%21/resource"));
        rui = ServletUtil.extractUriInfo(request, null);
        assertEquals("/contextRoot", rui.getContextPath());
        assertEquals("/app!/resource", rui.getPath());
    }
}
