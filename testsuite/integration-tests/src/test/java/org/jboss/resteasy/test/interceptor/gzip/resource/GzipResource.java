package org.jboss.resteasy.test.interceptor.gzip.resource;

import java.util.Optional;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;

import org.jboss.logging.Logger;

public class GzipResource implements GzipInterface {

    private static Logger log = Logger.getLogger(GzipResource.class);

    @Context
    HttpHeaders headers;

    public String process(String message) {
        log.info("echo " + message + " via GzipResource");

        Optional<String> oEncoding = headers.getRequestHeader("Accept-Encoding").stream().findFirst();
        String encoding = "gzip_in_request_header_no";
        if (oEncoding.isPresent()) {
            encoding = oEncoding.get().contains("gzip") ? "gzip_in_request_header_yes" : encoding;
        }

        return message + " ___ -Dresteasy.allowGzip=" + System.getProperty("resteasy.allowGzip", "null") + " ___ " + encoding;
    }

}
