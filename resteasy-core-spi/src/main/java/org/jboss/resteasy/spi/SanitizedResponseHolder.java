package org.jboss.resteasy.spi;

import jakarta.ws.rs.core.Response;

/**
 * Holder for sanitized response.
 *
 * @author Nicolas NESMON
 */
public interface SanitizedResponseHolder {

   /**
    * Returns the sanitized response.
    *
    * @return the sanitized response;
    */
    Response getSanitizedResponse();
}
