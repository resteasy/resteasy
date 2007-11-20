package org.resteasy;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ParameterExtractor {
    Object extract(HttpInputMessage request);
}
