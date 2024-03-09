package org.jboss.resteasy.test.interceptor.gzip.resource;

import java.util.Optional;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.core.HttpHeaders;

import org.jboss.logging.Logger;

@RequestScoped
public class GzipResource implements GzipInterface {

    private static Logger log = Logger.getLogger(GzipResource.class);

    @Inject
    HttpHeaders headers;

    @Inject
    ServletContext servletContext;

    public String process(String message) {
        log.info("echo " + message + " via GzipResource");

        Optional<String> oEncoding = headers.getRequestHeader("Accept-Encoding").stream().findFirst();
        String encoding = "gzip_in_request_header_no";
        if (oEncoding.isPresent()) {
            encoding = oEncoding.get().contains("gzip") ? "gzip_in_request_header_yes" : encoding;
        }

        final String value = servletContext.getInitParameter("resteasy.allowGzip");
        return message + " ___ -Dresteasy.allowGzip=" + value + " ___ " + encoding;
    }

}
