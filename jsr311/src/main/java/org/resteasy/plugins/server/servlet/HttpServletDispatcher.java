package org.resteasy.plugins.server.servlet;

import org.resteasy.Registry;
import org.resteasy.ResourceMethod;
import org.resteasy.specimpl.HttpHeadersImpl;
import org.resteasy.specimpl.MultivaluedMapImpl;
import org.resteasy.specimpl.UriInfoImpl;
import org.resteasy.spi.HttpInput;
import org.resteasy.spi.HttpOutput;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.HttpHeaderNames;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServletDispatcher extends HttpServlet {
    private ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
    private Registry registry = new Registry(providerFactory);

    public void init(ServletConfig servletConfig) throws ServletException {
    }

    public ResteasyProviderFactory getProviderFactory() {
        return providerFactory;
    }

    public Registry getRegistry() {
        return registry;
    }

    protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        service(httpServletRequest.getMethod(), httpServletRequest, httpServletResponse);
    }

    /**
     * wrapper around service so we can test easily
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    public void invoke(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        service(httpServletRequest, httpServletResponse);
    }

    public void service(String httpMethod, HttpServletRequest request, HttpServletResponse response) {
        HttpHeaders headers = extractHttpHeaders(request);
        MultivaluedMapImpl<String, String> parameters = extractParameters(request);
        String path = request.getPathInfo();


        ResourceMethod invoker = registry.getResourceInvoker(httpMethod, path, headers.getMediaType(), headers.getAcceptableMediaTypes());
        if (invoker == null) {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        if (!invoker.getHttpMethods().contains(httpMethod)) {
            try {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }


        HttpInput in;
        HttpOutput out;
        try {
            in = new HttpServletInputMessage(headers, request.getInputStream(), new UriInfoImpl(path), parameters);
            out = new HttpServletOutputMessage(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
            invoker.invoke(in, out);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
            this.log("Failed REST request", e);
            return;
        }
        for (String header : out.getOutputHeaders().keySet()) {
            response.setHeader(header, out.getOutputHeaders().getFirst(header));

        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public static MultivaluedMapImpl<String, String> extractParameters(HttpServletRequest request) {
        MultivaluedMapImpl<String, String> parameters = new MultivaluedMapImpl<String, String>();

        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = (String) parameterNames.nextElement();
            for (String parameterValue : request.getParameterValues(parameterName)) {
                parameters.add(parameterName, parameterValue);
            }
        }
        return parameters;
    }

    public static HttpHeaders extractHttpHeaders(HttpServletRequest request) {
        HttpHeadersImpl headers = new HttpHeadersImpl();

        MultivaluedMapImpl<String, String> requestHeaders = extractRequestHeaders(request);
        headers.setRequestHeaders(requestHeaders);
        List<MediaType> acceptableMediaTypes = extractAccepts(requestHeaders);
        headers.setAcceptableMediaTypes(acceptableMediaTypes);
        headers.setLanguage(requestHeaders.getFirst(HttpHeaderNames.CONTENT_LANGUAGE));

        String contentType = request.getContentType();
        if (contentType != null) headers.setMediaType(MediaType.parse(contentType));

        List<javax.ws.rs.core.Cookie> cookies = extractCookies(request);
        headers.setCookies(cookies);
        return headers;

    }

    private static List<javax.ws.rs.core.Cookie> extractCookies(HttpServletRequest request) {
        List<javax.ws.rs.core.Cookie> cookies = new ArrayList<javax.ws.rs.core.Cookie>();
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                cookies.add(new javax.ws.rs.core.Cookie(cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getDomain(), cookie.getVersion()));

            }
        }
        return cookies;
    }

    public static List<MediaType> extractAccepts(MultivaluedMapImpl<String, String> requestHeaders) {
        List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
        List<String> accepts = requestHeaders.get(HttpHeaderNames.ACCEPT);
        if (accepts == null) return acceptableMediaTypes;

        for (String accept : accepts) {
            acceptableMediaTypes.add(MediaType.parse(accept));
        }
        return acceptableMediaTypes;
    }

    public static MultivaluedMapImpl<String, String> extractRequestHeaders(HttpServletRequest request) {
        MultivaluedMapImpl<String, String> requestHeaders = new MultivaluedMapImpl<String, String>();

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            Enumeration headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = (String) headerValues.nextElement();
                requestHeaders.add(headerName, headerValue);
            }
        }
        return requestHeaders;
    }
}
